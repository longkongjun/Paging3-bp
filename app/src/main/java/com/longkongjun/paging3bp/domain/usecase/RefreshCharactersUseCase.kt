package com.longkongjun.paging3bp.domain.usecase

import com.longkongjun.paging3bp.domain.repository.CharacterRepository
import javax.inject.Inject

class RefreshCharactersUseCase @Inject constructor(
    private val repository: CharacterRepository
) {
    suspend operator fun invoke(ids: List<Long>) {
        repository.refreshCharacters(ids)
    }
}

