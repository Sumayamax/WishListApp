package com.example.wishlistapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wishlistapp.domain.model.WishCategory
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.AccentLavender
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import java.util.Locale

@Composable
fun PinterestWishCard(
    wish: WishItem, 
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            WishImage(wish)

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
                        text = "$${String.format(Locale.US, "%.2f", wish.price)}",
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
                CategoryTag(wish.category)
            }
        }
    }
}

@Composable
private fun WishImage(wish: WishItem) {
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
}

@Composable
fun CategoryTag(category: WishCategory) {
    Surface(
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = stringResource(category.resId),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}
