package com.example.memories.feature.feature_other.domain.repository

import com.example.memories.feature.feature_other.domain.model.GithubUserInfo

interface RemoteUserService {
    suspend fun getUserInfo() : GithubUserInfo
}