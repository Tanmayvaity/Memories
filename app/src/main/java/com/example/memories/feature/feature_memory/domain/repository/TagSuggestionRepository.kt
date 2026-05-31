package com.example.memories.feature.feature_memory.domain.repository

import android.net.Uri

interface TagSuggestionRepository {
    suspend fun suggestTags(uri: Uri): List<String>
}
