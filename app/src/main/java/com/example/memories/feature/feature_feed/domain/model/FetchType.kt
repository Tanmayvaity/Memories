package com.example.memories.feature.feature_feed.domain.model

enum class FetchType{
    ALL,
    FAVORITE,
    HIDDEN
}


fun FetchType.toIndex():Int {
    when(this){
        FetchType.ALL -> return 0
        FetchType.FAVORITE -> return 1
        FetchType.HIDDEN -> return 2
    }
}
