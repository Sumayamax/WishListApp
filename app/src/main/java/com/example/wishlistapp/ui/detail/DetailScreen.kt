package com.example.wishlistapp.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.AccentLavender
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import com.example.wishlistapp.ui.theme.TextGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val state = viewModel.state.value
    val wish = state.wish

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold, color = TextGray) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (wish != null) {
                        IconButton(onClick = { onEdit(wish.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        if (wish != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (wish.type == WishType.THING) LightPink else AccentLavender),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (wish.type == WishType.THING) Icons.Outlined.ShoppingCart else Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (wish.type == WishType.THING) PrimaryPink else Color(0xFF9B81FF)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = wish.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextGray
                )

                if (wish.price != null && wish.type == WishType.THING) {
                    Text(
                        text = "$${wish.price}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryPink,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    shape = CircleShape,
                    color = if (wish.status == WishStatus.COMPLETED) PrimaryPink.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.2f),
                ) {
                    Text(
                        text = wish.status.name,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (wish.status == WishStatus.COMPLETED) PrimaryPink else TextGray.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (wish.description.isNotEmpty()) {
                    Text(
                        text = "Description",
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = wish.description,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 16.sp,
                        color = TextGray.copy(alpha = 0.7f),
                        lineHeight = 24.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onToggleStatus() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (wish.status == WishStatus.COMPLETED) Color.LightGray.copy(alpha = 0.2f) else PrimaryPink,
                        contentColor = if (wish.status == WishStatus.COMPLETED) TextGray else Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Outlined.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (wish.status == WishStatus.COMPLETED) "Mark as Active" else "Mark as Completed",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                TextButton(
                    onClick = { viewModel.onDeleteWish { onBack() } },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Delete Wish", color = Color.Red.copy(alpha = 0.6f))
                }
            }
        } else if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryPink)
            }
        }
    }
}
