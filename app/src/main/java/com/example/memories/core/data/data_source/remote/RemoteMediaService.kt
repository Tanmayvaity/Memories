package com.example.memories.core.data.data_source.remote

import com.example.memories.BuildConfig
import com.example.memories.core.domain.model.RemoteImageMediaResponse
import com.example.memories.core.domain.model.RemoteVideoMediaResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface RemoteMediaService {

    @GET("curated/")
    suspend fun getRemoteImageMediaResponse(
        @Query("page") page : Int,
        @Query("per_page") perPage: Int = RemoteMediaService.DEFAULT_PER_PAGE
    ) : RemoteImageMediaResponse


    @GET("videos/popular")
    suspend fun getRemoteVideoMediaResponse(
        @Query("page") page : Int,
        @Query("per_page") perPage: Int = 30,
        @Query("min_duration") minDuration : Int = RemoteMediaService.MIN_VIDEO_DURATION,
        @Query("max_duration") maxDuration : Int = RemoteMediaService.MAX_VIDEO_DURATION,
    ) : RemoteVideoMediaResponse


    companion object {
        const val DEFAULT_PER_PAGE = 30
        const val MIN_VIDEO_DURATION = 15
        const val MAX_VIDEO_DURATION = 30
    }
}