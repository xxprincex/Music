package app.vitune.android.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import app.vitune.core.ui.ParcelableColor
import app.vitune.providers.innertube.Innertube
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mood(
    val name: String,
    val color: ParcelableColor,
    val browseId: String?,
    val params: String?
) : Parcelable

fun Innertube.Mood.Item.toUiMood() = Mood(
    name = title,
    color = Color(stripeColor),
    browseId = endpoint.browseId,
    params = endpoint.params
)
