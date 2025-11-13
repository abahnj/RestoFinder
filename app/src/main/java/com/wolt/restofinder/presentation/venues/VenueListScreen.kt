package com.wolt.restofinder.presentation.venues

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wolt.restofinder.R
import com.wolt.restofinder.domain.model.Venue
import com.wolt.restofinder.presentation.common.EmptyState
import com.wolt.restofinder.presentation.common.ErrorState
import com.wolt.restofinder.presentation.common.LoadingState
import com.wolt.restofinder.presentation.common.UiEvent
import com.wolt.restofinder.presentation.venues.components.VenueCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueListScreen(
    modifier: Modifier = Modifier,
    viewModel: VenueListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

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

    // Auto-scroll to top when new data is loaded
    val shouldScrollToTop by remember {
        derivedStateOf {
            uiState is VenueListUiState.Success &&
            listState.firstVisibleItemIndex > 5
        }
    }

    LaunchedEffect(uiState) {
        if (shouldScrollToTop && uiState is VenueListUiState.Success) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.venue_list_title)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
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
        Crossfade(
            targetState = uiState,
            label = "VenueListState",
            animationSpec = tween(durationMillis = 300),
            modifier = Modifier.padding(paddingValues)
        ) { state ->
            when (state) {
                is VenueListUiState.Loading -> {
                    LoadingState(
                        modifier = Modifier.testTag("VenueListLoading")
                    )
                }

                is VenueListUiState.Success -> {
                    if (state.venues.isEmpty()) {
                        EmptyState(
                            modifier = Modifier.testTag("VenueListEmpty")
                        )
                    } else {
                        VenueList(
                            venues = state.venues,
                            listState = listState,
                            onFavouriteClick = viewModel::toggleFavourite
                        )
                    }
                }

                is VenueListUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = viewModel::retry,
                        modifier = Modifier.testTag("VenueListError")
                    )
                }
            }
        }
    }
}

// MARK: - Previews

@androidx.compose.ui.tooling.preview.Preview(name = "Venue List", showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun VenueListPreview() {
    com.wolt.restofinder.presentation.theme.RestoFinderTheme {
        VenueList(
            venues = listOf(
                Venue("1", "McDonald's Helsinki", "I'm lovin' it.", "", "", false),
                Venue("2", "Noodle Story Freda", "Fresh homemade noodles", "", "", true),
                Venue("3", "Eat Poke Kamppi", null, "", "", false)
            ),
            listState = rememberLazyListState(),
            onFavouriteClick = {}
        )
    }
}

@Composable
private fun VenueList(
    venues: List<Venue>,
    listState: LazyListState,
    onFavouriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentDescription = stringResource(R.string.venue_list_content_description)

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .testTag("VenueList")
            .semantics {
                this.contentDescription = buildString {
                    append(contentDescription)
                    append(". ")
                    append(venues.size)
                    append(" ")
                    append(if (venues.size == 1) "restaurant" else "restaurants")
                }
            },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = venues,
            key = { venue -> venue.id },
            contentType = { "VenueCard" }
        ) { venue ->
            VenueCard(
                venue = venue,
                onFavouriteClick = onFavouriteClick,
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 250),
                    placementSpec = tween(durationMillis = 250),
                    fadeOutSpec = tween(durationMillis = 250)
                )
            )
        }
    }
}
