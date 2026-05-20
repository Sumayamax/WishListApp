package com.example.wishlistapp.ui.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wishlistapp.R
import com.example.wishlistapp.ui.components.PinterestWishCard
import com.example.wishlistapp.ui.theme.PrimaryPink
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddWish: () -> Unit,
    onWishClick: (Int) -> Unit,
    onSettingsClick: () -> Unit,
    onStatsClick: () -> Unit,
    onCompletedClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showBudgetDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Эффект для отображения ошибок через Snackbar
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(message = it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            HomeTopBar(
                onCompletedClick = onCompletedClick,
                onStatsClick = onStatsClick,
                onSettingsClick = onSettingsClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWish,
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_wish))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            BudgetCard(
                totalBudget = state.userPreferences.totalBudget,
                usedBudget = state.usedBudget,
                onEditClick = { showBudgetDialog = true }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FilterRow(
                selectedType = state.userPreferences.filterType,
                onTypeSelect = { viewModel.onTypeFilterChanged(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Senior-level state switching with Crossfade for smooth transitions
            // Это решает проблему области видимости AnimatedVisibility внутри Box
            Crossfade(
                targetState = state,
                modifier = Modifier.weight(1f),
                label = "HomeContentAnimation"
            ) { currentState ->
                when {
                    currentState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryPink)
                        }
                    }
                    currentState.wishes.isEmpty() -> {
                        EmptyState()
                    }
                    else -> {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            verticalItemSpacing = 12.dp,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(currentState.wishes, key = { it.id }) { wish ->
                                PinterestWishCard(
                                    wish = wish,
                                    onClick = { onWishClick(wish.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBudgetDialog) {
        BudgetEditDialog(
            currentBudget = state.userPreferences.totalBudget,
            onDismiss = { showBudgetDialog = false },
            onSave = { 
                viewModel.onUpdateBudget(it)
                showBudgetDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    onCompletedClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.my_wishlist), fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = onCompletedClick) {
                Icon(Icons.Default.CheckCircle, contentDescription = stringResource(R.string.completed_wishes))
            }
            IconButton(onClick = onStatsClick) {
                Icon(Icons.AutoMirrored.Outlined.List, contentDescription = stringResource(R.string.statistics))
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
fun BudgetCard(totalBudget: Double, usedBudget: Double, onEditClick: () -> Unit) {
    val progress = if (totalBudget > 0) (usedBudget / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = usedBudget > totalBudget && totalBudget > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverBudget) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(stringResource(R.string.budget_plan), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        if (totalBudget > 0) "$${String.format(Locale.US, "%.2f", usedBudget)} of $${String.format(Locale.US, "%.2f", totalBudget)}" 
                        else stringResource(R.string.no_budget_set),
                        fontSize = 12.sp, 
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit Budget", 
                        modifier = Modifier.size(20.dp), 
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )

            if (isOverBudget) {
                Text(
                    stringResource(R.string.over_budget), 
                    color = MaterialTheme.colorScheme.error, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Outlined.FavoriteBorder, 
                contentDescription = null, 
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.no_wishes_yet), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text(stringResource(R.string.start_adding_dream), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun BudgetEditDialog(currentBudget: Double, onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var text by remember { mutableStateOf(currentBudget.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.set_total_budget)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(stringResource(R.string.amount)) },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.toDoubleOrNull() ?: 0.0) }) {
                Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun FilterRow(selectedType: String, onTypeSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val types = listOf(
            "ALL" to R.string.type_all,
            "THING" to R.string.type_thing,
            "EXPERIENCE" to R.string.type_experience
        )
        types.forEach { (type, resId) ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelect(type) },
                label = { Text(stringResource(resId)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = null,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
