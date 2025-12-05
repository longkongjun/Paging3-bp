package com.longkongjun.paging3bp.domain.usecase

import androidx.paging.PagingData
import com.longkongjun.paging3bp.domain.model.Character
import com.longkongjun.paging3bp.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersNetworkOnlyUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    operator fun invoke(): Flow<PagingData<Character>> = repository.getCharactersNetworkOnly()
}

