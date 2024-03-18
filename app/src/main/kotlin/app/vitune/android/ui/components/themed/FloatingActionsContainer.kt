package app.vitune.android.ui.components.themed

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.vitune.android.LocalPlayerAwareWindowInsets
import app.vitune.android.R
import app.vitune.android.utils.ScrollingInfo
import app.vitune.android.utils.scrollingInfo
import app.vitune.android.utils.smoothScrollToTop
import kotlinx.coroutines.launch

@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    lazyGridState: LazyGridState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    @DrawableRes
    icon: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) lazyGridState.scrollingInfo() else null }

    FloatingActions(
        transitionState = transitionState,
        onScrollToTop = lazyGridState::smoothScrollToTop,
        icon = icon,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    @DrawableRes
    icon: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) lazyListState.scrollingInfo() else null }

    FloatingActions(
        transitionState = transitionState,
        onScrollToTop = lazyListState::smoothScrollToTop,
        icon = icon,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@Composable
fun BoxScope.FloatingActionsContainerWithScrollToTop(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    @DrawableRes
    icon: Int? = null,
    onClick: (() -> Unit)? = null,
    windowInsets: WindowInsets = LocalPlayerAwareWindowInsets.current
) {
    val transitionState = remember {
        MutableTransitionState<ScrollingInfo?>(ScrollingInfo())
    }.apply { targetState = if (visible) scrollState.scrollingInfo() else null }

    FloatingActions(
        transitionState = transitionState,
        icon = icon,
        onClick = onClick,
        windowInsets = windowInsets,
        modifier = modifier
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BoxScope.FloatingActions(
    transitionState: MutableTransitionState<ScrollingInfo?>,
    windowInsets: WindowInsets,
    modifier: Modifier = Modifier,
    onScrollToTop: (suspend () -> Unit)? = null,
    @DrawableRes
    icon: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val transition = updateTransition(transitionState, "")

    val bottomPaddingValues = windowInsets.only(WindowInsetsSides.Bottom).asPaddingValues()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp)
            .padding(
                windowInsets
                    .only(WindowInsetsSides.End)
                    .asPaddingValues()
            )
    ) {
        onScrollToTop?.let {
            transition.AnimatedVisibility(
                visible = { it?.isScrollingDown == false && it.isFar },
                enter = slideInVertically(tween(500, if (icon == null) 0 else 100)) { it },
                exit = slideOutVertically(tween(500, 0)) { it }
            ) {
                val coroutineScope = rememberCoroutineScope()

                SecondaryButton(
                    onClick = {
                        coroutineScope.launch {
                            onScrollToTop()
                        }
                    },
                    enabled = transition.targetState?.isScrollingDown == false && transition.targetState?.isFar == true,
                    iconId = R.drawable.chevron_up,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .padding(bottomPaddingValues)
                )
            }
        }

        icon?.let {
            onClick?.let {
                transition.AnimatedVisibility(
                    visible = { it?.isScrollingDown == false },
                    enter = slideInVertically(
                        animationSpec = tween(durationMillis = 500, delayMillis = 0),
                        initialOffsetY = { it }
                    ),
                    exit = slideOutVertically(
                        animationSpec = tween(durationMillis = 500, delayMillis = 100),
                        targetOffsetY = { it }
                    )
                ) {
                    PrimaryButton(
                        icon = icon,
                        onClick = onClick,
                        enabled = transition.targetState?.isScrollingDown == false,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .padding(bottomPaddingValues)
                    )
                }
            }
        }
    }
}
