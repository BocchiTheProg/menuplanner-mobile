package com.example.menuplanner.data.security

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class AppBiometricManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val prefs: SecurityPreferences
) {
    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
    }

    fun checkAvailability(): BiometricType {
        if (isEmulator()) {
            // Bypass strict hardware tier checks (on Emulator)
            // and assume fingerprint emulation is available for development.
            return BiometricType.FINGERPRINT
        }

        val manager = BiometricManager.from(context)

        // Check for Strong hardware (Real physical devices with secure enclaves)
        val strongStatus = manager.canAuthenticate(BIOMETRIC_STRONG)
        if (strongStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometricType.FINGERPRINT
        }

        // Fallback check for Weak hardware (budget devices)
        val weakStatus = manager.canAuthenticate(BIOMETRIC_WEAK)
        if (weakStatus == BiometricManager.BIOMETRIC_SUCCESS) {
            return BiometricType.FINGERPRINT
        }

        return BiometricType.NONE
    }

    fun isEnabledByUser(): Boolean = prefs.isBiometricsEnabled()

    fun setEnabledByUser(enabled: Boolean) = prefs.setBiometricsEnabled(enabled)

    suspend fun authenticate(activity: FragmentActivity, reason: String): BiometricAuthState =
        withContext(Dispatchers.Main) {
            val availability = checkAvailability()
            if (availability == BiometricType.NONE) {
                return@withContext BiometricAuthState.UNAVAILABLE
            }

            suspendCancellableCoroutine { continuation ->
                val executor = ContextCompat.getMainExecutor(activity)

                val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        if (continuation.isActive) continuation.resume(BiometricAuthState.SUCCESS)
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (continuation.isActive) {
                            if (errorCode == BiometricPrompt.ERROR_USER_CANCELED || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                                continuation.resume(BiometricAuthState.IDLE)
                            } else {
                                continuation.resume(BiometricAuthState.FAILED)
                            }
                        }
                    }

                    override fun onAuthenticationFailed() {
                        // Keep prompt open for continuous user touch retries
                    }
                }

                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Security Check Required")
                    .setSubtitle(reason)
                    .setNegativeButtonText("Cancel")
                    .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK) // allow both STRONG and WEAK sensors
                    .build()

                val prompt = BiometricPrompt(activity, executor, callback)
                prompt.authenticate(promptInfo)

                continuation.invokeOnCancellation {
                    prompt.cancelAuthentication()
                }
            }
        }
}