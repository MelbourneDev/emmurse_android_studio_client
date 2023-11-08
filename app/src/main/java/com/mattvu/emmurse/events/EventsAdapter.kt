// EventsAdapter.kt
package com.mattvu.emmurse.events

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mattvu.emmurse.R
import com.mattvu.emmurse.events.models.EventDataModel
import com.squareup.picasso.Picasso

class EventsAdapter(
    private val dataList: List<EventDataModel>,
    private val listener: OnItemClickListener? // Add listener parameter
) : RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    // Define the item click listener interface
    interface OnItemClickListener {
        fun onItemClick(eventName: String)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val bookEventButton: Button = itemView.findViewById(R.id.bookEventButton)

        init {
            bookEventButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val actualPosition = position % dataList.size // Calculate actual position
                    val event = dataList[actualPosition] // Use actualPosition here
                    listener?.onItemClick(event.eventName) // Utilize the listener
                }
            }
        }

    }





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_contents, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actualPosition = position % dataList.size // Calculate actual position

        val data = dataList[actualPosition] // Use actualPosition here

        Picasso.get()
            .load(data.imageUrl)
            .into(holder.imageView)
        holder.eventNameTextView.text = data.eventName // Update event name
        holder.textView.text = data.eventDescription
    }


    override fun getItemCount(): Int {
        return Integer.MAX_VALUE
    }

    fun onBookButtonClick(context: Context, eventName: String) {
        val bookingDialogFragment = BookingDialogFragment()

        val bundle = Bundle()
        bundle.putString("eventName", eventName)
        bookingDialogFragment.arguments = bundle

        bookingDialogFragment.show((context as FragmentActivity).supportFragmentManager, "bookingDialog")
    }
}
