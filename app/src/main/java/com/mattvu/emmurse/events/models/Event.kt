package com.mattvu.emmurse.events.models


data class Event(
    val eventId: Long? = null,
    val eventName: String,
    val userName: String? = null,
    val date: String,
    val time: String,
    val capacity: Int
)
