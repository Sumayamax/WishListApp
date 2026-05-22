package com.example.wishlistapp.ui.add_edit

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.domain.model.Product
import com.example.wishlistapp.domain.model.WishCategory
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.model.WishType
import com.example.wishlistapp.domain.repository.WishRepository
import com.example.wishlistapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: WishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(AddEditState())
    val state: State<AddEditState> = _state

    private var currentWishId: Int? = null
    private var searchJob: Job? = null

    init {
        savedStateHandle.get<Int>("wishId")?.let { wishId ->
            if (wishId != -1) {
                viewModelScope.launch {
                    repository.getWishById(wishId)?.let { wish ->
                        currentWishId = wish.id
                        _state.value = _state.value.copy(
                            title = wish.title,
                            description = wish.description,
                            type = wish.type,
                            category = wish.category,
                            price = wish.price?.toString() ?: "",
                            status = wish.status,
                            imageUrl = wish.imageUrl,
                            targetDate = wish.targetDate
                        )
                    }
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _state.value = _state.value.copy(title = title, isTitleError = false)
        searchProducts(title)
    }

    private fun searchProducts(query: String) {
        searchJob?.cancel()
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 2) {
            _state.value = _state.value.copy(suggestions = emptyList(), isSearching = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            _state.value = _state.value.copy(isSearching = true)
            
            when (val result = repository.searchProducts(trimmedQuery)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        suggestions = result.data ?: emptyList(),
                        isSearching = false
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        suggestions = emptyList(), 
                        isSearching = false
                    )
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isSearching = true)
                }
            }
        }
    }

    fun onSuggestionSelected(product: Product) {
        _state.value = _state.value.copy(
            title = product.title,
            price = product.price.toString(),
            imageUrl = product.imageUrl,
            description = product.description,
            suggestions = emptyList()
        )
    }

    fun onDescriptionChanged(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun onTypeChanged(type: WishType) {
        _state.value = _state.value.copy(type = type)
    }

    fun onCategoryChanged(category: WishCategory) {
        _state.value = _state.value.copy(category = category)
    }

    fun onPriceChanged(price: String) {
        _state.value = _state.value.copy(price = price)
    }

    fun onImageUrlChanged(imageUrl: String) {
        _state.value = _state.value.copy(imageUrl = imageUrl.trim())
    }

    fun onTargetDateChanged(date: String) {
        _state.value = _state.value.copy(targetDate = date)
    }

    fun onSaveWish() {
        if (_state.value.title.isBlank()) {
            _state.value = _state.value.copy(isTitleError = true)
            return
        }

        viewModelScope.launch {
            repository.insertWish(
                WishItem(
                    id = currentWishId ?: 0,
                    title = _state.value.title,
                    description = _state.value.description,
                    type = _state.value.type,
                    category = _state.value.category,
                    price = _state.value.price.toDoubleOrNull(),
                    status = _state.value.status,
                    imageUrl = _state.value.imageUrl,
                    targetDate = _state.value.targetDate
                )
            )
            _state.value = _state.value.copy(isSaved = true)
        }
    }
}
