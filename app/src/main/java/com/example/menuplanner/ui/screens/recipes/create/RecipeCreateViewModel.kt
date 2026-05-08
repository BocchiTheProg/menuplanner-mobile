package com.example.menuplanner.ui.screens.recipes.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeCreateViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    fun saveRecipe(title: String, calories: Int, prepTime: Int, isVegetarian: Boolean) {
        viewModelScope.launch {
            val newRecipe = Recipe(
                id = UUID.randomUUID(),
                title = title,
                description = "",
                calories = calories,
                prepTimeMinutes = prepTime,
                isVegetarian = isVegetarian,
                dateAdded = Date()
            )
            repository.saveRecipe(newRecipe)
        }
    }
}