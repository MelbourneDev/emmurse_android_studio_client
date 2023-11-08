// BookingDialogFragment.kt
package com.mattvu.emmurse.events

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.mattvu.emmurse.R
import com.mattvu.emmurse.events.models.Event
import com.mattvu.emmurse.events.service.EventAPIClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookingDialogFragment : DialogFragment() {

    private var eventName: String? = null
    private var userName: String? = null
    private var businessName: String? = null


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventName = it.getString("eventName")
            // Removing userName retrieval from here
            // ... other code ...
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_booking_dialog, container, false)

        // Fetch username directly within the fragment
        val prefs = requireActivity().getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        userName = prefs.getString("username", "")
        Log.d("UsernameDebug", "Retrieved Username: $userName")

        // Now, you can use userName and eventName in your fragment where you need it

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val datePicker = view.findViewById<DatePicker>(R.id.bookingDatePicker)
        val timePicker = view.findViewById<TimePicker>(R.id.bookingTimePicker)
        val numberPickerCapacity = view.findViewById<NumberPicker>(R.id.numberPickerCapacity)
        val submitButton = view.findViewById<Button>(R.id.submitBookingButton)

        // Configuring the NumberPicker
        numberPickerCapacity.minValue = 1
        numberPickerCapacity.maxValue = 100 // You can set this to your max capacity

        submitButton.setOnClickListener {
            val year = datePicker.year
            val month = datePicker.month
            val day = datePicker.dayOfMonth
            val hour = timePicker.hour
            val minute = timePicker.minute
            val capacity = numberPickerCapacity.value

            val calendar = Calendar.getInstance()
            calendar.set(year, month, day, hour, minute)

            val eventAPIClient = EventAPIClient() // Create an instance of EventAPIClient
            val eventAPI = eventAPIClient.service // Access the EventAPI service
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dateFormatted = dateFormatter.format(calendar.time)

            // Make sure to call bookEvent within a coroutine scope if it's a suspend function
            lifecycleScope.launch {
                try {
                    val response = eventAPI.bookEvent(
                        Event(
                            eventName = eventName ?: "",
                            userName = userName,
                            date = dateFormatted,
                            time = String.format("%02d:%02d", hour, minute),
                            capacity = capacity
                        )
                    )

                    Log.d("API_RESPONSE", "Response: $response")

                    if (response.isSuccessful) {
                        // API call was successful, dismiss the fragment
                        Toast.makeText(context, "Booking Successful", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        // API call failed, show an error message but don't dismiss the fragment
                        Toast.makeText(context, "Booking Failed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // API call threw an exception, show an error message but don't dismiss the fragment
                    Log.e("API_ERROR", "Error: $e")
                    Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(eventName: String, userName: String?, businessName: String?) =
            BookingDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("eventName", eventName)
                    putString("userName", userName)
                    putString("businessName", businessName)
                }
            }
    }
}
