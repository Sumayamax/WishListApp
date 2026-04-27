package com.example.wishlistapp.ui.add_edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.wishlistapp.domain.model.WishCategory
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.ui.theme.LightPink
import com.example.wishlistapp.ui.theme.PrimaryPink
import com.example.wishlistapp.ui.theme.TextGray
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    viewModel: AddEditViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state = viewModel.state.value
    var showDatePicker by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onImageUrlChanged(it.toString())
        }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.title.isEmpty()) "Add Wish" else "Edit Wish", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Preview and Selector
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(LightPink)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (state.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = state.imageUrl,
                        contentDescription = "Wish Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Add, 
                            contentDescription = null, 
                            tint = PrimaryPink, 
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Choose Photo", color = PrimaryPink, fontWeight = FontWeight.Medium)
                    }
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.isTitleError,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            // Target Date Picker
            OutlinedTextField(
                value = state.targetDate,
                onValueChange = { viewModel.onTargetDateChanged(it) },
                label = { Text("When do you want this?") },
                placeholder = { Text("e.g. Summer 2026 or pick a date") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = PrimaryPink)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPink,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Text("Type", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WishType.entries.forEach { type ->
                    val isSelected = state.type == type
                    Button(
                        onClick = { viewModel.onTypeChanged(type) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) PrimaryPink else LightPink,
                            contentColor = if (isSelected) Color.White else TextGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }

            Text("Category", fontWeight = FontWeight.SemiBold)
            CategoryDropdown(
                selectedCategory = state.category,
                onCategorySelected = { viewModel.onCategoryChanged(it) }
            )

            if (state.type == WishType.THING) {
                OutlinedTextField(
                    value = state.price,
                    onValueChange = { viewModel.onPriceChanged(it) },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryPink,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.onSaveWish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Wish", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
                        viewModel.onTargetDateChanged(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = PrimaryPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextGray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    selectedCategory: WishCategory,
    onCategorySelected: (WishCategory) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCategory.displayName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryPink,
                unfocusedBorderColor = Color.LightGray
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WishCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}
