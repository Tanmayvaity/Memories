package com.example.memories.view.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType


object CustomNavType {

    val UriType = object : NavType<Uri>(
        isNullableAllowed = false
    ){
        override fun get(bundle: Bundle, key: String): Uri? {
            return bundle.getString(key)?.let { Uri.parse(it) }
        }

        override fun parseValue(value: String): Uri {
            // Navigation Compose will call this to convert the string from the route path back to a Uri
            return Uri.parse(value)
        }

        override fun serializeAsValue(value: Uri): String {
            // Navigation Compose calls this to convert the Uri to a string for the route path
            return Uri.encode(value.toString()) // Important to encode for safe URL characters
        }

        override fun put(bundle: Bundle, key: String, value: Uri) {
            bundle.putString(key, value.toString())
        }

    }
}