package com.mattvu.emmurse.contact_us

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.DashboardActivity
import com.mattvu.emmurse.user_settings.UserSettingsActivity

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val messageEditText: EditText = findViewById(R.id.messageEditText)
        val sendEmailButton: Button = findViewById(R.id.sendEmailButton)
        val chatBotIcon: ImageView = findViewById(R.id.chatBotIcon)


        // Clear the text fields when the send button is clicked
        sendEmailButton.setOnClickListener {
            emailEditText.text.clear()
            messageEditText.text.clear()
        }


        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    // Navigate to DashboardActivity
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
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


        chatBotIcon.setOnClickListener {
            Log.d("ChatBotIcon", "Icon Clicked") // Log to see if this part is executed

            val shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation)
            chatBotIcon.startAnimation(shake)

            val username = getUserNameFromSharedPreferences()
            val greeting = "Hello, $username, how can I be of service today?"

            val transaction = supportFragmentManager.beginTransaction()
            val chatFragment = ChatFragment.newInstance(greeting)
            transaction.replace(R.id.container, chatFragment) // Ensure the ID is correct
            transaction.addToBackStack(null)
            transaction.commit()

            Log.d("ChatBotIcon", "Fragment Transaction Committed") // Log to see if this part is executed
        }

    }

    private fun getUserNameFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("username", "") ?: ""
    }

}