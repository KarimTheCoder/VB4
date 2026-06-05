package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.VocabularyTheme
import org.koin.androidx.compose.koinViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsComposeViewModel = koinViewModel(),
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as Activity

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

    SettingsScreenContent(
        uiState = uiState,
        wordsPerSessionPosition = wordsPerSessionPosition,
        repetitionsPerSessionPosition = repetitionsPerSessionPosition,
        onNavigateBack = onNavigateBack,
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
    wordsPerSessionPosition: Int,
    repetitionsPerSessionPosition: Int,
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
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
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
                SettingsSection(title = stringResource(id = R.string.settings_account)) {
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
                                    Text(
                                        if (uiState.isSignedIn) stringResource(id = R.string.settings_sign_out)
                                        else stringResource(id = R.string.sign_in)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Sound & Pronunciation Section
            SettingsSection(title = stringResource(id = R.string.settings_audio)) {
                SettingsToggleRow(
                    title = stringResource(id = R.string.settings_sound_effects),
                    subtitle = stringResource(id = R.string.settings_sound_effects_desc),
                    checked = uiState.soundEnabled,
                    onCheckedChange = onSoundCheckedChange
                )
                SettingsToggleRow(
                    title = stringResource(id = R.string.settings_voice_pronunciation),
                    subtitle = stringResource(id = R.string.settings_voice_pronunciation_desc),
                    checked = uiState.pronunciationEnabled,
                    onCheckedChange = onPronunciationCheckedChange
                )
            }

            // Training Options Section
            SettingsSection(title = stringResource(id = R.string.settings_training)) {
                val sessionOptions = stringArrayResource(id = R.array.settings_session_options).toList()
                SettingsDropdownRow(
                    title = stringResource(id = R.string.settings_words_per_session),
                    options = sessionOptions,
                    selectedIndex = wordsPerSessionPosition,
                    onSelect = onWordsPerSessionSelect
                )
                SettingsDropdownRow(
                    title = stringResource(id = R.string.settings_repetitions_per_session),
                    options = sessionOptions,
                    selectedIndex = repetitionsPerSessionPosition,
                    onSelect = onRepetitionsPerSessionSelect
                )
            }

            // Vocabulary Filters Section
            SettingsSection(title = stringResource(id = R.string.settings_vocabulary_filters)) {
                Text(
                    text = stringResource(id = R.string.settings_select_categories),
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
            SettingsSection(title = stringResource(id = R.string.settings_language)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_spanish_translations),
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
                            text = if (uiState.isSpanishEnabled) stringResource(id = R.string.settings_espanol) else stringResource(id = R.string.settings_english),
                            color = if (uiState.isSpanishEnabled)
                                Color.White
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Theme Section
            SettingsSection(title = stringResource(id = R.string.settings_appearance)) {
                val themeOptions = stringArrayResource(id = R.array.settings_theme_options).toList()
                SettingsDropdownRow(
                    title = stringResource(id = R.string.settings_theme),
                    options = themeOptions,
                    selectedIndex = uiState.darkModeIndex,
                    onSelect = onDarkModeSelect
                )
            }

            // Other Section
            SettingsSection(title = stringResource(id = R.string.settings_other)) {
                val context = LocalContext.current
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                SettingsComposeViewModel.PRIVACY_POLICY_URL.toUri()
                            )
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.settings_privacy_policy),
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
            onDarkModeSelect = {}
        )
    }
}
