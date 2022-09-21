package com.example.kotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    val state: StateFlow<UiState>
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val changeText = actionStateFlow
            .filterIsInstance<UiAction.Message>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Message("onStart UiAction.Message")) }

        state = changeText
            .map {
                UiState(
                    text = it.message
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = UiState()
            )



        accept = { action ->
            viewModelScope.launch {
                actionStateFlow.emit(action)
            }
        }
    }
}

sealed interface UiAction {
    data class Message(val message: String) : UiAction
}

data class UiState(
    val text: String = "initial UiState"
)