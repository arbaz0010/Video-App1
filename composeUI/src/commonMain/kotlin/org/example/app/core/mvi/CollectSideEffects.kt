package org.example.app.core.mvi

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <E> CollectSideEffects(effects: Flow<E>, onEffect: suspend (E) -> Unit) {
    LaunchedEffect(Unit) {
        effects.collect { onEffect(it) }
    }
}
