package com.mattvu.emmurse.user_settings.models

data class UserSettingsData(
    val userName: String?,
    val businessName: String?,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val businessEmail: String?,
    val password: String
)

