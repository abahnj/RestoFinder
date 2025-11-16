package com.wolt.restofinder.presentation.venues

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wolt.restofinder.R
import com.wolt.restofinder.presentation.common.AnimatedLocationDisplay
import com.wolt.restofinder.presentation.common.EmptyState
import com.wolt.restofinder.presentation.common.ErrorState
import com.wolt.restofinder.presentation.common.LoadingState
import com.wolt.restofinder.presentation.common.UiEvent
import com.wolt.restofinder.presentation.common.toAddress
import com.wolt.restofinder.presentation.venues.components.VenueCard
import com.wolt.restofinder.presentation.venues.components.VenueList
import com.wolt.restofinder.presentation.venues.components.VenueListTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueListScreen(
    modifier: Modifier = Modifier,
    viewModel: VenueListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLocation by viewModel.currentLocationFlow.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed && event.onAction != null) {
                        event.onAction.invoke()
                    }
                }
            }
        }
    }

    // Auto-scroll to top when location changes
    LaunchedEffect(currentLocation) {
        if (currentLocation != null && listState.firstVisibleItemIndex > 5) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            VenueListTopAppBar(
                userName = "Abah",
                onSearchClick = {  },
                onCartClick = {  },
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.testTag("VenueListSnackbar")
            )
        },
        modifier = modifier
            .testTag("VenueListScreen")
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedLocationDisplay(
                address = currentLocation?.toAddress() ?: "Discovering restaurants nearby...",
                coordinates = currentLocation?.let { it.latitude to it.longitude } ?: (0.0 to 0.0),
                isAnimating = currentLocation != null,
                locationKey = currentLocation ?: Unit,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Section header
            Text(
                text = "Showing places near you",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                lineHeight = 25.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content area
            when (val state = uiState) {
                is VenueListUiState.Loading -> {
                    LoadingState(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("VenueListLoading")
                    )
                }

                is VenueListUiState.Success -> {
                    if (state.venues.isEmpty()) {
                        EmptyState(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("VenueListEmpty")
                        )
                    } else {
                        val onFavouriteClick = remember { viewModel::toggleFavourite }
                        VenueList(
                            venues = state.venues,
                            listState = listState,
                            onFavouriteClick = onFavouriteClick,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                is VenueListUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = viewModel::retry,
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("VenueListError")
                    )
                }
            }
        }
    }
}
