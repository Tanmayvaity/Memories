package com.example.memories.feature.feature_media_edit.data

import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import java.util.Locale

/**
 * Builds a single AGSL (RuntimeShader) program that bakes a [FilterType] together with a map of
 * [AdjustType] values into one fragment shader. Both the live preview and the offscreen bitmap
 * pipeline ([com.example.memories.core.data.data_source.media.MediaManager.applyFilter]) run the
 * exact same shader code, so what the user sees is what gets downloaded/shared.
 *
 * Adjustment values are inlined as constants (no runtime uniforms) which keeps the existing
 * single-`inputShader` pipeline working unchanged. The only uniform that may be emitted is
 * `resolution`, required by vignette; callers must set it when [needsResolution] is true.
 */
object ShaderComposer {

    /** True when the generated [compose] output references the `resolution` uniform. */
    fun needsResolution(adjustValues: Map<AdjustType, Float>): Boolean =
        (adjustValues[AdjustType.VIGNETTE] ?: 0f) > 0f

    fun compose(
        filterType: FilterType,
        adjustValues: Map<AdjustType, Float>
    ): String {
        val blur = adjustValues[AdjustType.BLUR] ?: 0f
        val brightness = adjustValues[AdjustType.BRIGHTNESS] ?: 0f
        val contrast = adjustValues[AdjustType.CONTRAST] ?: 0f
        val saturation = adjustValues[AdjustType.SATURATION] ?: 0f
        val temperature = adjustValues[AdjustType.TEMPERATURE] ?: 0f
        val fade = adjustValues[AdjustType.FADE] ?: 0f
        val vignette = adjustValues[AdjustType.VIGNETTE] ?: 0f

        val needsResolution = vignette > 0f

        val body = StringBuilder()

        // 1. Base colour (optionally blurred).
        if (blur > 0f) {
            body.appendLine(
                """
                half4 color = half4(0.0);
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        color += inputShader.eval(fragCoord + float2(x, y) * ${f(blur)});
                    }
                }
                color /= 9.0;
                """.trimIndent()
            )
        } else {
            body.appendLine("half4 color = inputShader.eval(fragCoord);")
        }

        // 2. Filter transform.
        body.appendLine(filterSnippet(filterType))

        // 3. Adjustment transforms.
        if (brightness != 0f) {
            val b = (brightness / 100f).coerceIn(-1f, 1f)
            body.appendLine("color.rgb += ${f(b)};")
        }
        if (contrast != 0f) {
            val factor = (1f + contrast / 100f).coerceAtLeast(0f)
            body.appendLine("color.rgb = (color.rgb - 0.5) * ${f(factor)} + 0.5;")
        }
        if (saturation != 0f) {
            val factor = (1f + saturation / 100f).coerceAtLeast(0f)
            body.appendLine("half satGray = dot(color.rgb, half3(0.299, 0.587, 0.114));")
            body.appendLine("color.rgb = mix(half3(satGray), color.rgb, ${f(factor)});")
        }
        if (temperature != 0f) {
            val t = temperature / 100f * 0.2f
            body.appendLine("color.r += ${f(t)};")
            body.appendLine("color.b -= ${f(t)};")
        }
        if (fade > 0f) {
            val amt = (fade / 100f).coerceIn(0f, 1f)
            body.appendLine("color.rgb = mix(color.rgb, color.rgb * 0.8 + 0.15, ${f(amt)});")
        }
        if (vignette > 0f) {
            val amt = (vignette / 100f).coerceIn(0f, 1f)
            body.appendLine("float2 vigUv = fragCoord / resolution;")
            body.appendLine("float vigDist = distance(vigUv, float2(0.5));")
            body.appendLine("float vig = smoothstep(0.8, 0.35, vigDist);")
            body.appendLine("color.rgb *= mix(1.0, vig, ${f(amt)});")
        }

        body.appendLine("color.rgb = clamp(color.rgb, 0.0, 1.0);")
        body.appendLine("return color;")

        val uniforms = buildString {
            appendLine("uniform shader inputShader;")
            if (needsResolution) appendLine("uniform float2 resolution;")
        }

        return """
            $uniforms
            half4 main(float2 fragCoord) {
            ${body.toString().trimEnd()}
            }
        """.trimIndent()
    }

    private fun filterSnippet(filterType: FilterType): String = when (filterType) {
        FilterType.ORIGINAL -> ""
        FilterType.GRAYSCALE ->
            "float gray = dot(color.rgb, float3(0.299, 0.587, 0.114));\n" +
                    "color = half4(half3(gray), color.a);"

        FilterType.SEPIA ->
            "float sr = dot(color.rgb, float3(0.393, 0.769, 0.189));\n" +
                    "float sg = dot(color.rgb, float3(0.349, 0.686, 0.168));\n" +
                    "float sb = dot(color.rgb, float3(0.272, 0.534, 0.131));\n" +
                    "color = half4(half(sr), half(sg), half(sb), color.a);"

        FilterType.INVERT ->
            "color = half4(1.0 - color.rgb, color.a);"

        FilterType.VINTAGE ->
            "color.rgb *= half3(1.1, 1.0, 0.9);\n" +
                    "color.rgb = (color.rgb - 0.5) * 0.9 + 0.5;"

        FilterType.COOL_FADE ->
            "color.rgb = mix(color.rgb, half3(0.7, 0.85, 1.0), 0.2);"
    }

    /** Format a float as an AGSL-safe literal (US locale → '.' decimal separator). */
    private fun f(value: Float): String = String.format(Locale.US, "%.5f", value)
}
