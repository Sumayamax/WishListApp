package com.example.wishlistapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<Int>("wishId")?.let { wishId ->
            if (wishId != -1) {
                loadWish(wishId)
            }
        }
    }

    private fun loadWish(wishId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val wish = repository.getWishById(wishId)
            _state.update { it.copy(wish = wish, isLoading = false) }
        }
    }

    fun onToggleStatus() {
        val currentWish = _state.value.wish ?: return
        viewModelScope.launch {
            val newStatus = if (currentWish.status == WishStatus.WISH) WishStatus.COMPLETED else WishStatus.WISH
            val updatedWish = currentWish.copy(status = newStatus)
            repository.updateWish(updatedWish)
            _state.update { it.copy(wish = updatedWish) }
        }
    }

    fun onDeleteWish(onDeleted: () -> Unit) {
        val currentWish = _state.value.wish ?: return
        viewModelScope.launch {
            repository.deleteWish(currentWish)
            onDeleted()
        }
    }
}
