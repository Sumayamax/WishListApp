package com.example.wishlistapp.ui.completed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wishlistapp.domain.model.WishItem
import com.example.wishlistapp.domain.model.WishStatus
import com.example.wishlistapp.domain.repository.WishRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletedViewModel @Inject constructor(
    private val repository: WishRepository
) : ViewModel() {

    val completedWishes: StateFlow<List<WishItem>> = repository.getAllWishes()
        .map { wishes ->
            wishes.filter { it.status == WishStatus.COMPLETED }
                .sortedByDescending { it.createdAt }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onUndoStatus(wish: WishItem) {
        viewModelScope.launch {
            repository.updateWish(wish.copy(status = WishStatus.WISH))
        }
    }

    fun onDeleteWish(wish: WishItem) {
        viewModelScope.launch {
            repository.deleteWish(wish)
        }
    }
}
