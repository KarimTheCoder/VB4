package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.favorites

import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.components.WordCard
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.BeginnerSecondary
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.favorites.FavoriteViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice
import org.koin.androidx.compose.koinViewModel

/**
 * Favorite Words screen displaying saved vocabulary.
 * Replaces FavoriteFragment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    
    val listState = rememberLazyListState()

    // Track scroll for FAB visibility
    val isFabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 ||
            listState.firstVisibleItemScrollOffset <= 0
        }
    }

    // Cleanup media player
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            viewModel.saveScrollPosition(listState.firstVisibleItemIndex)
        }
    }

    // Restore scroll position
    LaunchedEffect(Unit) {
        val position = viewModel.getSavedScrollPosition()
        if (position > 0) {
            listState.scrollToItem(position)
        }
    }

    // Save scroll position on scroll
    LaunchedEffect(listState.firstVisibleItemIndex) {
        viewModel.saveScrollPosition(listState.firstVisibleItemIndex)
    }

    Scaffold(
        floatingActionButton = {
            if (uiState.words.isNotEmpty()) {
                AnimatedVisibility(
                    visible = isFabVisible,
                    enter = slideInVertically(initialOffsetY = { it * 2 }),
                    exit = slideOutVertically(targetOffsetY = { it * 2 })
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (uiState.canStartPractice) {
                                viewModel.startPractice()
                                context.startActivity(Intent(context, Practice::class.java))
                            } else {
                                Toast.makeText(
                                    context,
                                    "At least five words needed",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Practice favorites",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top bar
            TopAppBar(
                title = {
                    Text(
                        text = "FAVORITE",
                        color = BeginnerSecondary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Search bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search favorites...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )
            }

            // Content
            if (uiState.filteredWords.isEmpty() && !uiState.isLoading) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotEmpty())
                            "No favorites matching \"${uiState.searchQuery}\""
                        else
                            "No favorite words yet.\nTap the heart icon on any word to add it here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.filteredWords,
                        key = { it.word }
                    ) { word ->
                        WordCard(
                            word = word,
                            onFavoriteClick = { viewModel.removeFromFavorites(word) },
                            onSpeakerClick = {
                                viewModel.speakWord(word) { audioPath ->
                                    mediaPlayer?.release()
                                    mediaPlayer = MediaPlayer().apply {
                                        setDataSource(audioPath)
                                        prepare()
                                        start()
                                    }
                                }
                            },
                            isAudioLoading = uiState.loadingAudioForWord == word.word
                        )
                    }
                }
            }
        }
    }
}



