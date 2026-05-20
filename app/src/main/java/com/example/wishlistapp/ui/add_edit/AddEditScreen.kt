package com.example.wishlistapp.ui.add_edit

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.wishlistapp.R
import com.example.wishlistapp.data.remote.dto.ProductDto
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
        uri?.let { viewModel.onImageUrlChanged(it.toString()) }
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            AddEditTopBar(
                isEditing = state.title.isNotEmpty(),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ImagePreviewSection(
                imageUrl = state.imageUrl,
                onImageClick = { galleryLauncher.launch("image/*") }
            )

            OutlinedTextField(
                value = state.imageUrl,
                onValueChange = { viewModel.onImageUrlChanged(it) },
                label = { Text(stringResource(R.string.paste_image_url)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = defaultTextFieldColors()
            )

            TitleSearchSection(
                title = state.title,
                isError = state.isTitleError,
                isSearching = state.isSearching,
                suggestions = state.suggestions,
                onTitleChange = viewModel::onTitleChanged,
                onSuggestionSelected = viewModel::onSuggestionSelected
            )

            PriceField(
                price = state.price,
                onPriceChange = viewModel::onPriceChanged
            )

            TargetDateField(
                date = state.targetDate,
                onPickDateClick = { showDatePicker = true }
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text(stringResource(R.string.description_optional)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                shape = RoundedCornerShape(16.dp),
                colors = defaultTextFieldColors()
            )

            TypeSelectionSection(
                selectedType = state.type,
                onTypeSelected = viewModel::onTypeChanged
            )

            CategorySection(
                selectedCategory = state.category,
                onCategorySelected = viewModel::onCategoryChanged
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.onSaveWish() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPink),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.save_wish), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    if (showDatePicker) {
        WishDatePickerDialog(
            onDateSaved = viewModel::onTargetDateChanged,
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTopBar(isEditing: Boolean, onBack: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditing) stringResource(R.string.edit_wish) else stringResource(R.string.add_wish),
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
private fun ImagePreviewSection(imageUrl: String, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onImageClick() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl.isNotBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Wish Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = PrimaryPink,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.choose_photo),
                    color = PrimaryPink,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun TitleSearchSection(
    title: String,
    isError: Boolean,
    isSearching: Boolean,
    suggestions: List<ProductDto>,
    onTitleChange: (String) -> Unit,
    onSuggestionSelected: (ProductDto) -> Unit
) {
    Column {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(stringResource(R.string.title)) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                if (isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            },
            colors = defaultTextFieldColors()
        )

        AnimatedVisibility(visible = suggestions.isNotEmpty()) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    stringResource(R.string.suggestions),
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryPink,
                    modifier = Modifier.padding(start = 4.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(suggestions) { product ->
                        SearchSuggestionCard(product) { onSuggestionSelected(product) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceField(price: String, onPriceChange: (String) -> Unit) {
    OutlinedTextField(
        value = price,
        onValueChange = onPriceChange,
        label = { Text(stringResource(R.string.price)) },
        prefix = { Text("$ ") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        shape = RoundedCornerShape(16.dp),
        colors = defaultTextFieldColors()
    )
}

@Composable
private fun TargetDateField(date: String, onPickDateClick: () -> Unit) {
    OutlinedTextField(
        value = date,
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.target_date_label)) },
        placeholder = { Text(stringResource(R.string.target_date_placeholder)) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        trailingIcon = {
            IconButton(onClick = onPickDateClick) {
                Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = PrimaryPink)
            }
        },
        colors = defaultTextFieldColors()
    )
}

@Composable
private fun TypeSelectionSection(selectedType: WishType, onTypeSelected: (WishType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.type), fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val types = listOf(
                WishType.THING to R.string.type_thing,
                WishType.EXPERIENCE to R.string.type_experience
            )
            types.forEach { (type, resId) ->
                val isSelected = selectedType == type
                Button(
                    onClick = { onTypeSelected(type) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) PrimaryPink else LightPink,
                        contentColor = if (isSelected) Color.White else TextGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(resId))
                }
            }
        }
    }
}

@Composable
private fun CategorySection(selectedCategory: WishCategory, onCategorySelected: (WishCategory) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(stringResource(R.string.category), fontWeight = FontWeight.SemiBold)
        CategoryDropdown(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WishDatePickerDialog(onDateSaved: (String) -> Unit, onDismiss: () -> Unit) {
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(millis))
                    onDateSaved(date)
                }
                onDismiss()
            }) {
                Text(stringResource(R.string.save), color = PrimaryPink)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = TextGray)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Composable
private fun SearchSuggestionCard(product: ProductDto, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    maxLines = 1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$${product.price}",
                    fontSize = 11.sp,
                    color = PrimaryPink,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
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
            value = stringResource(selectedCategory.resId),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = defaultTextFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            WishCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(stringResource(category.resId)) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun defaultTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = PrimaryPink,
    unfocusedBorderColor = Color.LightGray
)
