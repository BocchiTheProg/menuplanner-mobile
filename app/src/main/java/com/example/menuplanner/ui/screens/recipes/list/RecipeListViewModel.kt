package com.example.menuplanner.ui.screens.recipes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeListViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    // Convert the Repository Flow into a StateFlow for the UI
    val recipes: StateFlow<List<Recipe>> = repository.allRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}