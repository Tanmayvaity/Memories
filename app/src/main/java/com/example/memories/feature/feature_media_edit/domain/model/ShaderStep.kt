package com.example.memories.feature.feature_media_edit.domain.model

/**
 * A single editing step expressed as AGSL fragment-shader source. Implementations simply expose
 * the shader code (see `OriginalStep` / `ComposedStep`); the actual `RuntimeShader` construction
 * and `inputShader`/`resolution` binding happens at the render sites (the editor preview and
 * `MediaManager.applyFilter`).
 */
interface ShaderStep {
    val shaderCode: String
}
