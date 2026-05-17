package com.example.menuplanner.data.security

enum class BiometricType(val displayName: String) {
    NONE("Not Supported"),
    FINGERPRINT("Touch ID / Fingerprint"),
    FACE("Face ID / Facial Recognition"),
    GENERIC("Biometric Security")
}

enum class BiometricAuthState {
    IDLE, AUTHENTICATING, SUCCESS, FAILED, UNAVAILABLE
}