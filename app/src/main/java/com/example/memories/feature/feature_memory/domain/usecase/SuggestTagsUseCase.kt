package com.example.memories.feature.feature_memory.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_memory.domain.repository.TagSuggestionRepository


class SuggestTagsUseCase(
    private val repository: TagSuggestionRepository
) {
    suspend operator fun invoke(uri: Uri): List<String> {
        return repository.suggestTags(uri)
    }
}
