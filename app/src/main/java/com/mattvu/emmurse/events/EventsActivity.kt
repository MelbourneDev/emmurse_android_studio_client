package com.mattvu.emmurse.events


import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.DashboardActivity
import com.mattvu.emmurse.events.models.*
import com.mattvu.emmurse.events.service.UserTierAPIClient
import com.mattvu.emmurse.user_settings.UserSettingsActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventsActivity : AppCompatActivity(), EventsAdapter.OnItemClickListener {

    override fun onItemClick(eventName: String) {
        val prefs = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "") ?: ""

        val bookingDialogFragment = BookingDialogFragment().apply {
            arguments = Bundle().apply {
                putString("eventName", eventName)
                putString("userName", username)
            }
        }
        bookingDialogFragment.show(supportFragmentManager, "bookingDialog")
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_user_settings -> {
                    val intent = Intent(this, UserSettingsActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



        // Define RecyclerViews
        val freeTierRecyclerView = findViewById<RecyclerView>(R.id.freeTierRecyclerView)
        val bronzeTierRecyclerView = findViewById<RecyclerView>(R.id.bronzeTierRecyclerView)
        val silverTierRecyclerView = findViewById<RecyclerView>(R.id.silverTierRecyclerView)
        val goldTierRecyclerView = findViewById<RecyclerView>(R.id.goldTierRecyclerView)

        // Define Cards
        val freeTierCard = findViewById<CardView>(R.id.freeTierCard)
        val bronzeTierCard = findViewById<CardView>(R.id.bronzeTierCard)
        val silverTierCard = findViewById<CardView>(R.id.silverTierCard)
        val goldTierCard = findViewById<CardView>(R.id.goldTierCard)

        initializeSnapHelper(freeTierRecyclerView)
        initializeSnapHelper(bronzeTierRecyclerView)
        initializeSnapHelper(silverTierRecyclerView)
        initializeSnapHelper(goldTierRecyclerView)

        // Define ScrollView
        val scrollView = findViewById<ScrollView>(R.id.scrollView)

// Initialize RecyclerView LayoutManagers
        val freeTierLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val bronzeTierLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val silverTierLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val goldTierLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

// Set LayoutManagers for RecyclerViews
        freeTierRecyclerView.layoutManager = freeTierLayoutManager
        bronzeTierRecyclerView.layoutManager = bronzeTierLayoutManager
        silverTierRecyclerView.layoutManager = silverTierLayoutManager
        goldTierRecyclerView.layoutManager = goldTierLayoutManager

        // Initialize data lists from EventData.kt
        val freeTierDataList = freeTierDataList
        val bronzeTierDataList = bronzeTierDataList
        val silverTierDataList = silverTierDataList
        val goldTierDataList = goldTierDataList

        // Create and set adapters for RecyclerViews
        val freeTierAdapter = EventsAdapter(freeTierDataList, this)
        val bronzeTierAdapter = EventsAdapter(bronzeTierDataList, this)
        val silverTierAdapter = EventsAdapter(silverTierDataList, this)
        val goldTierAdapter = EventsAdapter(goldTierDataList, this)


        freeTierRecyclerView.adapter = freeTierAdapter
        bronzeTierRecyclerView.adapter = bronzeTierAdapter
        silverTierRecyclerView.adapter = silverTierAdapter
        goldTierRecyclerView.adapter = goldTierAdapter

              val bronzeBanner: TextView = findViewById(R.id.bronzeBanner)
        val silverBanner: TextView = findViewById(R.id.silverBanner)
        val goldBanner: TextView = findViewById(R.id.goldBanner)
        val freeBanner: TextView = findViewById(R.id.freeBanner)

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        val cardHeight = (screenHeight * 0.8).toInt()

// Then apply this height to your CardViews
        freeTierCard.layoutParams.height = cardHeight
        bronzeTierCard.layoutParams.height = cardHeight
        silverTierCard.layoutParams.height = cardHeight
        goldTierCard.layoutParams.height = cardHeight
// ... (repeat for each card)



        // Initialize Retrofit service
        val service = UserTierAPIClient.service

        // Get the username from SharedPreferences (replace with your key)
        val prefs = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "") ?: ""

        // Make an API call to get the user's subscription type
        val call = service.getUserSubscription(username)
        Log.d("EventsActivity", "Before enqueue")
        call.enqueue(object : Callback<String> {

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("EventsActivity", "Inside onResponse")
                if (response.isSuccessful) {
                    val subscriptionType = response.body() ?: return
                    val toastText = "Subscription Type: $subscriptionType"
                    Toast.makeText(this@EventsActivity, toastText, Toast.LENGTH_SHORT).show()

                    // Grey out or disable tiers that user does not have access to
                    when (subscriptionType.lowercase()) {
                        "free" -> {


                            bronzeBanner.visibility = View.VISIBLE
                            silverBanner.visibility = View.VISIBLE
                            goldBanner.visibility = View.VISIBLE

                            bronzeTierCard.alpha = 0.25f
                            silverTierCard.alpha = 0.25f
                            goldTierCard.alpha = 0.25f

                            bronzeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            silverTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            goldTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                        }


                        "bronze" -> {


                            freeBanner.visibility = View.VISIBLE
                            silverBanner.visibility = View.VISIBLE
                            goldBanner.visibility = View.VISIBLE

                            freeTierCard.alpha = 0.25f
                            silverTierCard.alpha = 0.25f
                            goldTierCard.alpha = 0.25f

                            freeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            silverTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            goldTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                        }

                        "silver" -> {


                            freeBanner.visibility = View.VISIBLE
                            bronzeBanner.visibility = View.VISIBLE
                            goldBanner.visibility = View.VISIBLE

                            freeTierCard.alpha = 0.25f
                            bronzeTierCard.alpha = 0.25f
                            goldTierCard.alpha = 0.25f

                            bronzeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            freeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            goldTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                        }

                        "gold" -> {


                            freeBanner.visibility = View.VISIBLE
                            bronzeBanner.visibility = View.VISIBLE
                            silverBanner.visibility = View.VISIBLE

                            freeTierCard.alpha = 0.25f
                            bronzeTierCard.alpha = 0.25f
                            silverTierCard.alpha = 0.25f

                            bronzeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            silverTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false
                            freeTierCard.findViewById<Button>(R.id.bookEventButton).isEnabled = false



                        }
                    }

                    // text for the banners
                    bronzeBanner.text = "Subscribe to this tier to gain access"
                    silverBanner.text = "Subscribe to this tier to gain access"
                    goldBanner.text = "Subscribe to this tier to gain access"
                    freeBanner.text = "Subscribe to this tier to gain access"


                    // Call scrollToSubscriptionCard here with the fetched subscriptionType
                    scrollToSubscriptionCard(subscriptionType)
                }
            }


            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("EventsActivity", "Inside onFailure")
                // Handle failure, e.g., show error message to the user
            }
        })

        Log.d("EventsActivity", "After enqueue")
    }

    // Define scrollToSubscriptionCard outside the onCreate method
    fun scrollToSubscriptionCard(subscriptionType: String) {
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val relativeLayout: RelativeLayout = when (subscriptionType.lowercase()) {
            "free" -> findViewById(R.id.relativeLayoutFree)
            "bronze" -> findViewById(R.id.relativeLayoutBronze)
            "silver" -> findViewById(R.id.relativeLayoutSilver)
            "gold" -> findViewById(R.id.relativeLayoutGold)
            else -> return
        }

        scrollView.postDelayed({
            scrollView.smoothScrollTo(0, relativeLayout.top)
        }, 300)  // Delay of 300 milliseconds
    }


    fun initializeSnapHelper(recyclerView: RecyclerView) {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
    }



}
