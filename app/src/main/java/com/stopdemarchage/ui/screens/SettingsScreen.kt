package com.stopdemarchage.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stopdemarchage.BuildConfig
import com.stopdemarchage.R
import com.stopdemarchage.viewmodel.HistoryViewModel
import com.stopdemarchage.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isBlockingEnabled by settingsViewModel.isBlockingEnabled.collectAsStateWithLifecycle()
    val isNotificationsEnabled by settingsViewModel.isNotificationsEnabled.collectAsStateWithLifecycle()
    val isAutoCleanupEnabled by settingsViewModel.isAutoCleanupEnabled.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val jsonContent = inputStream?.bufferedReader()?.readText() ?: ""
                inputStream?.close()
                settingsViewModel.importPrefixes(jsonContent) { success, message ->
                    Toast.makeText(
                        context,
                        if (success) message ?: context.getString(R.string.toast_import_success)
                        else context.getString(R.string.toast_error) + ": $message",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    context.getString(R.string.toast_error) + ": ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // General Section
            SettingsSection(title = stringResource(R.string.settings_general)) {
                SwitchSettingsItem(
                    icon = Icons.Default.Shield,
                    title = stringResource(R.string.settings_enable_blocking),
                    description = stringResource(R.string.settings_enable_blocking_desc),
                    checked = isBlockingEnabled,
                    onCheckedChange = { settingsViewModel.setBlockingEnabled(it) }
                )
            }

            // Notifications Section
            SettingsSection(title = stringResource(R.string.settings_notifications)) {
                SwitchSettingsItem(
                    icon = Icons.Default.Notifications,
                    title = stringResource(R.string.settings_notify_blocked),
                    description = stringResource(R.string.settings_notify_blocked_desc),
                    checked = isNotificationsEnabled,
                    onCheckedChange = { settingsViewModel.setNotificationsEnabled(it) }
                )
            }

            // Data Section
            SettingsSection(title = stringResource(R.string.settings_data)) {
                SwitchSettingsItem(
                    icon = Icons.Default.CleaningServices,
                    title = stringResource(R.string.settings_auto_cleanup),
                    description = stringResource(R.string.settings_auto_cleanup_desc),
                    checked = isAutoCleanupEnabled,
                    onCheckedChange = {
                        settingsViewModel.setAutoCleanupEnabled(it)
                        if (it) {
                            historyViewModel.cleanupOldCalls()
                        }
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ClickableSettingsItem(
                    icon = Icons.Default.FileUpload,
                    title = stringResource(R.string.settings_export),
                    onClick = {
                        settingsViewModel.exportPrefixes(context) { success, path ->
                            Toast.makeText(
                                context,
                                if (success) "${context.getString(R.string.toast_export_success)}: $path"
                                else context.getString(R.string.toast_error),
                                Toast.LENGTH_LONG
                            ).show()

                            if (success && path != null) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/json"
                                    putExtra(Intent.EXTRA_SUBJECT, "Stop Démarchage - Préfixes")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Partager"))
                            }
                        }
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                ClickableSettingsItem(
                    icon = Icons.Default.FileDownload,
                    title = stringResource(R.string.settings_import),
                    onClick = {
                        filePickerLauncher.launch("application/json")
                    }
                )
            }

            // About Section
            SettingsSection(title = stringResource(R.string.settings_about)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.settings_version, BuildConfig.VERSION_NAME),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SwitchSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ClickableSettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
