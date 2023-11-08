package com.mattvu.emmurse.events.models

data class BusinessSubscription(
    val userID: String,
    val subscriptionTier: String // bronze, silver gold
)
