package com.example.memories.feature.feature_other.data.remote

import com.example.memories.feature.feature_other.domain.model.GithubUserInfo
import retrofit2.http.GET

interface GithubService {

    @GET("users/Tanmayvaity")
    suspend fun getUserInfo() : GithubUserInfo
}