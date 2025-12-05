package com.longkongjun.paging3bp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.longkongjun.paging3bp.domain.model.Character
import com.longkongjun.paging3bp.domain.usecase.GetCharactersNetworkOnlyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class NetworkOnlyViewModel @Inject constructor(
    getCharactersNetworkOnlyUseCase: GetCharactersNetworkOnlyUseCase
) : ViewModel() {

    val characters: Flow<PagingData<Character>> =
        getCharactersNetworkOnlyUseCase().cachedIn(viewModelScope)
}
