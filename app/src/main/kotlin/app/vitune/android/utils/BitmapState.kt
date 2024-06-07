package app.vitune.android.utils

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.vitune.android.service.PlayerService

@Composable
fun PlayerService.Binder?.collectProvidedBitmapAsState(
    key: Any = Unit
): State<Bitmap?> {
    val state = remember(this) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(this, key) {
        this@collectProvidedBitmapAsState?.setBitmapListener {
            state.value = it
        }
    }

    return state
}
