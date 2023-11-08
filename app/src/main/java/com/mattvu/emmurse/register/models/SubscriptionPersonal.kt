package com.mattvu.emmurse.register.models

data class Subscription(val type: String, val displayString: String)

val subscriptions = listOf(
    Subscription("hint", "Choose subscription"),
    Subscription("free", "FREE"),
    Subscription("bronze", "Bronze - $20 per month"),
    Subscription("silver", "Silver - $30 per month"),
    Subscription("gold", "Gold - $40 per month")
)


