package com.mattvu.emmurse.dashboard.model

import com.mattvu.emmurse.events.models.Event
import com.mattvu.emmurse.events.models.EventDataModel
import com.mattvu.emmurse.events.models.goldTierDataList

data class CombinedEventDataModel(
    val eventId: Long? = null,
    val eventName: String,
    val userName: String? = null,
    val date: String,
    val time: String,
    val capacity: Int,
    val eventDescription: String,
    val imageUrl: String
)

fun combineEventData(serverEvents: List<Event>, clientEvents: List<EventDataModel>): List<CombinedEventDataModel> {
    return serverEvents.map { serverEvent ->
        val clientEvent = clientEvents.find { it.eventName == serverEvent.eventName }
        CombinedEventDataModel(
            eventId = serverEvent.eventId,
            eventName = serverEvent.eventName,
            userName = serverEvent.userName,
            date = serverEvent.date,
            time = serverEvent.time,
            capacity = serverEvent.capacity,
            eventDescription = clientEvent?.eventDescription ?: "Description not available.",
            imageUrl = clientEvent?.imageUrl ?: "URL not available."
        )
    }
}



