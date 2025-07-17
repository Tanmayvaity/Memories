package com.example.memories.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.example.memories.core.presentation.Type
import com.example.memories.core.presentation.UriType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object CustomNavType {
    val uriWrapperType = object : NavType<UriType>(
        isNullableAllowed = false
    ){
        override fun get(
            bundle: Bundle,
            key: String
        ): UriType? {
            return Json.Default.decodeFromString(bundle.getString(key)?:return null)
        }

        override fun parseValue(value: String): UriType {
            return Json.Default.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: UriType): String {
            return Uri.encode(Json.Default.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: UriType
        ) {
            bundle.putString(key, Json.Default.encodeToString(value))
        }

    }

    val mediaType = object : NavType<Type>(
        isNullableAllowed =  false
    ){
        override fun get(
            bundle: Bundle,
            key: String
        ): Type? {
            return Json.Default.decodeFromString(bundle.getString(key)?:return null)
        }

        override fun parseValue(value: String): Type {
            return Json.Default.decodeFromString(Uri.decode(value))
        }

        override fun serializeAsValue(value: Type): String {
            return Uri.encode(Json.Default.encodeToString(value))
        }

        override fun put(
            bundle: Bundle,
            key: String,
            value: Type
        ) {
            bundle.putString(key, Json.Default.encodeToString(value))
        }

    }


}