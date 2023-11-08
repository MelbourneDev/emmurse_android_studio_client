package com.mattvu.emmurse.register.models

data class SubscriptionBusiness(val type: String, val displayString: String)

val subscriptionsBus = listOf(
    Subscription("hint", "Choose subscription"),
    Subscription("bronze", "Bronze - $150 per month"),
    Subscription("silver", "Silver - $200 per month"),
    Subscription("gold", "Gold - $250 per month")
)