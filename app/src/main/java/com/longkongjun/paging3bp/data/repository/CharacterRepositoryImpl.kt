package com.longkongjun.paging3bp.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.longkongjun.paging3bp.data.local.database.AppDatabase
import com.longkongjun.paging3bp.data.mapper.toDomain
import com.longkongjun.paging3bp.data.mapper.toEntity
import com.longkongjun.paging3bp.data.mediator.CharacterRemoteMediator
import com.longkongjun.paging3bp.data.remote.api.RickAndMortyApi
import com.longkongjun.paging3bp.data.remote.paging.NetworkCharacterPagingSource
import com.longkongjun.paging3bp.domain.model.Character
import com.longkongjun.paging3bp.domain.repository.CharacterRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalPagingApi::class)
@Singleton
class CharacterRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val api: RickAndMortyApi
) : CharacterRepository {

    private val networkOverrides = MutableStateFlow<Map<Long, Character>>(emptyMap())
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val networkPagerShared = Pager(
        config = PagingConfig(
            pageSize = NETWORK_PAGE_SIZE,
            initialLoadSize = NETWORK_PAGE_SIZE,
            enablePlaceholders = false
        ),
        pagingSourceFactory = { NetworkCharacterPagingSource(api) }
    ).flow.shareIn(
        scope = repositoryScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        replay = 1
    )

    override fun getCharacters(): Flow<PagingData<Character>> {
        val pagingSourceFactory = { database.characterDao().pagingSource() }
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                initialLoadSize = NETWORK_PAGE_SIZE * 2,
                enablePlaceholders = false
            ),
            remoteMediator = CharacterRemoteMediator(api, database),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override fun getCharactersNetworkOnly(): Flow<PagingData<Character>> {
        return networkOverrides.flatMapLatest { overrides ->
            networkPagerShared.map { pagingData ->
                pagingData.map { character ->
                    overrides[character.id] ?: character
                }
            }
        }
    }

    override suspend fun refreshCharacters(ids: List<Long>) {
        if (ids.isEmpty()) return
        val distinctIds = ids.distinct()

        val characters = mutableListOf<com.longkongjun.paging3bp.data.remote.model.CharacterDto>()
        distinctIds.forEach { id ->
            val dto = api.getCharacterById(id)
            characters += dto
        }

        if (characters.isEmpty()) return

        database.withTransaction {
            database.characterDao().insertCharacters(characters.map { it.toEntity() })
        }

        applyNetworkOverrides(characters.map { it.toDomain() })
    }

    private fun applyNetworkOverrides(updated: List<Character>) {
        if (updated.isEmpty()) return
        networkOverrides.update { current ->
            val mutable = current.toMutableMap()
            updated.forEach { character ->
                mutable[character.id] = character
            }
            mutable
        }
    }

    private companion object {
        const val NETWORK_PAGE_SIZE = 20
    }
}
