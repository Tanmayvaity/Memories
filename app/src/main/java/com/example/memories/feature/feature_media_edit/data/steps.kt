package com.example.memories.feature.feature_media_edit.data

import com.example.memories.feature.feature_media_edit.domain.model.ShaderStep

class OriginalStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
                return inputShader.eval(fragCoord);
            }
        """.trimIndent()
}


/**
 * A shader step whose AGSL source is produced dynamically (e.g. by
 * [com.example.memories.feature.feature_media_edit.data.ShaderComposer]), combining a filter and
 * adjustment values into a single program.
 */
class ComposedStep(override val shaderCode: String) : ShaderStep




