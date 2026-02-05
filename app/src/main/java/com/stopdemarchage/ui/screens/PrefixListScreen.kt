package com.stopdemarchage.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stopdemarchage.R
import com.stopdemarchage.data.model.Prefix
import com.stopdemarchage.viewmodel.PrefixViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrefixListScreen(
    onNavigateBack: () -> Unit,
    viewModel: PrefixViewModel = hiltViewModel()
) {
    val prefixes by viewModel.prefixes.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showLoadDefaultsDialog by remember { mutableStateOf(false) }
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    val editingPrefix by viewModel.editingPrefix.collectAsStateWithLifecycle()

    // Mode sélection multiple
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedPrefixes by remember { mutableStateOf(setOf<Long>()) }

    // Quitter le mode sélection si plus aucun préfixe sélectionné
    LaunchedEffect(selectedPrefixes) {
        if (selectedPrefixes.isEmpty() && isSelectionMode) {
            isSelectionMode = false
        }
    }

    Scaffold(
        topBar = {
            if (isSelectionMode) {
                // Barre de sélection
                TopAppBar(
                    title = { Text("${selectedPrefixes.size} sélectionné(s)") },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedPrefixes = emptySet()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Annuler")
                        }
                    },
                    actions = {
                        // Tout sélectionner / Tout désélectionner
                        IconButton(onClick = {
                            selectedPrefixes = if (selectedPrefixes.size == prefixes.size) {
                                emptySet()
                            } else {
                                prefixes.map { it.id }.toSet()
                            }
                        }) {
                            Icon(
                                if (selectedPrefixes.size == prefixes.size)
                                    Icons.Default.Deselect
                                else
                                    Icons.Default.SelectAll,
                                contentDescription = if (selectedPrefixes.size == prefixes.size)
                                    "Tout désélectionner"
                                else
                                    "Tout sélectionner"
                            )
                        }
                        // Activer tous les sélectionnés
                        IconButton(onClick = {
                            prefixes.filter { it.id in selectedPrefixes && !it.isEnabled }
                                .forEach { viewModel.togglePrefixEnabled(it) }
                        }) {
                            Icon(Icons.Default.ToggleOn, contentDescription = "Activer")
                        }
                        // Désactiver tous les sélectionnés
                        IconButton(onClick = {
                            prefixes.filter { it.id in selectedPrefixes && it.isEnabled }
                                .forEach { viewModel.togglePrefixEnabled(it) }
                        }) {
                            Icon(Icons.Default.ToggleOff, contentDescription = "Désactiver")
                        }
                        // Supprimer les sélectionnés
                        IconButton(onClick = { showDeleteSelectedDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            } else {
                // Barre normale
                TopAppBar(
                    title = { Text(stringResource(R.string.prefix_title)) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showLoadDefaultsDialog = true }) {
                            Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.prefix_load_defaults))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        floatingActionButton = {
            if (!isSelectionMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.prefix_add))
                }
            }
        }
    ) { paddingValues ->
        if (prefixes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.prefix_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = { showLoadDefaultsDialog = true }) {
                        Text(stringResource(R.string.prefix_load_defaults))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Hint pour le mode sélection
                AnimatedVisibility(visible = !isSelectionMode && prefixes.isNotEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Appui long pour sélectionner plusieurs préfixes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(prefixes, key = { it.id }) { prefix ->
                        PrefixItem(
                            prefix = prefix,
                            isSelectionMode = isSelectionMode,
                            isSelected = prefix.id in selectedPrefixes,
                            onToggleEnabled = { viewModel.togglePrefixEnabled(prefix) },
                            onEdit = { viewModel.setEditingPrefix(prefix) },
                            onDelete = { viewModel.deletePrefix(prefix) },
                            onLongClick = {
                                isSelectionMode = true
                                selectedPrefixes = selectedPrefixes + prefix.id
                            },
                            onSelectionToggle = {
                                selectedPrefixes = if (prefix.id in selectedPrefixes) {
                                    selectedPrefixes - prefix.id
                                } else {
                                    selectedPrefixes + prefix.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Dialog
    if (showAddDialog || editingPrefix != null) {
        PrefixDialog(
            prefix = editingPrefix,
            onDismiss = {
                showAddDialog = false
                viewModel.setEditingPrefix(null)
            },
            onSave = { prefixText, description ->
                if (editingPrefix != null) {
                    viewModel.updatePrefix(
                        editingPrefix!!.copy(
                            prefix = prefixText,
                            description = description
                        )
                    )
                } else {
                    viewModel.addPrefix(prefixText, description)
                }
                showAddDialog = false
                viewModel.setEditingPrefix(null)
            }
        )
    }

    // Load Defaults Dialog
    if (showLoadDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDefaultsDialog = false },
            title = { Text(stringResource(R.string.prefix_load_defaults)) },
            text = { Text("Charger les préfixes de démarchage téléphonique français par défaut ?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.loadDefaultPrefixes()
                    showLoadDefaultsDialog = false
                }) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoadDefaultsDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }

    // Delete Selected Dialog
    if (showDeleteSelectedDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteSelectedDialog = false },
            title = { Text("Supprimer ${selectedPrefixes.size} préfixe(s)") },
            text = { Text("Voulez-vous supprimer les ${selectedPrefixes.size} préfixes sélectionnés ?") },
            confirmButton = {
                TextButton(onClick = {
                    prefixes.filter { it.id in selectedPrefixes }
                        .forEach { viewModel.deletePrefix(it) }
                    selectedPrefixes = emptySet()
                    isSelectionMode = false
                    showDeleteSelectedDialog = false
                }) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteSelectedDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PrefixItem(
    prefix: Prefix,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleEnabled: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onLongClick: () -> Unit,
    onSelectionToggle: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) {
                        onSelectionToggle()
                    }
                },
                onLongClick = {
                    if (!isSelectionMode) {
                        onLongClick()
                    }
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox en mode sélection
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onSelectionToggle() }
                )
                Spacer(Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = prefix.prefix,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (prefix.description.isNotEmpty()) {
                    Text(
                        text = prefix.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Actions normales (pas en mode sélection)
            if (!isSelectionMode) {
                Switch(
                    checked = prefix.isEnabled,
                    onCheckedChange = { onToggleEnabled() }
                )

                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = stringResource(R.string.prefix_edit),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.prefix_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.prefix_delete)) },
            text = { Text("Supprimer le préfixe ${prefix.prefix} ?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.dialog_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.dialog_cancel))
                }
            }
        )
    }
}

@Composable
private fun PrefixDialog(
    prefix: Prefix?,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var prefixText by remember { mutableStateOf(prefix?.prefix ?: "") }
    var description by remember { mutableStateOf(prefix?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (prefix != null)
                    stringResource(R.string.prefix_edit)
                else
                    stringResource(R.string.prefix_add)
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = prefixText,
                    onValueChange = { prefixText = it },
                    label = { Text(stringResource(R.string.prefix_number_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.prefix_description_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(prefixText, description) },
                enabled = prefixText.isNotBlank()
            ) {
                Text(stringResource(R.string.prefix_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.prefix_cancel))
            }
        }
    )
}
