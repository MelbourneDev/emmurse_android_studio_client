package com.mattvu.emmurse.user_settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.DashboardActivity
import com.mattvu.emmurse.user_settings.models.UserSettingsData
import com.mattvu.emmurse.user_settings.service.UserSettingsApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserSettingsActivity : AppCompatActivity() {

    private val TAG = "UserSettingsActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_settings)
        Log.d(TAG, "UserSettingsActivity onCreate")

        // Get the username from SharedPreferences
        val prefs = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "") ?: ""

        // Initialize UI elements
        val userNameEditText = findViewById<EditText>(R.id.editTextUserNamePersonal)
        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstNamePersonal)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastNamePersonal)
        val emailEditText = findViewById<EditText>(R.id.editTextEmailPersonal)
        val passwordEditText = findViewById<EditText>(R.id.editTextPasswordPersonal)
        val passwordValidEditText = findViewById<EditText>(R.id.editTextPasswordValid)
        val saveButton = findViewById<Button>(R.id.buttonSavePersonal)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_user_settings -> {
                    true
                }
                else -> false
            }
        }

        // Load user data and populate the form
        val userSettingsService = UserSettingsApiClient.service
        userSettingsService.getUserData(username)
            .enqueue(object : Callback<UserSettingsData> {
                override fun onResponse(call: Call<UserSettingsData>, response: Response<UserSettingsData>) {
                    if (response.isSuccessful) {
                        val userData = response.body()
                        if (userData != null) {
                            Log.d(TAG, "User data retrieved successfully: $userData")
                            userNameEditText.setText(userData.userName ?: userData.businessName)
                            firstNameEditText.setText(userData.firstName)
                            lastNameEditText.setText(userData.lastName)
                            emailEditText.setText(userData.email ?: userData.businessEmail)
                            passwordEditText.setText(userData.password)
                        }
                    } else {
                        Log.e(TAG, "API error: " + response.code())
                        Log.e(TAG, "Response body is null")
                    }
                }

                override fun onFailure(call: Call<UserSettingsData>, t: Throwable) {
                    Log.e(TAG, "Network or other failure: " + t.message)
                }
            })


        saveButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            val confirmPassword = passwordValidEditText.text.toString()
            if (password != confirmPassword) {
                // If they don't match, log an error (or show an error message to the user)
                Log.e(TAG, "Passwords do not match")
                // Optionally, you could display a Toast or an AlertDialog to inform the user
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Exit the listener early
            }


            val updatedUserData = UserSettingsData(
                userName = userNameEditText.text.toString(),
                businessName = userNameEditText.text.toString(),  // Using the same field for both userName and businessName
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString(),
                email = emailEditText.text.toString(),
                businessEmail = emailEditText.text.toString(),  // Using the same field for both email and businessEmail
                password = passwordEditText.text.toString()
            )

            userSettingsService.updateUserData(username, updatedUserData)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    )  {
                        if (response.isSuccessful) {
                            Log.d(TAG, "User data updated successfully")
                        } else {
                            Log.e(
                                TAG,
                                "API error during update: ${response.code()} ${response.message()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e(TAG, "Network or other failure during update: ${t.message}")
                    }
                })
        }
    }
}
