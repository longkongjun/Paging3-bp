package com.longkongjun.paging3bp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.longkongjun.paging3bp.data.local.database.AppDatabase
import com.longkongjun.paging3bp.data.mapper.toDomain
import com.longkongjun.paging3bp.data.mediator.CharacterRemoteMediator
import com.longkongjun.paging3bp.data.remote.api.RickAndMortyApi
import com.longkongjun.paging3bp.domain.model.Character
import com.longkongjun.paging3bp.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val api: RickAndMortyApi
) : CharacterRepository {

    override fun getCharacters(): Flow<PagingData<Character>> {
        val pagingSourceFactory = { database.characterDao().pagingSource() }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                initialLoadSize = NETWORK_PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(api, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    private companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
