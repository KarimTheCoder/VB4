package com.fortitude.shamsulkarim.ieltsfordory.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.learning.home.DailyGoalProgress
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.*

private const val NAV_ANIM_DURATION = 300

// Spring spec for size and position animations (natural, responsive feel)
private val SizeSpring = spring<Dp>(
    dampingRatio = 0.8f,
    stiffness = Spring.StiffnessMediumLow
)

// Spring spec for padding (no bouncy overshoot to prevent negative padding values)
private val PaddingSpring = spring<Dp>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness = Spring.StiffnessMediumLow
)

// Spring spec for float animations (scale)
private val FloatSizeSpring = spring<Float>(
    dampingRatio = 0.8f,
    stiffness = Spring.StiffnessMediumLow
)

// Spring spec for IntSize animations (expand/shrink)
private val IntSizeSpring = spring<IntSize>(
    dampingRatio = 0.8f,
    stiffness = Spring.StiffnessMediumLow
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    dailyProgress: DailyGoalProgress = DailyGoalProgress(),
    onStartClick: () -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isOnHomeScreen = currentDestination?.hasRoute(HomeRoute::class) == true

    // Animated values for smooth transitions
    val elevation by animateDpAsState(
        targetValue = if (isOnHomeScreen) 16.dp else 8.dp,
        animationSpec = SizeSpring, label = "elevation"
    )
    val columnPadding by animateDpAsState(
        targetValue = if (isOnHomeScreen) 24.dp else 12.dp,
        animationSpec = PaddingSpring, label = "columnPadding"
    )
    val rowPadding by animateDpAsState(
        targetValue = if (isOnHomeScreen) 0.dp else 12.dp,
        animationSpec = PaddingSpring, label = "rowPadding"
    )

    // The container acts as the dashboard on Home, and just a nav bar on other screens
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(16.dp)
            .shadow(
                elevation = elevation.coerceAtLeast(0.dp),
                shape = RoundedCornerShape(32.dp),
                spotColor = DashboardBackground.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(DashboardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(columnPadding.coerceAtLeast(0.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dashboard Content (smoothly appears/disappears)
            AnimatedVisibility(
                visible = isOnHomeScreen,
                enter = expandVertically(
                    animationSpec = IntSizeSpring,
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(NAV_ANIM_DURATION)),
                exit = shrinkVertically(
                    animationSpec = IntSizeSpring,
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(NAV_ANIM_DURATION / 2))
            ) {
                Column {
                    DashboardContent(dailyProgress = dailyProgress)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Bottom Navigation Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = rowPadding.coerceAtLeast(0.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Book Icon (Words)
                NavIconItem(
                    item = bottomNavItems[0],
                    currentDestination = currentDestination,
                    navController = navController
                )
                
                // Start Session Button
                StartSessionButton(
                    isOnHomeScreen = isOnHomeScreen,
                    isLoading = isLoading,
                    onClick = {
                        if (isOnHomeScreen) {
                            onStartClick()
                        } else {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )

                // Profile Icon (Profile)
                NavIconItem(
                    item = bottomNavItems[2],
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
private fun DashboardContent(dailyProgress: DailyGoalProgress) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Top Row: Target Badge + Progress Ring
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Target Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(DashboardTargetBadgeBg)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DashboardDotNew,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "TODAY'S TARGET",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${dailyProgress.totalWordsTarget} words total",
                    color = DashboardTargetText,
                    fontSize = 12.sp
                )
            }

            // Progress Ring
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(56.dp),
                    color = DashboardBackground,
                    trackColor = DashboardTargetBadgeBg,
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
                
                val progressValue = if (dailyProgress.totalWordsTarget > 0) {
                    dailyProgress.totalWordsLearned.toFloat() / dailyProgress.totalWordsTarget
                } else {
                    0f
                }
                
                CircularProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier.size(56.dp),
                    color = DashboardDotNew,
                    trackColor = Color.Transparent,
                    strokeWidth = 4.dp,
                    strokeCap = StrokeCap.Round
                )
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(progressValue * 100).toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "MET",
                        color = DashboardTargetText,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Daily Mastery Goal",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Pills Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProgressPill(
                label = "New",
                learned = dailyProgress.newWordsLearned,
                target = dailyProgress.newWordsTarget,
                bgColor = DashboardPillNewBg,
                dotColor = DashboardDotNew,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ProgressPill(
                label = "Learn",
                learned = dailyProgress.learnWordsLearned,
                target = dailyProgress.learnWordsTarget,
                bgColor = DashboardPillLearnBg,
                dotColor = DashboardDotLearn,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ProgressPill(
                label = "Review",
                learned = dailyProgress.reviewWordsLearned,
                target = dailyProgress.reviewWordsTarget,
                bgColor = DashboardPillReviewBg,
                dotColor = DashboardDotReview,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProgressPill(
    label: String,
    learned: Int,
    target: Int,
    bgColor: Color,
    dotColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$learned/$target",
            color = dotColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun NavIconItem(
    item: BottomNavItem<Any>,
    currentDestination: androidx.navigation.NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hasRoute(item.route::class) == true
    val tintColor by animateColorAsState(
        targetValue = if (selected) Color.White else DashboardTargetText,
        animationSpec = tween(NAV_ANIM_DURATION),
        label = "navTint"
    )
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = FloatSizeSpring,
        label = "navScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(indication = null, interactionSource = interactionSource) {
                navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = tintColor,
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
private fun StartSessionButton(
    isOnHomeScreen: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val buttonPadding by animateDpAsState(
        targetValue = if (isOnHomeScreen) 24.dp else 32.dp,
        animationSpec = PaddingSpring, label = "buttonPadding"
    )
    
    Box(
        modifier = Modifier
            .height(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = DashboardButton,
                ambientColor = DashboardButton
            )
            .clip(RoundedCornerShape(28.dp))
            .background(DashboardButton)
            .clickable(
                indication = null, 
                interactionSource = interactionSource, 
                enabled = !isLoading,
                onClick = onClick
            )
            .padding(horizontal = buttonPadding.coerceAtLeast(0.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            AnimatedContent(
                targetState = isOnHomeScreen,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(NAV_ANIM_DURATION)) togetherWith
                            fadeOut(animationSpec = tween(NAV_ANIM_DURATION / 2)))
                        .using(SizeTransform(clip = false))
                },
                label = "buttonContent"
            ) { isHome ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isHome) "Start Session" else "Session",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (isHome) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}
