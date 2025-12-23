package com.example.memories.core.domain.model

import android.R.attr.type
import android.content.Context
import android.net.Uri
import kotlinx.serialization.Serializable
import java.net.URLConnection
import kotlin.text.startsWith

@Serializable
data class UriType(
    val uri : String? = null,
    val type : Type? = null
)

@Serializable
enum class Type{
    IMAGE_JPG,
    IMAGE_PNG,
    IMAGE_BMP,
    VIDEO_MP4,
    UNKNOWN_TYPE;

    fun isImageFile() : Boolean = this == IMAGE_JPG || this == IMAGE_PNG || this == IMAGE_BMP
    fun isVideoFile() = this == VIDEO_MP4

    fun isJpgFile() = this == IMAGE_JPG
    fun isUnknownType()  = this == UNKNOWN_TYPE



}
