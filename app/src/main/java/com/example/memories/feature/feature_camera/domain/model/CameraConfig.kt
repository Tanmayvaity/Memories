package com.example.memories.feature.feature_camera.domain.model

enum class LensFacing{
    BACK,
    FRONT
}

enum class AspectRatio(val ratio : Float){
    RATIO_4_3(3f/4f),
    RATIO_16_9(9f/16f)
}

enum class CameraMode{
    PHOTO,
    PORTRAIT,
    VIDEO
}