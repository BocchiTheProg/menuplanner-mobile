package com.example.menuplanner.ui.screens.security

import androidx.lifecycle.ViewModel
import com.example.menuplanner.data.security.AppBiometricManager
import com.example.menuplanner.data.security.BiometricType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val biometricManager: AppBiometricManager
) : ViewModel() {

    private val _biometricType = MutableStateFlow(BiometricType.NONE)
    val biometricType = _biometricType.asStateFlow()

    private val _isBiometricSwitchEnabled = MutableStateFlow(false)
    val isBiometricSwitchEnabled = _isBiometricSwitchEnabled.asStateFlow()

    init {
        _biometricType.value = biometricManager.checkAvailability()
        _isBiometricSwitchEnabled.value = biometricManager.isEnabledByUser()
    }

    fun toggleBiometrics(enabled: Boolean) {
        biometricManager.setEnabledByUser(enabled)
        _isBiometricSwitchEnabled.value = enabled
    }
}