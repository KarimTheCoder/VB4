package com.fortitude.shamsulkarim.ieltsfordory.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fortitude.shamsulkarim.ieltsfordory.R
import com.fortitude.shamsulkarim.ieltsfordory.ui.components.LevelCard
import com.fortitude.shamsulkarim.ieltsfordory.ui.theme.Green
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.home.HomeViewModel
import com.fortitude.shamsulkarim.ieltsfordory.ui.screens.home.TrialStatus
import org.koin.androidx.compose.koinViewModel

/**
 * Home screen displaying level selection cards and trial status.
 * This is the Compose replacement for HomeFragment.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigateToPretrain: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Trial Status Header
        TrialStatusHeader(
            trialStatus = uiState.trialStatus,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Advanced Level Card
        LevelCard(
            title = stringResource(R.string.advance_title),
            percentage = uiState.advancePercentage,
            level = "advance",
            onClick = {
                viewModel.selectLevel("advance")
                onNavigateToPretrain()
            },
            modifier = Modifier.weight(1f)
        )

        // Intermediate Level Card
        LevelCard(
            title = stringResource(R.string.intermediate_title),
            percentage = uiState.intermediatePercentage,
            level = "intermediate",
            onClick = {
                viewModel.selectLevel("intermediate")
                onNavigateToPretrain()
            },
            modifier = Modifier.weight(1f)
        )

        // Beginner Level Card
        LevelCard(
            title = stringResource(R.string.beginner_title),
            percentage = uiState.beginnerPercentage,
            level = "beginner",
            onClick = {
                viewModel.selectLevel("beginner")
                onNavigateToPretrain()
            },
            modifier = Modifier.weight(1f)
        )
    }

    // Trial Ended Dialog
    if (uiState.showTrialEndedDialog) {
        TrialEndedDialog(
            onUpgrade = {
                viewModel.dismissTrialDialog()
                onNavigateToPretrain()
            },
            onContinueBasic = {
                viewModel.continueWithBasic()
            }
        )
    }
}

/**
 * Trial status header showing current subscription state.
 */
@Composable
private fun TrialStatusHeader(
    trialStatus: TrialStatus,
    modifier: Modifier = Modifier
) {
    when (trialStatus) {
        TrialStatus.Hidden -> { /* Don't show anything */ }
        TrialStatus.Active -> {
            TrialStatusRow(
                text = stringResource(R.string.trial_mode, "active"),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier
            )
        }
        TrialStatus.Ended -> {
            TrialStatusRow(
                text = stringResource(R.string.trial_mode, "ended"),
                color = MaterialTheme.colorScheme.error,
                modifier = modifier
            )
        }
        TrialStatus.Premium -> {
            TrialStatusRow(
                text = "PREMIUM+",
                color = Green,
                modifier = modifier
            )
        }
    }
}

/**
 * Row displaying trial status with icon.
 */
@Composable
private fun TrialStatusRow(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Dialog shown when trial has ended.
 */
@Composable
private fun TrialEndedDialog(
    onUpgrade: () -> Unit,
    onContinueBasic: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onContinueBasic,
        title = {
            Text(text = stringResource(R.string.trial_ended))
        },
        text = {
            Text(text = stringResource(R.string.trial_ended_description))
        },
        confirmButton = {
            TextButton(onClick = onUpgrade) {
                Text(
                    text = "Upgrade",
                    color = Green
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onContinueBasic) {
                Text(
                    text = "Continue with basic",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}



