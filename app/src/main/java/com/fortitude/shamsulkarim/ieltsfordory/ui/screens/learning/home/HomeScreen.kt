package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components.AnimatedWordCard
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components.HomeTitle
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components.StreakIndicator
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components.NavigationDrawerContent
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.LightBlueBackground
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.PrimaryBlue
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.ProgressBlue
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.ProgressGreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.ProgressPink
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.ProgressTrack
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToPretrain: () -> Unit = {},
    onStartClick: () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.checkForRefresh()
    }
    LifecycleResumeEffect(Unit) {
        // Refresh if requested (e.g., after completing a session or changing settings)
        viewModel.checkForRefresh()
        onPauseOrDispose { }
    }

    HomeScreenContent(
        words = uiState.words,
        infoBannerText = uiState.infoBannerText,
        isLoading = uiState.isLoading,
        onStartClick = {
            // Prepare session before navigating
            if (viewModel.prepareSession()) {
                onStartClick()
            }
        },
        onSkipWord = { wordId -> viewModel.onSkipWord(wordId) },
        bottomBar = bottomBar
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    words: List<WordItem> = emptyList(),
    infoBannerText: String = "You will learn 3 new words, you can skip any words you already know",
    streakDays: Int = 5,
    isLoading: Boolean = false,
    onMenuClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onStartClick: () -> Unit = {},
    onSkipWord: (String) -> Unit = {},
    bottomBar: @Composable () -> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerContent(
                onItemClick = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = bottomBar,
            topBar = {
                TopAppBar(
                    title = {
                        HomeTitle()
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open navigation menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        StreakIndicator(
                            streakDays = streakDays,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Info Banner
                InfoBanner(
                    text = infoBannerText,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Word List with loading indicator
                if (isLoading) {
                    // Show loading indicator while words are being fetched
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = PrimaryBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading words...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                } else {
                    // Word List with animations
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = words,
                            key = { it.id }  // Unique key is required for animations
                        ) { word ->
                            AnimatedWordCard(
                                word = word,
                                onSkipClick = { onSkipWord(word.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun InfoBanner(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenContentPreview() {
    val sampleWords = listOf(
        WordItem(
            id = "1",
            text = "It's raining cat's and dog's",
            progress = 1.0f,
            progressColor = ProgressBlue,
            type = "Due"
        ),
        WordItem(id = "2", text = "Bite the bullet", progress = 0.7f, progressColor = ProgressBlue, type = "Learning"),
        WordItem(
            id = "3",
            text = "The ball is in your court",
            progress = 0.4f,
            progressColor = ProgressBlue,
            type = "New"
        ),
        WordItem(
            id = "4",
            text = "Hit the nail on the head",
            progress = 0.2f,
            progressColor = ProgressPink,
            type = "Mistaken"
        ),
        WordItem(
            id = "5",
            text = "Let the cat out of the bag",
            progress = 1.0f,
            progressColor = ProgressGreen,
            type = "Learning"
        )
    )
    VocabularyTheme {
        HomeScreenContent(
            words = sampleWords,
            infoBannerText = "You will learn 3 new words, you can skip any words you already know"
        )
    }
}
