package com.longkongjun.paging3bp.data.mapper

import com.longkongjun.paging3bp.data.local.entity.CharacterEntity
import com.longkongjun.paging3bp.data.remote.model.CharacterDto
import com.longkongjun.paging3bp.domain.model.Character

fun CharacterDto.toEntity(): CharacterEntity = CharacterEntity(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    imageUrl = image,
    originName = origin.name,
    locationName = location.name,
    createdAt = createdAt
)

fun CharacterEntity.toDomain(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    imageUrl = imageUrl,
    origin = originName,
    location = locationName,
    createdAt = createdAt
)
