package org.example.app.feature.home.presentation

import org.example.app.core.mvi.MviStore

data class HomeState(
    val greeting: String = "Hello, KMP",
)

sealed interface HomeIntent {
    data class SetGreeting(val text: String) : HomeIntent
}

sealed interface HomeEffect {
    data class ShowToast(val text: String) : HomeEffect
}

class HomeStore : MviStore<HomeState, HomeIntent, HomeEffect>(HomeState()) {
    override suspend fun handleIntent(state: HomeState, intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SetGreeting -> updateState { copy(greeting = intent.text) }
        }
    }
}
