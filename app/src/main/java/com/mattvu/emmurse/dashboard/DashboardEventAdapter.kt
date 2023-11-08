package com.mattvu.emmurse.dashboard

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.model.CombinedEventDataModel
import com.mattvu.emmurse.events.EventsActivity
import com.mattvu.emmurse.events.service.EventAPIClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DashboardEventAdapter(
    private var combinedEvents: MutableList<CombinedEventDataModel>,
    private val context: Context,
    private val onAddEventClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_EVENT = 0
        const val VIEW_TYPE_ADD_EVENT = 1
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cancelEventButton: Button = itemView.findViewById(R.id.cancelEventButton)
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventName)
        private val eventCapacityTextView: TextView = itemView.findViewById(R.id.eventCapacity)
        private val eventDateTimeTextView: TextView = itemView.findViewById(R.id.eventDateTime)
        private val eventImageView: ImageView = itemView.findViewById(R.id.eventImage)



        fun bind(combinedEvent: CombinedEventDataModel, adapter: DashboardEventAdapter, context: Context) {
            eventNameTextView.text = "Location: ${combinedEvent.eventName}"
            eventCapacityTextView.text = "Capacity: ${combinedEvent.capacity}"
            eventDateTimeTextView.text = "Date:${combinedEvent.date} Time:${combinedEvent.time}"

            cancelEventButton.setOnClickListener {
                if (combinedEvent.eventId != null) {
                    adapter.cancelEvent(combinedEvent.eventId)
                    Log.d("DashboardEventAdapter", "Cancel button clicked for event ID: ${combinedEvent.eventId}")
                    // Refresh the DashboardActivity by restarting it
                    val intent = Intent(context, DashboardActivity::class.java)
                    context.startActivity(intent)
                } else {
                    // Handle null eventId (e.g., log an error, show a user message, etc.)
                    Log.e("DashboardEventAdapter", "Cancel button clicked but eventId is null")
                }
            }

            Glide.with(context)
                .load(combinedEvent.imageUrl)
                .into(eventImageView)
        }
    }






    class AddEventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addButton: ImageButton = itemView.findViewById(R.id.addEventButton)

        init {
            addButton.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EventsActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
    override fun getItemViewType(position: Int): Int = if (position == combinedEvents.size) VIEW_TYPE_ADD_EVENT else VIEW_TYPE_EVENT


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == VIEW_TYPE_EVENT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
            EventViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.add_event_item, parent, false)
            AddEventViewHolder(view)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventViewHolder -> holder.bind(combinedEvents[position],this, context)
            is AddEventViewHolder -> holder.itemView.setOnClickListener { onAddEventClicked() }
        }
    }
    fun updateCombinedEvents(newCombinedEvents: List<CombinedEventDataModel>) {
        this.combinedEvents = newCombinedEvents.toMutableList()
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = combinedEvents.size + 1


    private val eventAPIClient = EventAPIClient().service  // Create an instance of EventAPIClient

    fun cancelEvent(eventId: Long) {
        // Since this is a network operation, it's better to handle it in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("DashboardEventAdapter", "Sending request to cancel event ID: $eventId")
                val response = eventAPIClient.cancelEvent(eventId)
                if (response.isSuccessful) {
                    // Handle success, e.g., refresh the list of events or show a success message
                    withContext(Dispatchers.Main) {
                        // Update UI on the main thread
                        Log.d("DashboardEventAdapter", "Event cancellation successful for event ID: $eventId")
                    }
                } else {
                    // Handle error, e.g., show an error message
                    withContext(Dispatchers.Main) {
                        // Update UI on the main thread
                        Log.e("DashboardEventAdapter", "Event cancellation failed for event ID: $eventId. Response: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Log exception
                    Log.e("DashboardEventAdapter", "Exception while canceling event ID: $eventId", e)
                }
            }
        }
    }

}


