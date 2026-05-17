package com.example.menuplanner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.menuplanner.data.MenuRepository
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.fragment.app.FragmentActivity
import com.example.menuplanner.ui.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject
    lateinit var repository: MenuRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Sync unsynced data when starting the app
        lifecycleScope.launch {
            repository.syncPendingData()
        }

        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface {
                    // Launch navigation and screen structure
                    AppNavigation()
                }
            }
        }
    }
}
