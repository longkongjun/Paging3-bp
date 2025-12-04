package com.longkongjun.paging3bp.di

import com.longkongjun.paging3bp.data.repository.CharacterRepositoryImpl
import com.longkongjun.paging3bp.domain.repository.CharacterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindCharacterRepository(
        repositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository
}
