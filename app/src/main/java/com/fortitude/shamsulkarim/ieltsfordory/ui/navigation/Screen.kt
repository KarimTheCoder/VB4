package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing all navigation destinations in the app.
 * 
 * @param route The unique route identifier for navigation
 * @param title Display title for the screen
 * @param icon Icon to show in bottom navigation
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Words : Screen(
        route = "words",
        title = "Words",
        icon = Icons.Default.MenuBook
    )

    data object Learned : Screen(
        route = "learned",
        title = "Learned",
        icon = Icons.Default.School
    )

    data object Favorite : Screen(
        route = "favorite",
        title = "Favorite",
        icon = Icons.Default.Favorite
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person
    )

    /**
     * Pretrain screen - not shown in bottom nav, accessed from Home screen.
     */
    data object Pretrain : Screen(
        route = "pretrain",
        title = "Start Training",
        icon = Icons.Default.School // Not used in bottom nav
    )

    companion object {
        /**
         * List of all bottom navigation items in display order.
         */
        val bottomNavItems = listOf(Home, Words, Learned, Favorite, Profile)
    }
}


