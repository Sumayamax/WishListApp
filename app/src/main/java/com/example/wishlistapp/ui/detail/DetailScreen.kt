package com.example.wishlistapp.ui.detail

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wishlistapp.R
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.AccentLavender
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    // Senior Level: Use collectAsState() to observe StateFlow reactively
    val state by viewModel.state.collectAsState()
    val wish = state.wish

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.details), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (wish != null) {
                        IconButton(onClick = { onEdit(wish.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        // Use Crossfade for smooth transitions between Loading and Content
        Crossfade(
            targetState = state.isLoading,
            modifier = Modifier.padding(padding),
            label = "DetailContentAnimation"
        ) { isLoading ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryPink)
                }
            } else if (wish != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WishImage(wish.imageUrl, wish.type)

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = wish.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (wish.price != null && wish.type == WishType.THING) {
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", wish.price)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    StatusTag(wish.status)

                    Spacer(modifier = Modifier.height(32.dp))

                    if (wish.description.isNotEmpty()) {
                        DescriptionSection(wish.description)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    ActionButtons(
                        status = wish.status,
                        onToggle = viewModel::onToggleStatus,
                        onDelete = { viewModel.onDeleteWish { onBack() } }
                    )
                }
            }
        }
    }
}

@Composable
private fun WishImage(imageUrl: String, type: WishType) {
    if (imageUrl.isNotBlank()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Wish Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(24.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(if (type == WishType.THING) LightPink else AccentLavender),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (type == WishType.THING) Icons.Outlined.ShoppingCart else Icons.Outlined.Star,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (type == WishType.THING) PrimaryPink else Color(0xFF9B81FF)
            )
        }
    }
}

@Composable
private fun StatusTag(status: WishStatus) {
    Surface(
        shape = CircleShape,
        color = if (status == WishStatus.COMPLETED) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = stringResource(status.resId),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (status == WishStatus.COMPLETED) MaterialTheme.colorScheme.primary 
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DescriptionSection(description: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.description),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun ActionButtons(
    status: WishStatus,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Button(
            onClick = onToggle,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (status == WishStatus.COMPLETED) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primary,
                contentColor = if (status == WishStatus.COMPLETED) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = if (status == WishStatus.COMPLETED) stringResource(R.string.mark_as_active) 
                       else stringResource(R.string.mark_as_completed),
                fontWeight = FontWeight.Bold
            )
        }
        
        TextButton(
            onClick = onDelete,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
        ) {
            Text(stringResource(R.string.delete_wish), color = MaterialTheme.colorScheme.error)
        }
    }
}
