package com.example.wishlistapp.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.AccentLavender
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import com.example.wishlistapp.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddWish: () -> Unit,
    onWishClick: (Int) -> Unit,
    onStatsClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showBudgetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Wishlist", fontWeight = FontWeight.Bold, color = TextGray) },
                actions = {
                    IconButton(onClick = onStatsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Statistics", tint = TextGray)
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
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.wishes, key = { it.id }) { wish ->
                        SwipeableWishCard(
                            wish = wish,
                            onToggleStatus = { viewModel.onToggleStatus(wish) },
                            onDelete = { viewModel.onDeleteWish(wish) },
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
fun BudgetCard(totalBudget: Double, usedBudget: Double, onEditClick: () -> Unit) {
    val progress = if (totalBudget > 0) (usedBudget / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = usedBudget > totalBudget && totalBudget > 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = if (isOverBudget) Color(0xFFFFEBEE) else LightPink)
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
                        color = TextGray.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Budget", modifier = Modifier.size(20.dp), tint = PrimaryPink)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = if (isOverBudget) Color.Red.copy(alpha = 0.6f) else PrimaryPink,
                trackColor = Color.White
            )

            if (isOverBudget) {
                Text(
                    "You are over budget!", 
                    color = Color.Red.copy(alpha = 0.7f), 
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
                tint = PrimaryPink.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No wishes yet", fontWeight = FontWeight.Medium, color = TextGray.copy(alpha = 0.5f))
            Text("Start by adding your first dream!", fontSize = 14.sp, color = TextGray.copy(alpha = 0.4f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableWishCard(
    wish: WishItem,
    onToggleStatus: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.EndToStart -> {
                onDelete()
            }
            SwipeToDismissBoxValue.StartToEnd -> {
                onToggleStatus()
                dismissState.reset()
            }
            else -> {}
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color = when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Color.Green.copy(alpha = 0.2f)
                SwipeToDismissBoxValue.EndToStart -> Color.Red.copy(alpha = 0.2f)
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
            ) {
                if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Green)
                } else {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                }
            }
        },
        content = {
            WishCard(wish = wish, onToggleStatus = onToggleStatus, onClick = onClick)
        }
    )
}

@Composable
fun WishCard(wish: WishItem, onToggleStatus: () -> Unit, onClick: () -> Unit) {
    val isCompleted = wish.status == WishStatus.COMPLETED
    val alpha = if (isCompleted) 0.6f else 1f
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (wish.type == WishType.THING) LightPink else AccentLavender),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (wish.type == WishType.THING) Icons.Outlined.ShoppingCart else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (wish.type == WishType.THING) PrimaryPink else Color(0xFF9B81FF)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wish.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextGray.copy(alpha = alpha),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CategoryTag(wish.category.displayName)
                }
                
                if (wish.price != null && wish.type == WishType.THING) {
                    Text(
                        text = "$${String.format("%.2f", wish.price)}",
                        fontSize = 14.sp,
                        color = PrimaryPink.copy(alpha = alpha)
                    )
                }
            }

            IconButton(onClick = onToggleStatus) {
                Icon(
                    imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = "Toggle Status",
                    tint = if (isCompleted) PrimaryPink else TextGray.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun CategoryTag(label: String) {
    Surface(
        color = Color.LightGray.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = TextGray.copy(alpha = 0.5f)
        )
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(text.toDoubleOrNull() ?: 0.0) }) {
                Text("Save", color = PrimaryPink)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
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
                    selectedContainerColor = PrimaryPink,
                    selectedLabelColor = Color.White
                ),
                border = null,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}
