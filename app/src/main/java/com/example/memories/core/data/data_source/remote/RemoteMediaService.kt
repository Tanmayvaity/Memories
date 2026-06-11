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
        @Query("per_page") perPage: Int = 30
    ) : RemoteImageMediaResponse


    @GET("videos/popular")
    suspend fun getRemoteVideoMediaResponse(
        @Query("page") page : Int,
        @Query("per_page") perPage: Int = 30
    ) : RemoteVideoMediaResponse
}