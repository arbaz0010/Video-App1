package org.example.app.feature.videoupload.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.app.core.mvi.CollectSideEffects
import org.example.app.core.platform.FilePickerHelper
import org.example.app.feature.videoupload.presentation.VideoUploadEffect
import org.example.app.feature.videoupload.presentation.VideoUploadIntent
import org.example.app.feature.videoupload.presentation.VideoUploadStore
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoUploadScreen(
    onUploadComplete: () -> Unit,
    onBack: () -> Unit,
    store: VideoUploadStore = koinInject()
) {
    val state by store.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val filePicker = remember { FilePickerHelper() }
    
    CollectSideEffects(store.effects) { effect ->
        when (effect) {
            is VideoUploadEffect.UploadSuccess -> {
                snackbarHostState.showSnackbar("ویڈیو اپلوڈ ہو گئی")
                onUploadComplete()
            }
            is VideoUploadEffect.ShowError -> {
                snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ویڈیو اپلوڈ کریں") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // عنوان
            OutlinedTextField(
                value = state.title,
                onValueChange = { store.dispatch(VideoUploadIntent.UpdateTitle(it)) },
                label = { Text("عنوان") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )
            
            // تفصیل
            OutlinedTextField(
                value = state.description,
                onValueChange = { store.dispatch(VideoUploadIntent.UpdateDescription(it)) },
                label = { Text("تفصیل") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading
            )
            
            // فائل سلیکشن
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.selectedFilePath ?: "کوئی فائل منتخب نہیں",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // Desktop کے لیے
                            kotlinx.coroutines.MainScope().kotlinx.coroutines.launch {
                                val path = filePicker.pickVideoFile()
                                if (path != null) {
                                    store.dispatch(VideoUploadIntent.FileSelected(path))
                                }
                            }
                        },
                        enabled = !state.isUploading
                    ) {
                        Text("ویڈیو منتخب کریں")
                    }
                }
            }
            
            // اپلوڈ بٹن
            Button(
                onClick = { store.dispatch(VideoUploadIntent.UploadVideo) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isUploading && state.selectedFilePath != null
            ) {
                if (state.isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (state.isUploading) "اپلوڈ ہو رہی ہے..." else "اپلوڈ کریں")
            }
            
            // پروگریس
            if (state.isUploading) {
                LinearProgressIndicator(
                    progress = { state.uploadProgress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
