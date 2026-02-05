package com.stopdemarchage.ui.screens

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stopdemarchage.R
import com.stopdemarchage.viewmodel.HistoryViewModel
import com.stopdemarchage.viewmodel.PrefixViewModel
import com.stopdemarchage.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToPrefixes: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onRequestScreeningRole: () -> Unit,
    prefixViewModel: PrefixViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val enabledPrefixCount by prefixViewModel.enabledPrefixCount.collectAsStateWithLifecycle()
    val todayCount by historyViewModel.todayCount.collectAsStateWithLifecycle()
    val weekCount by historyViewModel.weekCount.collectAsStateWithLifecycle()
    val monthCount by historyViewModel.monthCount.collectAsStateWithLifecycle()
    val isBlockingEnabled by settingsViewModel.isBlockingEnabled.collectAsStateWithLifecycle()

    var isScreeningServiceEnabled by remember { mutableStateOf(isCallScreeningRoleHeld(context)) }
    var permissionsStatus by remember { mutableStateOf(checkPermissionsStatus(context)) }

    LaunchedEffect(Unit) {
        isScreeningServiceEnabled = isCallScreeningRoleHeld(context)
        permissionsStatus = checkPermissionsStatus(context)
    }

    val isFullyConfigured = isBlockingEnabled && isScreeningServiceEnabled && permissionsStatus.allGranted

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isFullyConfigured)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = if (isFullyConfigured)
                            Icons.Default.Shield
                        else
                            Icons.Default.ShieldMoon,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = if (isFullyConfigured)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.error
                    )
                    Column {
                        Text(
                            text = if (isFullyConfigured)
                                stringResource(R.string.home_protection_active)
                            else
                                stringResource(R.string.home_protection_inactive),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$enabledPrefixCount ${stringResource(R.string.home_active_prefixes)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Permissions Card
            if (!permissionsStatus.allGranted) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Permissions manquantes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            PermissionStatusRow("Telephone", permissionsStatus.phoneState)
                            PermissionStatusRow("Journal d'appels", permissionsStatus.callLog)
                            PermissionStatusRow("Repondre aux appels", permissionsStatus.answerCalls)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                PermissionStatusRow("Notifications", permissionsStatus.notifications)
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Ouvrir les parametres")
                        }
                    }
                }
            }

            // Setup Card
            if (!isScreeningServiceEnabled) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.PhoneCallback,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = stringResource(R.string.home_setup_required),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = stringResource(R.string.home_setup_description),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(
                            onClick = onRequestScreeningRole,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(stringResource(R.string.home_setup_button))
                        }
                    }
                }
            }

            // Statistics Cards
            Text(
                text = "Statistiques",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_calls_blocked_today),
                    count = todayCount,
                    icon = Icons.Default.Today
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.home_calls_blocked_week),
                    count = weekCount,
                    icon = Icons.Default.DateRange
                )
            }

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.home_calls_blocked_month),
                count = monthCount,
                icon = Icons.Default.CalendarMonth
            )

            Spacer(modifier = Modifier.weight(1f))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToPrefixes,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.nav_prefixes))
                }
                OutlinedButton(
                    onClick = onNavigateToHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.History, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.nav_history))
                }
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(name: String, granted: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun isCallScreeningRoleHeld(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    } else {
        false
    }
}

private data class PermissionsStatus(
    val phoneState: Boolean,
    val callLog: Boolean,
    val answerCalls: Boolean,
    val notifications: Boolean
) {
    val allGranted: Boolean
        get() = phoneState && callLog && answerCalls &&
                (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || notifications)
}

private fun checkPermissionsStatus(context: Context): PermissionsStatus {
    return PermissionsStatus(
        phoneState = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED,
        callLog = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALL_LOG
        ) == PackageManager.PERMISSION_GRANTED,
        answerCalls = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ANSWER_PHONE_CALLS
        ) == PackageManager.PERMISSION_GRANTED,
        notifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    )
}
