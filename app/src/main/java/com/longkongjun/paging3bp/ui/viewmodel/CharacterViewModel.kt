package com.longkongjun.paging3bp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.longkongjun.paging3bp.domain.model.Character
import com.longkongjun.paging3bp.domain.usecase.GetCharactersUseCase
import com.longkongjun.paging3bp.domain.usecase.RefreshCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class CharacterViewModel @Inject constructor(
    getCharactersUseCase: GetCharactersUseCase,
    private val refreshCharactersUseCase: RefreshCharactersUseCase
) : ViewModel() {
    val characters: Flow<PagingData<Character>> = getCharactersUseCase().cachedIn(viewModelScope)

    private val _isPartialRefreshing = MutableStateFlow(false)
    val isPartialRefreshing: StateFlow<Boolean> = _isPartialRefreshing.asStateFlow()

    private val _partialRefreshEvents = MutableSharedFlow<PartialRefreshEvent>()
    val partialRefreshEvents: SharedFlow<PartialRefreshEvent> = _partialRefreshEvents.asSharedFlow()

    fun refreshCharacters(ids: List<Long>) {
        val distinctIds = ids.distinct()
        if (distinctIds.isEmpty()) {
            viewModelScope.launch {
                _partialRefreshEvents.emit(PartialRefreshEvent.Empty)
            }
            return
        }

        viewModelScope.launch {
            _isPartialRefreshing.value = true
            try {
                refreshCharactersUseCase(distinctIds)
                _partialRefreshEvents.emit(PartialRefreshEvent.Success(distinctIds.size))
            } catch (throwable: Throwable) {
                _partialRefreshEvents.emit(PartialRefreshEvent.Failure(throwable))
            } finally {
                _isPartialRefreshing.value = false
            }
        }
    }
}

sealed interface PartialRefreshEvent {
    data class Success(val count: Int) : PartialRefreshEvent
    data class Failure(val throwable: Throwable) : PartialRefreshEvent
    data object Empty : PartialRefreshEvent
}
