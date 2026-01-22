package com.example.memories.feature.feature_other.domain.model

import com.google.gson.annotations.SerializedName

data class GithubUserInfo(
    val id : Int,
    @SerializedName("avatar_url") val avatarUrl : String?,
    @SerializedName("html_url") val profileUrl : String?,
    val name : String,
    val bio : String?,
)