package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learned

import android.content.Intent
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.components.WordCard
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Purple500
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Cyan200
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learned.LearnedViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice
import org.koin.androidx.compose.koinViewModel

/**
 * Learned Words screen displaying vocabulary that has been learned.
 * Replaces LearnedFragment.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnedScreen(
    viewModel: LearnedViewModel = koinViewModel(),
    onNavigateToPretrain: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showDropdown by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    
    val listState = rememberLazyListState()
    val levelOptions = listOf("Beginner", "Intermediate", "Advanced")

    // Track scroll direction for FAB visibility
    val isFabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 || 
            listState.firstVisibleItemScrollOffset <= 0
        }
    }

    // Cleanup media player
    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    Scaffold(
        bottomBar = bottomBar,
        floatingActionButton = {
            if (uiState.showPracticeFab) {
                AnimatedVisibility(
                    visible = isFabVisible,
                    enter = slideInVertically(initialOffsetY = { it * 2 }),
                    exit = slideOutVertically(targetOffsetY = { it * 2 })
                ) {
                    FloatingActionButton(
                        onClick = {
                            if (uiState.canStartPractice) {
                                viewModel.startPractice(uiState.selectedLevel)
                                context.startActivity(Intent(context, Practice::class.java))
                            } else {
                                Toast.makeText(
                                    context,
                                    "There must be at least five words",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Practice",
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
                        text = "LEARNED",
                        color = MaterialTheme.colorScheme.primary,
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
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search learned words...") },
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

                    if (uiState.searchQuery.isEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
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
                                        Icons.Default.ArrowDropDown,
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

            // Content
            if (uiState.filteredWords.isEmpty() && !uiState.isLoading) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (uiState.searchQuery.isNotEmpty())
                            "No learned words matching \"${uiState.searchQuery}\""
                        else
                            "You haven't learned any words yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (uiState.searchQuery.isEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.setLevelForTraining()
                                onNavigateToPretrain()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Purple500, Cyan200)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "Start Learning",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
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



