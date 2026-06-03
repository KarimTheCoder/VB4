package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings.SettingsComposeViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import org.koin.androidx.compose.koinViewModel

/**
 * Settings screen with all app configuration options.
 * Replaces SettingActivity.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsComposeViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

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

    val wordsPerSessionPosition = remember(uiState.wordsPerSession) {
        viewModel.valueToPosition(uiState.wordsPerSession)
    }
    val repetitionsPerSessionPosition = remember(uiState.repetitionsPerSession) {
        viewModel.valueToPosition(uiState.repetitionsPerSession)
    }
    val canUseDarkMode = remember(uiState) {
        viewModel.canUseDarkMode()
    }

    SettingsScreenContent(
        uiState = uiState,
        wordsPerSessionPosition = wordsPerSessionPosition,
        repetitionsPerSessionPosition = repetitionsPerSessionPosition,
        canUseDarkMode = canUseDarkMode,
        onNavigateBack = onNavigateBack,
        onSignInClick = onSignInClick,
        onSignOutClick = onSignOutClick,
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
            when (index) {
                0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        },
        onShowUpgradeToast = {
            Toast.makeText(
                context,
                "Please upgrade to enjoy dark mode feature",
                Toast.LENGTH_SHORT
            ).show()
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreenContent(
    uiState: SettingsComposeUiState,
    wordsPerSessionPosition: Int,
    repetitionsPerSessionPosition: Int,
    canUseDarkMode: Boolean,
    onNavigateBack: () -> Unit,
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
    onShowUpgradeToast: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
                SettingsSection(title = "Account") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.userName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.userEmail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (uiState.isSignInInProgress) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        if (uiState.isSignedIn) onSignOutClick() else onSignInClick()
                                    }
                                ) {
                                    Text(if (uiState.isSignedIn) "Sign Out" else "Sign In")
                                }
                            }
                        }
                    }
                }
            }

            // Sound & Pronunciation Section
            SettingsSection(title = "Audio") {
                SettingsToggleRow(
                    title = "Sound Effects",
                    subtitle = "Play sounds during training",
                    checked = uiState.soundEnabled,
                    onCheckedChange = onSoundCheckedChange
                )
                SettingsToggleRow(
                    title = "Voice Pronunciation",
                    subtitle = "Download and play word pronunciations",
                    checked = uiState.pronunciationEnabled,
                    onCheckedChange = onPronunciationCheckedChange
                )
            }

            // Training Options Section
            SettingsSection(title = "Training") {
                SettingsDropdownRow(
                    title = "Words per Session",
                    options = listOf("25", "20", "15", "10", "5", "4", "3"),
                    selectedIndex = wordsPerSessionPosition,
                    onSelect = onWordsPerSessionSelect
                )
                SettingsDropdownRow(
                    title = "Repetitions per Session",
                    options = listOf("25", "20", "15", "10", "5", "4", "3"),
                    selectedIndex = repetitionsPerSessionPosition,
                    onSelect = onRepetitionsPerSessionSelect
                )
            }

            // Vocabulary Filters Section
            SettingsSection(title = "Vocabulary Filters") {
                Text(
                    text = "Select word categories to include in training:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip("IELTS", uiState.ieltsActive, onIeltsActiveChange)
                    FilterChip("TOEFL", uiState.toeflActive, onToeflActiveChange)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip("SAT", uiState.satActive, onSatActiveChange)
                    FilterChip("GRE", uiState.greActive, onGreActiveChange)
                }
            }

            // Language Section
            SettingsSection(title = "Language") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Spanish Translations",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = onToggleSpanish,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isSpanishEnabled)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = if (uiState.isSpanishEnabled) "Español" else "English",
                            color = if (uiState.isSpanishEnabled)
                                Color.White
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Theme Section
            SettingsSection(title = "Appearance") {
                SettingsDropdownRow(
                    title = "Theme",
                    options = listOf("Light", "Dark", "System"),
                    selectedIndex = uiState.darkModeIndex,
                    onSelect = { index ->
                        if (canUseDarkMode) {
                            onDarkModeSelect(index)
                        } else {
                            onShowUpgradeToast()
                        }
                    }
                )
            }

            // Other Section
            SettingsSection(title = "Other") {
                val context = LocalContext.current
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(SettingsComposeViewModel.PRIVACY_POLICY_URL)
                            )
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Privacy Policy",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast
                                .makeText(context, "Restore is unavailable", Toast.LENGTH_SHORT)
                                .show()
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = "Restore Purchases",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
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
            wordsPerSessionPosition = 2,
            repetitionsPerSessionPosition = 3,
            canUseDarkMode = true,
            onNavigateBack = {},
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
            onDarkModeSelect = {},
            onShowUpgradeToast = {}
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsDropdownRow(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = options.getOrElse(selectedIndex) { options.first() },
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onCheckedChange(!checked) }
            .background(
                color = if (checked) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}


