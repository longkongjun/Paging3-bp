package com.longkongjun.paging3bp.data.remote.api

import com.longkongjun.paging3bp.data.remote.model.CharacterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int
    ): CharacterResponse

    companion object {
        const val BASE_URL = "https://rickandmortyapi.com/api/"
    }
}
