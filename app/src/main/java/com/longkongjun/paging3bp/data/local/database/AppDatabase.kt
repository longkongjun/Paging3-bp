package com.longkongjun.paging3bp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.longkongjun.paging3bp.data.local.dao.CharacterDao
import com.longkongjun.paging3bp.data.local.dao.RemoteKeysDao
import com.longkongjun.paging3bp.data.local.entity.CharacterEntity
import com.longkongjun.paging3bp.data.local.keys.RemoteKeysEntity

@Database(
    entities = [CharacterEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
