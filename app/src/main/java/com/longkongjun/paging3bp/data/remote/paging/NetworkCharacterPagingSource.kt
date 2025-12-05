package com.longkongjun.paging3bp.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.longkongjun.paging3bp.data.mapper.toDomain
import com.longkongjun.paging3bp.data.remote.api.RickAndMortyApi
import com.longkongjun.paging3bp.domain.model.Character
import retrofit2.HttpException
import java.io.IOException

class NetworkCharacterPagingSource(
    private val api: RickAndMortyApi
) : PagingSource<Int, Character>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val response = api.getCharacters(page)
            val characters = response.results.map { it.toDomain() }
            val endOfPaginationReached = characters.isEmpty() || response.info.next == null

            LoadResult.Page(
                data = characters,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (endOfPaginationReached) null else page + 1
            )
        } catch (ioException: IOException) {
            LoadResult.Error(ioException)
        } catch (httpException: HttpException) {
            LoadResult.Error(httpException)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    private companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}

