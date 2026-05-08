package com.example.menuplanner.ui.screens.recipes.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _recipe = MutableStateFlow<Recipe?>(null)
    val recipe: StateFlow<Recipe?> = _recipe.asStateFlow()

    fun loadRecipe(idString: String?) {
        val id = idString?.let { UUID.fromString(it) } ?: return
        viewModelScope.launch {
            _recipe.value = repository.getRecipe(id)
        }
    }

    fun updateDescription(description: String) {
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            val updated = currentRecipe.copy(description = description)
            repository.saveRecipe(updated)
            _recipe.value = updated
        }
    }
}