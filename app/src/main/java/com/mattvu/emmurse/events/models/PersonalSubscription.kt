package com.mattvu.emmurse.events.models

data class PersonalSubscription(
    val userID: String,
    val subscriptionTier: String // free, bronze, silver, gold
)
