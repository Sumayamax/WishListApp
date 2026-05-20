package com.example.wishlistapp.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import com.example.wishlistapp.ui.theme.TextGray
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Progress Summary Card
                StatsSummaryCard(
                    title = stringResource(R.string.overall_progress),
                    value = "${(state.completionRate * 100).toInt()}%",
                    subtitle = stringResource(
                        R.string.wishes_achieved_format,
                        state.completedWishes,
                        state.totalWishes
                    ),
                    progress = state.completionRate
                )

                // Cost Analysis Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            stringResource(R.string.cost_analysis),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        CostRow(
                            stringResource(R.string.total_estimated),
                            "$${String.format(Locale.US, "%.2f", state.totalCost)}"
                        )
                        CostRow(
                            stringResource(R.string.total_completed_cost),
                            "$${String.format(Locale.US, "%.2f", state.completedCost)}",
                            color = PrimaryPink
                        )
                        
                        val remainingCost = state.totalCost - state.completedCost
                        CostRow(
                            stringResource(R.string.remaining_needed),
                            "$${String.format(Locale.US, "%.2f", if (remainingCost > 0) remainingCost else 0.0)}"
                        )
                    }
                }

                // Type Breakdown
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    TypeCountCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.things),
                        count = state.thingsCount,
                        color = LightPink
                    )
                    TypeCountCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.experiences),
                        count = state.experiencesCount,
                        color = Color(0xFFE8D7FF) // AccentLavender
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun StatsSummaryCard(title: String, value: String, subtitle: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightPink)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = TextGray.copy(alpha = 0.7f))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 48.sp, color = PrimaryPink)
            Text(subtitle, fontSize = 14.sp, color = TextGray.copy(alpha = 0.6f))
            
            Spacer(modifier = Modifier.height(20.dp))
            
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(CircleShape),
                color = PrimaryPink,
                trackColor = Color.White
            )
        }
    }
}

@Composable
fun CostRow(label: String, value: String, color: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, fontWeight = FontWeight.SemiBold, color = color)
    }
}

@Composable
fun TypeCountCard(modifier: Modifier, label: String, count: Int, color: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextGray)
            Text(text = label, fontSize = 12.sp, color = TextGray.copy(alpha = 0.7f))
        }
    }
}
