package com.example.menuplanner.di

import android.content.Context
import androidx.room.Room
import com.example.menuplanner.data.network.MenuApiService
import com.example.menuplanner.data.network.MockMenuApiImpl
import com.example.menuplanner.data.local.MenuDao
import com.example.menuplanner.data.local.MenuDatabase
import com.example.menuplanner.data.MenuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMenuDatabase(@ApplicationContext context: Context): MenuDatabase {
        return Room.databaseBuilder(
            context,
            MenuDatabase::class.java,
            "menu_planner_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMenuDao(database: MenuDatabase): MenuDao {
        return database.menuDao()
    }

    @Provides
    @Singleton
    fun provideMenuApiService(): MenuApiService {
        return MockMenuApiImpl() // Swap with Retrofit implementation later
    }

    @Provides
    @Singleton
    fun provideMenuRepository(dao: MenuDao, api: MenuApiService): MenuRepository {
        return MenuRepository(dao, api)
    }
}