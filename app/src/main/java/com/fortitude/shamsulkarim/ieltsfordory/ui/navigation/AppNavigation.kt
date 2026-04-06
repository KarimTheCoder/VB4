package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.fortitude.shamsulkarim.ieltsfordory.data_old.TrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.HomeScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.HomeViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.result.ResultScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.session.SessionScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.pretrain.PretrainScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.profile.ProfileScreen
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.words.UnifiedWordsScreen
import org.koin.androidx.compose.koinViewModel

private const val ANIMATION_DURATION = 300

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // Hoist the HomeViewModel here so the bottom bar can observe loading state
    // without being recreated on every screen transition.
    val homeViewModel: HomeViewModel = koinViewModel()
    val uiState by homeViewModel.uiState.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar =
        currentDestination?.hasRoute(HomeRoute::class) == true ||
        currentDestination?.hasRoute(WordsRoute::class) == true ||
        currentDestination?.hasRoute(ProfileRoute::class) == true

    // A single outer Scaffold renders ONE BottomNavigationBar that lives completely
    // outside the NavHost — it is never part of any screen transition animation,
    // which eliminates the "two overlapping bars" flicker.
    //
    // contentWindowInsets = WindowInsets(0): the outer Scaffold does not auto-consume
    // any window insets. The BottomNavigationBar handles nav-bar insets itself via
    // navigationBarsPadding(). consumeWindowInsets(innerPadding) on the NavHost then
    // signals to every inner Scaffold that the bottom insets are already accounted for,
    // so they never add a redundant second layer of navigation-bar padding.
    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(tween(ANIMATION_DURATION)),
                exit = fadeOut(tween(ANIMATION_DURATION))
            ) {
                BottomNavigationBar(
                    navController = navController,
                    isLoading = uiState.isLoading,
                    onStartClick = {
                        if (homeViewModel.prepareSession()) {
                            navController.navigate(SessionRoute)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {

            composable<HomeRoute>(
                enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
                exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) },
                popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
                popExitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
            ) {
                HomeScreen(viewModel = homeViewModel)
            }

            composable<WordsRoute>(
                enterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
                exitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) },
                popEnterTransition = { fadeIn(animationSpec = tween(ANIMATION_DURATION)) },
                popExitTransition = { fadeOut(animationSpec = tween(ANIMATION_DURATION)) }
            ) {
                UnifiedWordsScreen(
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
                ProfileScreen()
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
}
