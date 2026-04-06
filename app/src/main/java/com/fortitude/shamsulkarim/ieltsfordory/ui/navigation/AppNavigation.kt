package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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


