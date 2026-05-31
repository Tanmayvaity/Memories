package com.example.memories.feature.feature_memory.data.repository

import android.net.Uri
import com.example.memories.feature.feature_memory.data.data_source.ImageTagSuggester
import com.example.memories.feature.feature_memory.domain.repository.TagSuggestionRepository
import javax.inject.Inject

class TagSuggestionRepositoryImpl(
    private val imageTagSuggester: ImageTagSuggester,
) : TagSuggestionRepository {
    override suspend fun suggestTags(uri: Uri): List<String> {
        return imageTagSuggester.suggest(uri)
    }
}
