package com.longkongjun.paging3bp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.longkongjun.paging3bp.data.local.keys.RemoteKeysEntity

@Dao
interface RemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemoteKeys(remoteKeys: List<RemoteKeysEntity>)

    @Query("SELECT * FROM character_remote_keys WHERE characterId = :id")
    suspend fun remoteKeysCharacterId(id: Long): RemoteKeysEntity?

    @Query("DELETE FROM character_remote_keys")
    suspend fun clearRemoteKeys()
}
