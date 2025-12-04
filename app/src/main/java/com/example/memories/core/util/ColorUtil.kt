package com.example.memories.core.util


import androidx.compose.ui.graphics.Color
import kotlin.random.Random
import kotlin.math.max

object ColorUtils {

    // --------------------------------------
    // BASIC LIGHT + DARK COLORS
    // --------------------------------------

    // Very bright colors (light mode backgrounds)
    fun randomLightColor(): Color {
        val r = (180..255).random()
        val g = (180..255).random()
        val b = (180..255).random()
        return Color(r, g, b)
    }

    // Very dark colors (dark mode backgrounds)
    fun randomDarkColor(): Color {
        val r = (0..80).random()
        val g = (0..80).random()
        val b = (0..80).random()
        return Color(r, g, b)
    }

    // --------------------------------------
    // SATURATED LIGHT COLORS (pastel-ish)
    // --------------------------------------

    fun randomLightColorSaturated(): Color {
        val base = (150..255).random()     // bright
        val accent = (120..255).random()   // adds saturation

        val r = max(base, accent)
        val g = base
        val b = accent

        return Color(r, g, b)
    }

    // --------------------------------------
    // SATURATED DARK COLORS (deep rich tones)
    // --------------------------------------

    fun randomDarkColorSaturated(): Color {
        val base = (20..80).random()
        val accent = (40..120).random()

        val r = max(base, accent)
        val g = base
        val b = accent

        return Color(r, g, b)
    }

    // --------------------------------------
    // HIGH-QUALITY HSL Pastel Color (best for light mode)
    // --------------------------------------

    fun randomPastelColor(): Color {
        val hue = Random.nextFloat() * 360f
        val saturation = 0.4f
        val lightness = 0.85f
        return Color.hsl(hue, saturation, lightness)
    }

    // --------------------------------------
    // HIGH-QUALITY HSL Dark Saturated Color (best for dark mode)
    // --------------------------------------

    fun randomDarkHslColor(): Color {
        val hue = Random.nextFloat() * 360f
        val saturation = 0.65f
        val lightness = 0.25f
        return Color.hsl(hue, saturation, lightness)
    }
}
