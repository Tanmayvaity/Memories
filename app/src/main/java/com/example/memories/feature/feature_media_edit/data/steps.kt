
@file:RequiresApi(Build.VERSION_CODES.TIRAMISU)
package com.example.memories.feature.feature_media_edit.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.core.data.data_source.graphics.ShaderStep

class OriginalStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
                return inputShader.eval(fragCoord);
            }
        """.trimIndent()
}


class GrayScaleStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            float gray = dot(color.rgb, float3(0.299, 0.587, 0.114));
            return half4(gray, gray, gray, color.a);
        }
        """.trimIndent()
}

class SepiaStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            float r = dot(color.rgb, float3(0.393, 0.769, 0.189));
            float g = dot(color.rgb, float3(0.349, 0.686, 0.168));
            float b = dot(color.rgb, float3(0.272, 0.534, 0.131));
            return half4(r, g, b, color.a);
        }
        """.trimIndent()
}

class InvertStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
            half4 color = inputShader.eval(fragCoord);
            return half4(1.0 - color.rgb, color.a);
        }
        """.trimIndent()

}

class VintageStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
            half4 c = inputShader.eval(fragCoord);
            c.rgb *= half3(1.1, 1.0, 0.9);
            c.rgb = (c.rgb - 0.5) * 0.9 + 0.5;
            return c;
        }
        """.trimIndent()
}

class CoolFadeStep : ShaderStep {
    override val shaderCode: String
        get() = """
            uniform shader inputShader;
            half4 main(float2 fragCoord) {
            half4 c = inputShader.eval(fragCoord);
            c.rgb = mix(c.rgb, half3(0.7, 0.85, 1.0), 0.2);
            return c;
        }
        """.trimIndent()
}




