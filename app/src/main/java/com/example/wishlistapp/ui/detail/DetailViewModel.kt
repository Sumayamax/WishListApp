package com.example.wishlistapp.ui.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: WishRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(DetailState())
    val state: State<DetailState> = _state

    init {
        savedStateHandle.get<Int>("wishId")?.let { wishId ->
            viewModelScope.launch {
                _state.value = _state.value.copy(isLoading = true)
                val wish = repository.getWishById(wishId)
                _state.value = _state.value.copy(wish = wish, isLoading = false)
            }
        }
    }

    fun onToggleStatus() {
        state.value.wish?.let { wish ->
            viewModelScope.launch {
                val newStatus = if (wish.status == WishStatus.WISH) WishStatus.COMPLETED else WishStatus.WISH
                val updatedWish = wish.copy(status = newStatus)
                repository.updateWish(updatedWish)
                _state.value = _state.value.copy(wish = updatedWish)
            }
        }
    }

    fun onDeleteWish(onDeleted: () -> Unit) {
        state.value.wish?.let { wish ->
            viewModelScope.launch {
                repository.deleteWish(wish)
                onDeleted()
            }
        }
    }
}
