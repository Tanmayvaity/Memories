package com.example.memories.core.data.data_source.graphics

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RuntimeShader
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
interface ShaderStep {
    val shaderCode : String

    fun configure(shader : RuntimeShader,source : Bitmap){
        shader.setInputBuffer("inputShader",
            BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        )
    }

}