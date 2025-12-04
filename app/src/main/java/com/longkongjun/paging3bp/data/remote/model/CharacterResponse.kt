package com.longkongjun.paging3bp.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val info: InfoDto,
    val results: List<CharacterDto>
)

@Serializable
data class InfoDto(
    val count: Int? = null,
    val pages: Int? = null,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class CharacterDto(
    val id: Long,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String,
    val origin: SimpleLocationDto = SimpleLocationDto(),
    val location: SimpleLocationDto = SimpleLocationDto(),
    @SerialName("created")
    val createdAt: String
)

@Serializable
data class SimpleLocationDto(
    val name: String = ""
)
