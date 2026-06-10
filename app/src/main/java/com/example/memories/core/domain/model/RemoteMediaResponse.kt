package com.example.memories.core.domain.model

import com.google.gson.annotations.SerializedName

data class RemoteImageMediaResponse(
    val photos: List<Photo>,
    val page : Int,
    @SerializedName("prev_page") val prevPage : String?,
    @SerializedName("next_page") val nextPage : String?
)
data class RemoteVideoMediaResponse(
    val videos: List<Video>,
    val page : Int,
    @SerializedName("prev_page") val prevPage : String?,
    @SerializedName("next_page") val nextPage : String?
)

data class Photo(
    val id: Long,
    val width: Long,
    val height: Long,
    val url: String,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("photographer_id") val photographerId: Long,
    @SerializedName("avg_color") val avgColor: String,
    val alt : String,
    val src : Src
)

data class Video(
    val id: Long,
    val width: Long,
    val height: Long,
    val url: String,
    val image: String,
    val duration: Long,
    val user : User
)

data class User(
    val id  : Long,
    val name : String,
    @SerializedName("url") val profileUrl : String,
)


data class Src(
    val portrait : String
)