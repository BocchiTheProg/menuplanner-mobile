package com.example.menuplanner.ui.screens.mealPlans.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.MealPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MealPlanDetailViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _mealPlan = MutableStateFlow<MealPlan?>(null)
    val mealPlan: StateFlow<MealPlan?> = _mealPlan.asStateFlow()

    fun loadMealPlan(idString: String?) {
        val id = idString?.let { UUID.fromString(it) } ?: return
        viewModelScope.launch {
            _mealPlan.value = repository.getMealPlan(id)
        }
    }

    fun toggleCookedStatus() {
        val current = _mealPlan.value ?: return
        viewModelScope.launch {
            val updated = current.copy(isCooked = !current.isCooked)
            repository.saveMealPlan(updated)
            _mealPlan.value = updated
        }
    }
}