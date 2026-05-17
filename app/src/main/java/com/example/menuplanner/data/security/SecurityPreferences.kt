package com.example.menuplanner.data.security

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SecurityPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("security_settings", Context.MODE_PRIVATE)

    fun isBiometricsEnabled(): Boolean {
        return prefs.getBoolean("KEY_BIOMETRICS_ENABLED", false)
    }

    fun setBiometricsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("KEY_BIOMETRICS_ENABLED", enabled) }
    }
}