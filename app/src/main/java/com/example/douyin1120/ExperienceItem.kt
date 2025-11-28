package com.example.douyin1120

data class ExperienceItem(
    val id: Int,
    val imageUrl: String,
    val title: String,
    val avatarUrl: String,
    val username: String,
    val likeCount: Int,
    val isLiked: Boolean = false
)
