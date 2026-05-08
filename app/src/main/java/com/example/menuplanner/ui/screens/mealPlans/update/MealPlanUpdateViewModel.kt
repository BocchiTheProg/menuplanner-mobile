package com.example.menuplanner.ui.screens.mealPlans.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.MealPlan
import com.example.menuplanner.domain.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MealPlanUpdateViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _mealPlan = MutableStateFlow<MealPlan?>(null)
    val mealPlan: StateFlow<MealPlan?> = _mealPlan.asStateFlow()

    // Needed for the dropdown selections
    val availableRecipes: StateFlow<List<Recipe>> = repository.allRecipes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadMealPlan(idString: String?) {
        val id = idString?.let { UUID.fromString(it) } ?: return
        viewModelScope.launch {
            _mealPlan.value = repository.getMealPlan(id)
        }
    }

    fun updateMealPlan(breakfast: Recipe, lunch: Recipe, dinner: Recipe) {
        val current = _mealPlan.value ?: return
        viewModelScope.launch {
            val updated = current.copy(
                breakfast = breakfast,
                lunch = lunch,
                dinner = dinner
            )
            repository.saveMealPlan(updated)
        }
    }
}