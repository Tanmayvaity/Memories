package com.example.memories.core.data.data_source.graphics

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap

class ShaderPipeLine {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun process(source: Bitmap, steps: List<ShaderStep>): Bitmap {
        if (steps.isEmpty()) return source

        var current = source
        steps.forEach { step ->
            val output = createBitmap(
                current.width,
                current.height,
                current.config ?: throw NullPointerException("Bitmap config cannot be null")
            )
            val shader = RuntimeShader(step.shaderCode)
            step.configure(shader, current)

            val canvas = Canvas(output)
            val paint = Paint().apply { this.shader = shader }
            canvas.drawRect(0f, 0f, output.width.toFloat(), output.height.toFloat(), paint)

            // Avoid recycling the very first source bitmap passed in
            if (current != source) current.recycle()
            current = output
        }
        return current
    }

}