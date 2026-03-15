package com.example.memories.core.domain.model

import android.R.attr.type
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.media3.common.MimeTypes.IMAGE_BMP
import kotlinx.serialization.Serializable
import java.net.URLConnection
import kotlin.text.startsWith

@Serializable
data class UriType(
    val uri : String? = null,
    val type : Type? = null
)

@Serializable
enum class Type(val mimeType : String){
    IMAGE_JPG("image/jpeg"),
    IMAGE_PNG("image/png"),
//    IMAGE_BMP(""),
    VIDEO_MP4("video/mp4"),
    UNKNOWN_TYPE("unknown");

    fun isImageFile() : Boolean = this == IMAGE_JPG || this == IMAGE_PNG
    fun isVideoFile() = this == VIDEO_MP4

    fun isJpgFile() = this == IMAGE_JPG
    fun isUnknownType()  = this == UNKNOWN_TYPE


    companion object{
        fun fromUri(uri: Uri, context: Context? = null): Type {
            val mimeType = when (uri.scheme) {
                ContentResolver.SCHEME_CONTENT -> {
                    if(context == null){
                        throw IllegalArgumentException("Context cannot be null")
                    }
                    context!!.contentResolver.getType(uri)
                }
                ContentResolver.SCHEME_FILE -> {
                    MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                        ?.let { MimeTypeMap.getSingleton().getMimeTypeFromExtension(it) }
                }
                else -> null
            } ?: throw IllegalArgumentException("Unable to determine mime type")

            return fromMimeType(mimeType)
        }

        fun fromMimeType(mimeType: String): Type = when (mimeType) {
            "image/jpeg", "image/jpg" -> IMAGE_JPG
            "image/png" -> IMAGE_PNG
            "video/mp4" -> VIDEO_MP4
            else -> UNKNOWN_TYPE
        }

    }
}
