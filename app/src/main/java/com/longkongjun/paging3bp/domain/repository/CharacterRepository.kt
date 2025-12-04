package com.longkongjun.paging3bp.domain.repository

import androidx.paging.PagingData
import com.longkongjun.paging3bp.domain.model.Character
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    fun getCharacters(): Flow<PagingData<Character>>
}
