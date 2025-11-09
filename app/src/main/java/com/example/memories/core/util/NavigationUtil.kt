package com.example.memories.core.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

val LocalAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope> {
    error("animated visibility scope Must be provided first")
}
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope> {
    error("shared transition Scope Must be provided first")
}

@Composable
fun AnimatedVisibilityScope.ProvideAnimatedVisibilityScope(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAnimatedVisibilityScope provides this,
        content = content
    )
}

@Composable
fun WithAnimatedVisibilityScope(block: @Composable AnimatedVisibilityScope.() -> Unit) {
    with(LocalAnimatedVisibilityScope.current) {
        block()
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ProvideSharedTransitionScope(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSharedTransitionScope provides this,
        content = content
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WithSharedTransitionScope(block: @Composable SharedTransitionScope.() -> Unit) {
    with(LocalSharedTransitionScope.current) {
        block()
    }
}