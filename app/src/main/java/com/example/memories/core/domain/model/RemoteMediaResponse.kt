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
    val user : User,
    @SerializedName("video_files") val videoFiles: List<VideoFile> = emptyList()
) {
    val downloadLink: String?
        get() = videoFiles.firstOrNull { it.quality == "hd" && it.fileType == "video/mp4" }?.link
            ?: videoFiles.firstOrNull { it.fileType == "video/mp4" }?.link
            ?: videoFiles.firstOrNull()?.link
}

data class VideoFile(
    val id: Long,
    val quality: String?,
    @SerializedName("file_type") val fileType: String?,
    val width: Int?,
    val height: Int?,
    val link: String
)

data class User(
    val id  : Long,
    val name : String,
    @SerializedName("url") val profileUrl : String,
)


data class Src(
    val portrait : String
)