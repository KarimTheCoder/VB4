package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.components.WordCard
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.BeginnerSecondary
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.AllWordsViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * All Words screen displaying vocabulary list with search and level filter.
 * Replaces AllWordsFragment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllWordsScreen(
    viewModel: AllWordsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    
    val listState = rememberLazyListState()
    val levelOptions = listOf("Beginner", "Intermediate", "Advanced")

    // Cleanup media player on dispose
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    // Restore scroll position on first load
    LaunchedEffect(Unit) {
        val (position, _) = viewModel.getSavedScrollPosition()
        if (position > 0) {
            listState.scrollToItem(position)
        }
    }

    // Save scroll position on scroll
    LaunchedEffect(listState.firstVisibleItemIndex) {
        viewModel.saveScrollPosition(
            listState.firstVisibleItemIndex,
            listState.firstVisibleItemScrollOffset
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top bar with title
        TopAppBar(
            title = {
                Text(
                    text = "WORDS",
                    color = BeginnerSecondary,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        // Search bar and level selector
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search words...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search"
                                )
                            }
                        }
                    },
                    singleLine = true
                )

                // Level selector (shown when not searching)
                if (uiState.searchQuery.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            TextButton(onClick = { showDropdown = true }) {
                                Text(
                                    text = levelOptions[uiState.selectedLevel],
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Select level",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                levelOptions.forEachIndexed { index, level ->
                                    DropdownMenuItem(
                                        text = { Text(level) },
                                        onClick = {
                                            viewModel.loadWordsForLevel(index)
                                            showDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Word list
        if (uiState.filteredWords.isEmpty() && !uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (uiState.searchQuery.isNotEmpty()) 
                        "No words found matching \"${uiState.searchQuery}\"" 
                    else 
                        "No words available",
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
                        onFavoriteClick = { viewModel.toggleFavorite(word) },
                        onSpeakerClick = {
                            viewModel.speakWord(word) { audioPath ->
                                // Play the downloaded audio
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



