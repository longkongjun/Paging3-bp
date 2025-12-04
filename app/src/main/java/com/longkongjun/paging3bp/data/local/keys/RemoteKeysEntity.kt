package com.longkongjun.paging3bp.data.local.keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "character_remote_keys")
data class RemoteKeysEntity(
    @PrimaryKey val characterId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
