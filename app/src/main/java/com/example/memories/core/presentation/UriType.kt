package com.example.memories.core.presentation

import android.net.Uri
import android.util.Log
import kotlinx.serialization.Serializable
import java.net.URLConnection
import kotlin.text.startsWith

@Serializable
data class UriType(
    val uri : String? = null,
    val type : Type? = null
){
    companion object{
        fun isVideoFile(path: String?): Boolean {
            val mimeType = URLConnection.guessContentTypeFromName(path)
            return mimeType != null && mimeType.startsWith("video")
        }
        fun isImageFile(path: String?): Boolean {
            val mimeType = URLConnection.guessContentTypeFromName(path)
            return mimeType != null && mimeType.startsWith("image")
        }

        fun Uri?.mapToType(): Type{
            if(this == null) {
                throw NullPointerException("Uri is null")
            }

            return if(isVideoFile(this.toString())){
                Type.VIDEO
            }else{
                Type.IMAGE
            }
        }
    }


}

@Serializable
enum class Type{
    IMAGE,
    VIDEO
}
