package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.HomeScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.result.ResultScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session.SessionScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.pretrain.PretrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile.ProfileScreen
import com.fortitude.shamsulkarim.ieltsfordory.data_old.TrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.HomeViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.UnifiedWordsScreen
import org.koin.androidx.compose.koinViewModel

private const val ANIMATION_DURATION = 300

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Default bottomBar (for Words and Profile screens)
    val bottomBar: @Composable () -> Unit = {
        BottomNavigationBar(navController = navController)
    }

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {

        composable<HomeRoute>(
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            val homeViewModel: HomeViewModel = koinViewModel()
            val uiState by homeViewModel.uiState.collectAsState()
            
            HomeScreen(
                viewModel = homeViewModel,
                bottomBar = {
                    BottomNavigationBar(
                        navController = navController,
                        isLoading = uiState.isLoading,
                        onStartClick = {
                            if (homeViewModel.prepareSession()) {
                                navController.navigate(SessionRoute)
                            }
                        }
                    )
                },
                onStartClick = {
                    if (homeViewModel.prepareSession()) {
                        navController.navigate(SessionRoute)
                    }
                }
            )
        }
        composable<WordsRoute>(
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            UnifiedWordsScreen(
                bottomBar = bottomBar,
                onNavigateToPretrain = {
                    navController.navigate(PretrainRoute)
                }
            )
        }
        composable<ProfileRoute>(
            enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
        ) {
            ProfileScreen(
                bottomBar = bottomBar
            )
        }

        composable<PretrainRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            PretrainScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable<TrainRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            TrainScreen(
                onNavigateHome = { navController.popBackStack() },
                onTrainingComplete = { navController.popBackStack() }
            )
        }

        composable<SessionRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            SessionScreen(
                onBack = { navController.popBackStack() },
                onNavigateToResult = { navController.navigate(ResultRoute) }
            )
        }

        composable<ResultRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            ResultScreen(
                onHomeClick = {
                    navController.popBackStack(HomeRoute, inclusive = false)
                },
                onNewSessionClick = {
                    navController.popBackStack(HomeRoute, inclusive = false)
                    navController.navigate(SessionRoute)
                }
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onStartClick: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isOnHomeScreen = currentDestination?.hasRoute(HomeRoute::class) == true

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hasRoute(item.route::class) == true
            val isHome = item.route == HomeRoute

            if (isHome) {
                // Home button with blue pill background
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            if (isOnHomeScreen && !isLoading) {
                                // On home screen and loaded -> start session
                                onStartClick()
                            } else if (!isOnHomeScreen) {
                                // Not on home screen -> navigate to home
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 48.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isOnHomeScreen && isLoading -> {
                            // Loading state: show circular progress
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        }
                        isOnHomeScreen && !isLoading -> {
                            // Loaded state: show "Start" text
                            Text(
                                text = "Start",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        else -> {
                            // Not on home screen: show home icon
                            Icon(
                                imageVector = item.unselectedIcon,
                                contentDescription = item.title,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            } else {
                // Words and Profile with icon + label
                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title,
                        tint = if (selected) MaterialTheme.colorScheme.primary else Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Black
                    )
                }
            }
        }
    }
}

