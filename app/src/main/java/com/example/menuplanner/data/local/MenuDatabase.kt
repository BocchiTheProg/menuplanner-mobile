package com.example.menuplanner.data.local

import java.util.UUID
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [RecipeEntity::class, MealPlanEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class) // Register the converters
abstract class MenuDatabase : RoomDatabase() {
    abstract fun menuDao(): MenuDao

    companion object {
        private val DAYS = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

        fun getDatabaseCallback(): RoomDatabase.Callback {
            return object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    DAYS.forEach { day ->
                        // Generate a clean UUID string
                        val uuid = UUID.randomUUID().toString()

                        db.execSQL(
                            """
                            INSERT INTO meal_plans (id, dayOfWeek, breakfastId, lunchId, dinnerId, isCooked, syncStatus) 
                            VALUES ('$uuid', '$day', NULL, NULL, NULL, 0, 'SYNCED')
                            """.trimIndent()
                        )
                    }
                }
            }
        }
    }
}