package com.example.memories.feature.feature_feed.presentation.feed_detail

sealed class MemoryDetailEvents {
    data class Fetch(val id:String): MemoryDetailEvents()
    data class FavoriteToggle(val id : String,val isFavourite : Boolean) : MemoryDetailEvents()
    data class HiddenToggle(val id : String,val isHidden : Boolean) : MemoryDetailEvents()

    object Delete : MemoryDetailEvents()
}

sealed class UiEvent{
    data class ShowToast(
        val message: String,
        val type : ToastType
    ) : UiEvent()


    enum class ToastType{
        HIDDEN,
        DELETE
    }

}
