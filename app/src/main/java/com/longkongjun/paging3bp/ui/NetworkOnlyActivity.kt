package com.longkongjun.paging3bp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.longkongjun.paging3bp.R
import com.longkongjun.paging3bp.databinding.ActivityNetworkOnlyBinding
import com.longkongjun.paging3bp.ui.adapter.CharacterAdapter
import com.longkongjun.paging3bp.ui.adapter.CharacterLoadStateAdapter
import com.longkongjun.paging3bp.ui.components.VerticalSpaceItemDecoration
import com.longkongjun.paging3bp.ui.viewmodel.NetworkOnlyViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NetworkOnlyActivity : AppCompatActivity() {

    private val viewModel: NetworkOnlyViewModel by viewModels()
    private lateinit var binding: ActivityNetworkOnlyBinding
    private lateinit var headerAdapter: CharacterLoadStateAdapter
    private lateinit var footerAdapter: CharacterLoadStateAdapter

    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityNetworkOnlyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        collectPagingData()
        observeLoadStates()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.network_mode_title)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        val spacing = resources.getDimensionPixelSize(R.dimen.list_item_spacing)
        headerAdapter = CharacterLoadStateAdapter { characterAdapter.retry() }
        footerAdapter = CharacterLoadStateAdapter { characterAdapter.retry() }
        binding.recyclerCharacters.apply {
            layoutManager = LinearLayoutManager(this@NetworkOnlyActivity)
            adapter = ConcatAdapter(headerAdapter, characterAdapter, footerAdapter)
            addItemDecoration(VerticalSpaceItemDecoration(spacing))
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            characterAdapter.refresh()
        }
        binding.buttonStateAction.setOnClickListener {
            characterAdapter.retry()
        }
    }

    private fun collectPagingData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characters.collectLatest { pagingData ->
                    characterAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun observeLoadStates() {
        characterAdapter.addLoadStateListener { combinedLoadStates ->
            val refreshState = combinedLoadStates.refresh
            val isInitialLoading = refreshState is LoadState.Loading && characterAdapter.itemCount == 0
            val isError = refreshState is LoadState.Error
            val isEmpty = refreshState is LoadState.NotLoading &&
                combinedLoadStates.append.endOfPaginationReached &&
                characterAdapter.itemCount == 0

            binding.progressBar.isVisible = isInitialLoading
            binding.recyclerCharacters.isVisible = !isInitialLoading && !isEmpty && !isError

            val showStateView = isError || isEmpty
            binding.viewState.isVisible = showStateView
            binding.textState.text = when {
                isError -> getString(R.string.load_failed)
                isEmpty -> getString(R.string.empty_state_title)
                else -> ""
            }
            binding.buttonStateAction.isVisible = isError || isEmpty
            binding.buttonStateAction.text = getString(R.string.retry)

            binding.swipeRefresh.isRefreshing =
                refreshState is LoadState.Loading && characterAdapter.itemCount > 0

            headerAdapter.loadState = combinedLoadStates.prepend
            footerAdapter.loadState = combinedLoadStates.append
        }
    }
}

