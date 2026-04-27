package com.example.wishlistapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.AccentLavender
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onCompletedClick) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed")
                    }
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.AutoMirrored.Outlined.List, contentDescription = "Statistics")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddWish,
                containerColor = PrimaryPink,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Wish")
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

            if (state.wishes.isEmpty()) {
                EmptyState(modifier = Modifier.weight(1f))
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    verticalItemSpacing = 12.dp,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.wishes, key = { it.id }) { wish ->
                        PinterestWishCard(
                            wish = wish,
                            onClick = { onWishClick(wish.id) }
                        )
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

@Composable
fun PinterestWishCard(wish: WishItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (wish.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(wish.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Wish Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(if (wish.type == WishType.THING) LightPink else AccentLavender),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (wish.type == WishType.THING) Icons.Outlined.ShoppingCart else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (wish.type == WishType.THING) PrimaryPink else Color(0xFF9B81FF),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = wish.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (wish.price != null && wish.type == WishType.THING) {
                    Text(
                        text = "$${String.format("%.2f", wish.price)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (wish.targetDate.isNotBlank()) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = wish.targetDate,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                CategoryTag(wish.category.displayName)
            }
        }
    }
}

@Composable
fun CategoryTag(label: String) {
    Surface(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
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
                    Text("Budget Plan", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        if (totalBudget > 0) "$${String.format("%.2f", usedBudget)} of $${String.format("%.2f", totalBudget)}" 
                        else "No budget set",
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
                    "You are over budget!", 
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
            Text("No wishes yet", fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            Text("Start by adding your first dream!", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun BudgetEditDialog(currentBudget: Double, onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var text by remember { mutableStateOf(currentBudget.toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Total Budget") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Amount") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.toDoubleOrNull() ?: 0.0) }) {
                Text("Save", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FilterRow(selectedType: String, onTypeSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("ALL", "THING", "EXPERIENCE").forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelect(type) },
                label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
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
