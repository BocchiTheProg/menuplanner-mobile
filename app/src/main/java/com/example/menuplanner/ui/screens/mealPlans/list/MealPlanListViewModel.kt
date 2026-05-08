package com.example.menuplanner.ui.screens.mealPlans.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.MenuRepository
import com.example.menuplanner.domain.model.MealPlan
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MealPlanListViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    val mealPlans: StateFlow<List<MealPlan>> = repository.allMealPlans
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}