package com.longkongjun.paging3bp

import android.content.Intent
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
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.longkongjun.paging3bp.databinding.ActivityMainBinding
import com.longkongjun.paging3bp.ui.adapter.CharacterAdapter
import com.longkongjun.paging3bp.ui.adapter.CharacterLoadStateAdapter
import com.longkongjun.paging3bp.ui.components.VerticalSpaceItemDecoration
import com.longkongjun.paging3bp.ui.NetworkOnlyActivity
import com.longkongjun.paging3bp.ui.viewmodel.CharacterViewModel
import com.longkongjun.paging3bp.ui.viewmodel.PartialRefreshEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: CharacterViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var headerAdapter: CharacterLoadStateAdapter
    private lateinit var footerAdapter: CharacterLoadStateAdapter

    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSwipeRefresh()
        setupPartialRefreshButton()
        setupNetworkModeButton()
        collectPagingData()
        observeLoadStates()
        observePartialRefresh()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupRecyclerView() {
        val spacing = resources.getDimensionPixelSize(R.dimen.list_item_spacing)
        headerAdapter = CharacterLoadStateAdapter { characterAdapter.retry() }
        footerAdapter = CharacterLoadStateAdapter { characterAdapter.retry() }
        binding.recyclerCharacters.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
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

    private fun setupPartialRefreshButton() {
        binding.buttonPartialRefresh.setOnClickListener {
            refreshVisibleRange()
        }
    }

    private fun setupNetworkModeButton() {
        binding.buttonNetworkMode.setOnClickListener {
            startActivity(Intent(this, NetworkOnlyActivity::class.java))
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
            val mediatorRefresh = combinedLoadStates.mediator?.refresh ?: combinedLoadStates.refresh
            val isInitialLoading = mediatorRefresh is LoadState.Loading && characterAdapter.itemCount == 0
            val isError = mediatorRefresh is LoadState.Error
            val isEmpty = mediatorRefresh is LoadState.NotLoading &&
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
                mediatorRefresh is LoadState.Loading && characterAdapter.itemCount > 0

            headerAdapter.loadState = combinedLoadStates.prepend
            footerAdapter.loadState = combinedLoadStates.append
        }
    }

    private fun observePartialRefresh() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.partialRefreshEvents.collect { event ->
                        when (event) {
                            is PartialRefreshEvent.Success -> {
                                showPartialRefreshMessage(
                                    getString(R.string.partial_refresh_success, event.count)
                                )
                            }

                            is PartialRefreshEvent.Failure -> {
                                val reason = event.throwable.message ?: getString(R.string.load_failed)
                                showPartialRefreshMessage(
                                    getString(R.string.partial_refresh_failed, reason)
                                )
                            }

                            PartialRefreshEvent.Empty -> {
                                showPartialRefreshMessage(getString(R.string.partial_refresh_no_items))
                            }
                        }
                    }
                }

                launch {
                    viewModel.isPartialRefreshing.collect { isRefreshing ->
                        binding.buttonPartialRefresh.isEnabled = !isRefreshing
                        binding.buttonPartialRefresh.text = if (isRefreshing) {
                            getString(R.string.partial_refreshing)
                        } else {
                            getString(R.string.partial_refresh)
                        }
                    }
                }
            }
        }
    }

    private fun refreshVisibleRange() {
        val layoutManager = binding.recyclerCharacters.layoutManager as? LinearLayoutManager
            ?: return
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (firstVisible == RecyclerView.NO_POSITION || lastVisible == RecyclerView.NO_POSITION) {
            showPartialRefreshMessage(getString(R.string.partial_refresh_no_items))
            return
        }

        val ids = (firstVisible..lastVisible).mapNotNull { position ->
            characterAdapter.peek(position)?.id
        }

        if (ids.isEmpty()) {
            showPartialRefreshMessage(getString(R.string.partial_refresh_no_items))
            return
        }

        viewModel.refreshCharacters(ids)
    }

    private fun showPartialRefreshMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
