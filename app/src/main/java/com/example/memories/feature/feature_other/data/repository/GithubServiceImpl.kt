package com.example.memories.feature.feature_other.data.repository

import com.example.memories.feature.feature_other.data.remote.GithubService
import com.example.memories.feature.feature_other.domain.model.GithubUserInfo
import com.example.memories.feature.feature_other.domain.repository.RemoteUserService
import javax.inject.Inject

class GithubServiceImpl @Inject constructor(
    private val service: GithubService
) : RemoteUserService {
    override suspend fun getUserInfo(): GithubUserInfo {
        return service.getUserInfo()
    }
}
