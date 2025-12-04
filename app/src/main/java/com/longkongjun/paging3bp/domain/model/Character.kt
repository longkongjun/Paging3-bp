package com.longkongjun.paging3bp.domain.model

data class Character(
    val id: Long,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val imageUrl: String,
    val origin: String,
    val location: String,
    val createdAt: String
)
