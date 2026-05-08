package com.example.menuplanner.ui.screens.recipes.update

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
class RecipeUpdateViewModel @Inject constructor(
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

    fun updateRecipe(title: String, calories: Int, prepTime: Int, isVegetarian: Boolean) {
        val current = _recipe.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                title = title,
                calories = calories,
                prepTimeMinutes = prepTime,
                isVegetarian = isVegetarian
            )
            repository.saveRecipe(updated)
        }
    }
}