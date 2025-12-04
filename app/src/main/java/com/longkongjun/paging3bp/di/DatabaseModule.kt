package com.longkongjun.paging3bp.di

import android.content.Context
import androidx.room.Room
import com.longkongjun.paging3bp.data.local.dao.CharacterDao
import com.longkongjun.paging3bp.data.local.dao.RemoteKeysDao
import com.longkongjun.paging3bp.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "paging_best_practice.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCharacterDao(database: AppDatabase): CharacterDao = database.characterDao()

    @Provides
    fun provideRemoteKeysDao(database: AppDatabase): RemoteKeysDao = database.remoteKeysDao()
}
