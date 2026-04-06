package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.navigation.compose.currentBackStackEntryAsState

private const val NAV_ANIM_DURATION = 300

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
                HomeNavItem(
                    item = item,
                    isOnHomeScreen = isOnHomeScreen,
                    isLoading = isLoading,
                    onStartClick = onStartClick,
                    navController = navController
                )
            } else {
                RegularNavItem(
                    item = item,
                    selected = selected,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun HomeNavItem(
    item: BottomNavItem<Any>,
    isOnHomeScreen: Boolean,
    isLoading: Boolean,
    onStartClick: () -> Unit,
    navController: NavHostController
) {
    val background by animateColorAsState(
        targetValue = if (isOnHomeScreen) MaterialTheme.colorScheme.primary else Color.LightGray,
        animationSpec = tween(NAV_ANIM_DURATION),
        label = "homeBackground"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .background(background)
            .clickable(indication = null, interactionSource = interactionSource) {
                if (isOnHomeScreen && !isLoading) {
                    onStartClick()
                } else if (!isOnHomeScreen) {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
            .height(56.dp)
            .padding(horizontal = 48.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isOnHomeScreen && isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
            isOnHomeScreen -> {
                Text(
                    text = "Start",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            else -> {
                Icon(
                    imageVector = item.unselectedIcon,
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun RegularNavItem(
    item: BottomNavItem<Any>,
    selected: Boolean,
    navController: NavHostController
) {
    val tintColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else Color.Black,
        animationSpec = tween(NAV_ANIM_DURATION),
        label = "tint_${item.title}"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .clickable(indication = null, interactionSource = interactionSource) {
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
            tint = tintColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.labelSmall,
            color = tintColor
        )
    }
}
