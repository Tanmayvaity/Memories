package com.example.memories.feature.feature_feed.domain.model

enum class FetchType{
    ALL,
    FAVORITE,
    HIDDEN
}

enum class SortType{
    CreatedForDate,
    DateAdded,
    Title
}

enum class OrderByType{
    Ascending,
    Descending
}


fun FetchType.toIndex():Int {
     when(this){
        FetchType.ALL -> return 0
        FetchType.FAVORITE -> return 1
        FetchType.HIDDEN -> return 2
    }
}

fun SortType.toIndex() : Int{
    return when(this){
        SortType.CreatedForDate -> 0
        SortType.DateAdded -> 1
        SortType.Title -> 2
    }
}

fun OrderByType.toIndex() : Int {
     when(this){
        OrderByType.Ascending -> return 0
        OrderByType.Descending -> return 1
    }
}
