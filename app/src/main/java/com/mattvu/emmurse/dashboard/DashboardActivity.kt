package com.mattvu.emmurse.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mattvu.emmurse.R
import com.mattvu.emmurse.contact_us.ContactUsActivity
import com.mattvu.emmurse.dashboard.model.CombinedEventDataModel
import com.mattvu.emmurse.dashboard.model.combineEventData
import com.mattvu.emmurse.events.EventsActivity
import com.mattvu.emmurse.events.models.Event
import com.mattvu.emmurse.events.models.goldTierDataList
import com.mattvu.emmurse.events.service.EventAPIClient
import com.mattvu.emmurse.login.LoginActivity
import com.mattvu.emmurse.pricing.PricingActivity
import com.mattvu.emmurse.user_settings.UserSettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private val eventAPIClient = EventAPIClient().service
    private lateinit var dashboardEventAdapter: DashboardEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)



        val logoutButton: Button = findViewById(R.id.logoutButton)

        logoutButton.setOnClickListener {
            // Clear user session or perform any necessary logout actions

            // For example, if you store user data in SharedPreferences, clear it
            val sharedPreferences = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            // Navigate to the LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // Finish the current activity to prevent going back to DashboardActivity
            finish()
        }



        val aboutTextView: TextView = findViewById(R.id.aboutTextView)
        aboutTextView.text = "Welcome to eMmurse, where the boundaries between reality and the virtual world blur, ushering you into an extraordinary dimension of unique experiences and unparalleled excitement. As a cutting-edge VR events company, we redefine the essence of events and gatherings, transporting you into immersive environments that captivate the senses and ignite imagination.\n" +
                "\n" +
                "Our mission revolves around crafting compelling VR experiences that resonate, engage, and create lasting memories. We meticulously design virtual spaces where every detail comes to life, allowing participants to explore, interact, and indulge in a realm of possibilities. From spectacular virtual concerts, groundbreaking conferences, to intimate gatherings, eMmurse is a gateway to diverse and dynamic virtual events that resonate with the evolving needs and aspirations of modern audiences.\n" +
                "\n" +
                "We believe in innovation, creativity, and the transformative power of technology to enrich experiences, connect people across the globe, and foster a sense of community and shared purpose. At eMmurse, we invite you to embark on a mesmerizing journey through the virtual universe where extraordinary events unfold, stories come alive, and unforgettable moments are etched in the heart of the community. Join us, and immerse yourself in the world of spectacular VR events where imagination knows no bounds."


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    // Do nothing since we are already in DashboardActivity
                    true
                }
                R.id.navigation_user_settings -> {
                    // Navigate to UserSettingsActivity
                    val intent = Intent(this, UserSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


        val combinedEvents = mutableListOf<CombinedEventDataModel>()

        // Initialize the adapter
        dashboardEventAdapter = DashboardEventAdapter(mutableListOf(),this) {
            // Handle onAddEventClicked
        }

        val recyclerView: RecyclerView = findViewById(R.id.bookedEventsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = dashboardEventAdapter

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // Navigate to eventsActivity via dashboard menu.
        val btnNavigateToEvents = findViewById<Button>(R.id.btnNavigateToEvents)
        btnNavigateToEvents.setOnClickListener {
            val intent = Intent(this, EventsActivity::class.java)
            startActivity(intent)
        }

        val username = getUserNameFromSharedPreferences()
        if (username.isNotEmpty()) {
            fetchAndDisplayEvents(username)
        } else {
            Log.e("DashboardActivity", "Username is empty.")
        }



        val contactUsButton: Button = findViewById(R.id.contactUsButton)

        contactUsButton.setOnClickListener {
            val intent = Intent(this, ContactUsActivity::class.java)
            startActivity(intent)
        }

        val pricingButton: Button = findViewById(R.id.pricingButton)
        pricingButton.setOnClickListener {
            // Open the PricingActivity when the button is clicked
            val intent = Intent(this@DashboardActivity, PricingActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getUserNameFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "") ?: ""

        if (username.isNotEmpty()) {
            val greetingTextView: TextView = findViewById(R.id.greetingTextView)
            greetingTextView.text = "Welcome, $username"
        }

        return username
    }



    private fun fetchAndDisplayEvents(username: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = eventAPIClient.getUserEvents(username)
            if (response.isSuccessful) {
                val serverEvents = response.body() ?: emptyList()
                updateUI(serverEvents)
            } else {
                Log.e("DashboardActivity", "Failed to fetch events: ${response.errorBody()?.string()}")
            }
        }
    }

    private fun updateUI(serverEvents: List<Event>) {
        val combinedEvents: List<CombinedEventDataModel> = combineEventData(serverEvents, goldTierDataList)
        dashboardEventAdapter.updateCombinedEvents(combinedEvents)
        Log.d("DashboardActivity", "Adapter Updated: ${combinedEvents.size} events")
    }
}
