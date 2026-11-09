package org.example.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.example.app.feature.home.ui.HomeScreen
import org.example.app.navigation.AppRoute
import org.example.app.theme.AppTheme

@Composable
fun App() {
    AppTheme {
        var route by remember { mutableStateOf<AppRoute>(AppRoute.Home) }
        Scaffold { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) {
                when (route) {
                    AppRoute.Home -> HomeScreen()
                }
            }
        }
    }
}
