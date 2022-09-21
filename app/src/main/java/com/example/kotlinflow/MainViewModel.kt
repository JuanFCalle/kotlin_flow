package com.example.kotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    val state1: StateFlow<UiState>
    val state2: StateFlow<String>
    val accept: (UiAction) -> Unit

    init {
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search("onStart search default_query")) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Scroll("onStart scroll default_query.")) }

        state2 = searches
            .flatMapLatest {
                flow {
                    emit("1${it.query}\n")
                    delay(100)
                    emit("2${it.query}\n")
                    delay(100)
                    emit("3${it.query}\n")
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = ""
            )


        state1 = combine(
            searches,
            queriesScrolled,
            ::Pair
        )
            .map { (search, queriesScrolled) ->
                UiState(
                    query = search.query,
                    lastQueryScrolled = queriesScrolled.currentQuery
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
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
    data class Search(val query: String) : UiAction
    data class Scroll(val currentQuery: String) : UiAction
}

data class UiState(
    val query: String = "default_query",
    val lastQueryScrolled: String = "default_query"
)