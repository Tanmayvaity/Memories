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
    IMAGE,
    VIDEO
}
