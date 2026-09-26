package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.LocalExtendedColors
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

private const val ANIMATION_DURATION = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsComposeViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSaveAndNavigateHome: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

    var showDiscardDialog by remember { mutableStateOf(false) }

    // Intercept hardware / system gesture back press
    BackHandler(enabled = uiState.hasUnsavedChanges) {
        showDiscardDialog = true
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(
                    text = stringResource(id = R.string.settings_discard_title),
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.settings_discard_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.discardChanges()
                        onNavigateBack()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_discard_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false }
                ) {
                    Text(text = stringResource(id = R.string.settings_discard_dismiss))
                }
            }
        )
    }

    // Handle toast messages
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle error messages
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    SettingsScreenContent(
        uiState = uiState,
        onNavigateBack = {
            if (uiState.hasUnsavedChanges) {
                showDiscardDialog = true
            } else {
                onNavigateBack()
            }
        },
        onSaveClick = {
            viewModel.saveSettings {
                onSaveAndNavigateHome()
            }
        },
        onSignInClick = { viewModel.signIn(activity) },
        onSignOutClick = { viewModel.signOut() },
        onSoundCheckedChange = { viewModel.setSound(it) },
        onPronunciationCheckedChange = { viewModel.setPronunciation(it) },
        onWordsPerSessionSelect = { viewModel.setWordsPerSession(it) },
        onRepetitionsPerSessionSelect = { viewModel.setRepetitionsPerSession(it) },
        onIeltsActiveChange = { viewModel.setIeltsActive(it) },
        onToeflActiveChange = { viewModel.setToeflActive(it) },
        onSatActiveChange = { viewModel.setSatActive(it) },
        onGreActiveChange = { viewModel.setGreActive(it) },
        onToggleSpanish = { viewModel.toggleSpanish() },
        onDarkModeSelect = { index ->
            viewModel.setDarkMode(index)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    uiState: SettingsComposeUiState,
    onNavigateBack: () -> Unit,
    onSaveClick: () -> Unit,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onSoundCheckedChange: (Boolean) -> Unit,
    onPronunciationCheckedChange: (Boolean) -> Unit,
    onWordsPerSessionSelect: (Int) -> Unit,
    onRepetitionsPerSessionSelect: (Int) -> Unit,
    onIeltsActiveChange: (Boolean) -> Unit,
    onToeflActiveChange: (Boolean) -> Unit,
    onSatActiveChange: (Boolean) -> Unit,
    onGreActiveChange: (Boolean) -> Unit,
    onToggleSpanish: () -> Unit,
    onDarkModeSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.settings_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiState.hasUnsavedChanges,
                enter = slideInVertically(
                    initialOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeIn(animationSpec = tween(ANIMATION_DURATION)),
                exit = slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(ANIMATION_DURATION)
                ) + fadeOut(animationSpec = tween(ANIMATION_DURATION))
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Button(
                            onClick = onSaveClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.settings_save_changes),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Account Section
            if (uiState.showSignInSection) {
                SettingsSection(title = "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                shape = CircleShape,
                                color = LocalExtendedColors.current.lightBlue,
                                modifier = Modifier.size(56.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF1B8755),
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Sync,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.userName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = if (uiState.isSignedIn) "CLOUD" else "LOCAL",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (uiState.isSignedIn) uiState.userEmail else "Cloud backup inactive",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (uiState.isSignInInProgress) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            if (uiState.isSignedIn) {
                                OutlinedButton(
                                    onClick = onSignOutClick
                                ) {
                                    Text(text = stringResource(id = R.string.settings_sign_out))
                                }
                            } else {
                                Button(
                                    onClick = onSignInClick
                                ) {
                                    Text(text = stringResource(id = R.string.sign_in))
                                }
                            }
                        }
                    }
                }
            }

            // Training Options Section
            SettingsSection(title = "STUDY SESSIONS") {
                Column {
                    Text(
                            text = "New Words per Session",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val options = listOf(
                                3 to "Optimal",
                                5 to "Intense",
                                8 to "Sprint",
                                12 to "Marathon"
                            )
                            options.forEach { (value, label) ->
                                val isSelected = uiState.wordsPerSession == value
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else LocalExtendedColors.current.lightBlue,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp)
                                        .clickable { onWordsPerSessionSelect(value) }
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 12.dp)
                                    ) {
                                        Text(
                                            text = value.toString(),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Repetitions",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Spaced interval review",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = LocalExtendedColors.current.lightBlue.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = LocalExtendedColors.current.lightBlue,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(enabled = uiState.repetitionsPerSession > 1) {
                                                onRepetitionsPerSessionSelect(uiState.repetitionsPerSession - 1)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "-",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                    
                                    Text(
                                        text = uiState.repetitionsPerSession.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    
                                    Surface(
                                        shape = CircleShape,
                                        color = LocalExtendedColors.current.lightBlue,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable(enabled = uiState.repetitionsPerSession < 10) {
                                                onRepetitionsPerSessionSelect(uiState.repetitionsPerSession + 1)
                                            }
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "+",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            // Vocabulary Filters Section
            SettingsSection(title = "VOCABULARY FILTERS") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    VocabularyFilterCard(
                        title = "IELTS",
                        subtitle = "Academic & General",
                        icon = Icons.Default.School,
                        tagText = "BAND 7.5+",
                        tagColor = Color(0xFF198754),
                        checked = uiState.ieltsActive,
                        onCheckedChange = onIeltsActiveChange,
                        modifier = Modifier.weight(1f)
                    )
                    VocabularyFilterCard(
                        title = "TOEFL",
                        subtitle = "iBT Key Lexicon",
                        icon = Icons.Default.Public,
                        tagText = "100+ SCOREFOCUS",
                        tagColor = Color(0xFF198754),
                        checked = uiState.toeflActive,
                        onCheckedChange = onToeflActiveChange,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    VocabularyFilterCard(
                        title = "SAT",
                        subtitle = "Critical Reading",
                        icon = Icons.Default.MenuBook,
                        tagText = "CONTEXT MASTERY",
                        tagColor = Color(0xFFDC3545),
                        checked = uiState.satActive,
                        onCheckedChange = onSatActiveChange,
                        modifier = Modifier.weight(1f)
                    )
                    VocabularyFilterCard(
                        title = "GRE",
                        subtitle = "High-Frequency",
                        icon = Icons.Default.LocationOn,
                        tagText = "ELITE TIER",
                        tagColor = MaterialTheme.colorScheme.primary,
                        checked = uiState.greActive,
                        onCheckedChange = onGreActiveChange,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Audio & Speech Section
            SettingsSection(title = "AUDIO & SPEECH") {
                IconSettingsRow(
                    title = "Sound Effects",
                    subtitle = "Haptics & micro-alerts",
                    icon = Icons.Default.VolumeUp,
                    rightContent = {
                        Switch(checked = uiState.soundEnabled, onCheckedChange = onSoundCheckedChange)
                    }
                )
                
                androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                
                IconSettingsRow(
                    title = "Voice Pronunciation",
                    subtitle = "IPA phonetic native audio",
                    icon = Icons.Default.RecordVoiceOver,
                    rightContent = {
                        Switch(checked = uiState.pronunciationEnabled, onCheckedChange = onPronunciationCheckedChange)
                    }
                )
            }

            // Language Section
            SettingsSection(title = "LANGUAGE & INTERFACE") {
                val context = LocalContext.current
                IconSettingsRow(
                    title = "Translations",
                    subtitle = "In-card definition aid",
                    icon = Icons.Default.Translate,
                    rightContent = {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = LocalExtendedColors.current.lightBlue,
                            modifier = Modifier.clickable { onToggleSpanish() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (uiState.isSpanishEnabled) "Spanish" else "English",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(8.dp))
                
                IconSettingsRow(
                    title = "Theme",
                    subtitle = "Visual appearance",
                    icon = Icons.Default.Palette,
                    rightContent = {}
                )
                
                SegmentedThemeControl(
                    selectedIndex = uiState.darkModeIndex,
                    onSelect = onDarkModeSelect
                )
            }

            // About & Privacy Section
            SettingsSection(title = "ABOUT & PRIVACY") {
                val context = LocalContext.current
                AboutPrivacyRow(
                    title = "Privacy Policy",
                    icon = Icons.Default.Security,
                    rightContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            SettingsComposeViewModel.PRIVACY_POLICY_URL.toUri()
                        )
                        context.startActivity(intent)
                    }
                )
                
                androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                
                AboutPrivacyRow(
                    title = "Terms of Service",
                    icon = Icons.Default.Description,
                    rightContent = {
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    onClick = {
                        Toast.makeText(context, "Terms of Service not available yet", Toast.LENGTH_SHORT).show()
                    }
                )
                
                androidx.compose.material3.Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                
                AboutPrivacyRow(
                    title = "Clear Vocabulary Cache",
                    icon = Icons.Default.Delete,
                    rightContent = {
                        Text(
                            text = "14.2 MB",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    onClick = {
                        Toast.makeText(context, "Vocabulary cache cleared", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Footer
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fortitude Learn",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "VERSION 2.4.0 (BUILD 114) • Vocabulary Builder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenContentPreview() {
    VocabularyTheme {
        SettingsScreenContent(
            uiState = SettingsComposeUiState(
                isLoading = false,
                soundEnabled = true,
                pronunciationEnabled = false,
                darkModeIndex = 2,
                wordsPerSession = 15,
                repetitionsPerSession = 10,
                ieltsActive = true,
                toeflActive = false,
                satActive = true,
                greActive = false,
                isSpanishEnabled = true,
                isSignedIn = true,
                userName = "Jane Doe",
                userEmail = "jane.doe@example.com",
                showSignInSection = true
            ),
            onNavigateBack = {},
            onSaveClick = {},
            onSignInClick = {},
            onSignOutClick = {},
            onSoundCheckedChange = {},
            onPronunciationCheckedChange = {},
            onWordsPerSessionSelect = {},
            onRepetitionsPerSessionSelect = {},
            onIeltsActiveChange = {},
            onToeflActiveChange = {},
            onSatActiveChange = {},
            onGreActiveChange = {},
            onToggleSpanish = {},
            onDarkModeSelect = {}
        )
    }
}
