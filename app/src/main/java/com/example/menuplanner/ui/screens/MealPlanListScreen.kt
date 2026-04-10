package com.example.menuplanner.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.MealPlan

@Composable
fun MealPlanListScreen(onMealClick: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val mealPlans = DummyData.mealPlans // Fetching data

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            item {
                Text("Weekly Meal Plans", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(mealPlans) { mealPlan ->
                MealPlanCard(mealPlan, onMealClick)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MealPlanCard(mealPlan: MealPlan, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(mealPlan.id.toString()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = mealPlan.dayOfWeek, style = MaterialTheme.typography.titleLarge)
            Text(text = if (mealPlan.isCooked) "Cooked" else "Need to be Prepared")
        }
    }
}