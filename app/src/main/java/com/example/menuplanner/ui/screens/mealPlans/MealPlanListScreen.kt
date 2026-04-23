package com.example.menuplanner.ui.screens.mealPlans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.menuplanner.data.DummyData
import com.example.menuplanner.data.model.MealPlan

@Composable
fun MealPlanListScreen(onMealClick: (String) -> Unit) {
    val mealPlans = DummyData.mealPlans

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your Weekly Planner", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "7 days, one plan each day",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(mealPlans) { mealPlan ->
            MealPlanCard(mealPlan, onMealClick)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun MealPlanCard(mealPlan: MealPlan, onClick: (String) -> Unit) {
    val cardColor = if (mealPlan.isCooked) Color(0xFFE8F5E9) else Color(0xFFFFF4E5)

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