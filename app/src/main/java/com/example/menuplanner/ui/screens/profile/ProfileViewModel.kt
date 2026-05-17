package com.example.menuplanner.ui.screens.profile

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.menuplanner.data.security.AppBiometricManager
import com.example.menuplanner.data.security.BiometricAuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val biometricManager: AppBiometricManager
) : ViewModel() {

    private val _authState = MutableStateFlow(BiometricAuthState.IDLE)
    val authState = _authState.asStateFlow()

    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked = _isUnlocked.asStateFlow()

    fun checkIfAuthenticationRequired() {
        if (!biometricManager.isEnabledByUser()) {
            _isUnlocked.value = true // Automatically skip if disabled
        }
    }

    fun initiateAuthentication(activity: FragmentActivity) {
        viewModelScope.launch {
            _authState.value = BiometricAuthState.AUTHENTICATING
            val result = biometricManager.authenticate(activity, "Unlock secure app data profile parameters.")
            _authState.value = result

            if (result == BiometricAuthState.SUCCESS) {
                _isUnlocked.value = true
            }
        }
    }

    fun resetLockState() {
        if (biometricManager.isEnabledByUser()) {
            _isUnlocked.value = false
            _authState.value = BiometricAuthState.IDLE
        }
    }
}