package com.example.menuplanner.ui.screens.mealPlans.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.menuplanner.domain.model.MealPlan

@Composable
fun MealPlanListScreen(
    viewModel: MealPlanListViewModel = hiltViewModel(),
    onMealClick: (String) -> Unit
) {
    val mealPlans by viewModel.mealPlans.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your Weekly Planner", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "${mealPlans.size} plans available",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(mealPlans, key = { it.id }) { mealPlan ->
            MealPlanCard(mealPlan, onMealClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MealPlanCard(mealPlan: MealPlan, onClick: (String) -> Unit) {
    val cardColor = if (mealPlan.isCooked) Color(0xFFE8F5E9) else Color(0xFFFFF4E5)

    val menuSummary = listOfNotNull(
        mealPlan.breakfast?.title,
        mealPlan.lunch?.title,
        mealPlan.dinner?.title
    ).joinToString(" • ").ifEmpty { "No meals selected yet" }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(mealPlan.id.toString()) },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mealPlan.dayOfWeek,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = { Text(if (mealPlan.isCooked) "Cooked" else "Pending") },
                    leadingIcon = {
                        Icon(
                            imageVector = if (mealPlan.isCooked) Icons.Default.CheckCircle else Icons.Default.Schedule,
                            contentDescription = null
                        )
                    }
                )
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocalDining, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${mealPlan.breakfast.title} • ${mealPlan.lunch.title} • ${mealPlan.dinner.title}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}