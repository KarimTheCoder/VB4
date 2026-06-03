package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using @Serializable objects.
 * This approach is recommended for Navigation Compose 2.8+
 */

// ========== Route Definitions ==========

@Serializable
object HomeRoute

@Serializable
object WordsRoute

@Serializable
object ProfileRoute

@Serializable
object SettingsRoute

@Serializable
object PretrainRoute

@Serializable
object TrainRoute

@Serializable
object SessionRoute

@Serializable
object ResultRoute

// ========== Bottom Navigation Items ==========

/**
 * Represents a bottom navigation item with display properties.
 * Uses filled icons when selected, outlined when not (Material Design guideline).
 */
data class BottomNavItem<T : Any>(
    val route: T,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * Bottom navigation items in display order.
 */
val bottomNavItems: List<BottomNavItem<Any>> = listOf(
    BottomNavItem(
        route = WordsRoute,
        title = "Words",
        selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
        unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
    ),
    BottomNavItem(
        route = HomeRoute,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = ProfileRoute,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
