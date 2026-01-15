package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.domain.vocabulary.model.VocabularyWord
import com.fortitude.shamsulkarim.ieltsfordory.ui.practice.Practice
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.components.EmptyStateContent
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.components.SearchTopBar
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.components.WordList
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun UnifiedWordsScreen(
    viewModel: UnifiedWordsViewModel = koinViewModel(),
    bottomBar: @Composable () -> Unit = {},
    onNavigateToPretrain: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    val isFabVisible by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 ||
            listState.firstVisibleItemScrollOffset <= 0
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
        topBar = {
            SearchTopBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery
            )
        },
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
                                viewModel.startPractice()
                                context.startActivity(Intent(context, Practice::class.java))
                            } else {
                                Toast.makeText(
                                    context,
                                    "At least 5 words needed",
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab chips
            TabChips(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::selectTab
            )

            // Pager-style horizontal slide animation
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = {
                    val animSpec = tween<IntOffset>(
                        durationMillis = 350,
                        easing = FastOutSlowInEasing
                    )
                    val direction = if (targetState.ordinal > initialState.ordinal) {
                        // Navigating right: slide in from right, slide out to left
                        slideInHorizontally(
                            animationSpec = animSpec,
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = animSpec,
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
                    } else {
                        // Navigating left: slide in from left, slide out to right
                        slideInHorizontally(
                            animationSpec = animSpec,
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = animSpec,
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
                    }
                    direction
                },
                label = "tabContent"
            ) { tab ->
                if (uiState.currentWords.isEmpty() && !uiState.isLoading) {
                    EmptyStateContent(message = uiState.emptyStateMessage)
                } else {
                    WordList(
                        words = uiState.currentWords,
                        listState = listState,
                        loadingAudioWord = uiState.loadingAudioForWord,
                        onFavoriteClick = viewModel::toggleFavorite,
                        onSpeakerClick = viewModel::speakWord
                    )
                }
            }
        }
    }
}

@Composable
private fun TabChips(
    selectedTab: WordTab,
    onTabSelected: (WordTab) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(WordTab.entries) { tab ->
            val selected = selectedTab == tab
            val scale by animateFloatAsState(
                targetValue = if (selected) 1.03f else 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
                label = "chipScale"
            )
            
            FilterChip(
                modifier = Modifier.scale(scale),
                selected = selected,
                onClick = { onTabSelected(tab) },
                leadingIcon = {
                    AnimatedVisibility(visible = selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                label = { Text(tab.title) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected,
                    borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    selectedBorderColor = Color.Transparent,
                    selectedBorderWidth = 0.dp
                )
            )
        }
    }
}

@Composable
private fun UnifiedWordsScreenContent(
    uiState: UnifiedWordsUiState,
    onSearchQueryChange: (String) -> Unit,
    onTabSelected: (WordTab) -> Unit,
    onFavoriteClick: (VocabularyWord) -> Unit,
    onSpeakerClick: (VocabularyWord) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    
    Scaffold(
        modifier = modifier,
        topBar = {
            SearchTopBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabChips(
                selectedTab = uiState.selectedTab,
                onTabSelected = onTabSelected
            )

            if (uiState.currentWords.isEmpty() && !uiState.isLoading) {
                EmptyStateContent(message = uiState.emptyStateMessage)
            } else {
                WordList(
                    words = uiState.currentWords,
                    listState = listState,
                    loadingAudioWord = uiState.loadingAudioForWord,
                    onFavoriteClick = onFavoriteClick,
                    onSpeakerClick = onSpeakerClick
                )
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true, name = "Empty State")
@Composable
private fun UnifiedWordsScreenEmptyPreview() {
    val previewState = UnifiedWordsUiState(
        selectedTab = WordTab.FAVORITES,
        searchQuery = "",
        isLoading = false,
        favoriteWords = emptyList(),
        filteredFavoriteWords = emptyList()
    )
    
    VocabularyTheme {
        UnifiedWordsScreenContent(
            uiState = previewState,
            onSearchQueryChange = {},
            onTabSelected = {},
            onFavoriteClick = {},
            onSpeakerClick = {}
        )
    }
}
