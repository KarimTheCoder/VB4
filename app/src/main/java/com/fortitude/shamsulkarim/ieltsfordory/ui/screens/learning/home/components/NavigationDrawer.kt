package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R

private val PrimaryBlue = Color(0xFF4052B5)


@Composable
 fun NavigationDrawerContent(
    onItemClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // URLs for social links
    val facebookUrl = "https://www.facebook.com/AAALanguageApp"
    val instagramUrl = "https://www.instagram.com/aaalanguageapp"
    val rateAppUrl = "https://play.google.com/store/apps/details?id=com.fortitude.shamsulkarim.ieltsfordory"

    ModalDrawerSheet {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Essential Vocabulary",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Learn English words easily",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Social Media Items
        DrawerMenuItemWithPainter(
            iconRes = R.drawable.ic_facebook,
            label = "Facebook",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
                context.startActivity(intent)
                onItemClick()
            }
        )
        DrawerMenuItemWithPainter(
            iconRes = R.drawable.ic_instagram,
            label = "Instagram",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl))
                context.startActivity(intent)
                onItemClick()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Rate App
        DrawerMenuItem(
            icon = Icons.Default.Star,
            label = "Rate App",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rateAppUrl))
                context.startActivity(intent)
                onItemClick()
            }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            onClick = onItemClick
        )
        DrawerMenuItem(
            icon = Icons.Default.Info,
            label = "About",
            onClick = onItemClick
        )
    }
}

@Composable
private fun DrawerMenuItemWithPainter(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
        },
        label = { Text(text = label) },
        selected = false,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = label) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}