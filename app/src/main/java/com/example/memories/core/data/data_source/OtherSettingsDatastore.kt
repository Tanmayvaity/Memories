package com.example.memories.core.data.data_source

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map

class OtherSettingsDatastore(
    val context : Context
) {
    companion object{
        private const val TAG = "OtherSettingsDatastore"
        private val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = "other_settings")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

     val isDarkModeEnabled = context.datastore.data.map { preferences ->
         preferences[DARK_MODE_KEY]?: false
    }


    suspend fun setDarkMode(){
        context.datastore.edit { preferences ->
            val enable = preferences[DARK_MODE_KEY] ?: false
            preferences[DARK_MODE_KEY] = !enable
            Log.d(TAG, "setDarkMode: ${!enable} ")
        }
    }

}