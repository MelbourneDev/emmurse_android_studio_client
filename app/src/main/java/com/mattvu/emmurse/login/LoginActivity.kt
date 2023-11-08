package com.mattvu.emmurse.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.DashboardActivity
import com.mattvu.emmurse.login.models.LoginData
import com.mattvu.emmurse.login.service.LoginAPIClient
import com.mattvu.emmurse.register.RegisterActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback




class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().remove("username").apply()


        // Find the ImageViews
        val circle1 = findViewById<ImageView>(R.id.circleImage1)
        val circle2 = findViewById<ImageView>(R.id.circleImage2)
        val circle3 = findViewById<ImageView>(R.id.circleImage3)
        val circle4 = findViewById<ImageView>(R.id.circleImage4)
        // Load the bop animation
        circle1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bop_animation))
        circle2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bop_animation1))
        circle3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bop_animation2))
        circle4.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bop_animation3))

        // initiating the UI elements
        val loginUserName: EditText = findViewById(R.id.loginUserName)
        val loginPassword: EditText = findViewById(R.id.loginPassword)
        val registerButton: Button = findViewById(R.id.registerButton)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener{
            val LoginRequest = LoginData (
                userName = loginUserName.text.toString().trim(),
                password = loginPassword.text.toString().trim(),
            )

            val call = LoginAPIClient.service.LoginUser(LoginRequest)
            call.enqueue(object: Callback<ResponseBody>{
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("API_RESPONSE", "Received status code: ${response.code()}")
                    try{
                        val responseBody = response.body()?.string()
                        Log.d("API_RESPONSE", "Received response: $responseBody")

                        if(response.isSuccessful && responseBody != null) {
                            val successMessage = responseBody.toString()
                            Toast.makeText(this@LoginActivity, successMessage, Toast.LENGTH_SHORT).show()

                            getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("username", loginUserName.text.toString().trim())
                                .apply()

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                            startActivity(intent)
                        } else {

                            if (response.code() == 400) {
                                val errorMessage = response.errorBody()?.string()
                                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Exception in onResponse: ${e.message}", e)
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    try {                  // Handle failure cases as needed
                        // ...
                    } catch (e: Exception) {
                        Log.e("API_FAILURE", "Exception in onFailure: ${e.message}", e)
                    }
                }
            })


        }


        registerButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }





    }
}

