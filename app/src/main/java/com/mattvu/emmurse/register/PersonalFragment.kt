package com.mattvu.emmurse.register


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.mattvu.emmurse.R
import com.mattvu.emmurse.login.LoginActivity
import com.mattvu.emmurse.payscreen.PayScreenActivity
import com.mattvu.emmurse.register.models.PersonalUserData
import com.mattvu.emmurse.register.models.Subscription
import com.mattvu.emmurse.register.service.PersonalApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import okhttp3.ResponseBody

// Remove unwanted imports like okhttp3.Call and javax.security.auth.callback.Callback


class PersonalFragment : Fragment() {

    // This function is called to create the view for this Fragment.
    // Here, we inflate the layout we defined above.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_personal, container, false)
    }

    // This function is called once the view is created. Here, you can
    // initialize your UI components.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For example, get a reference to the EditText
        val userNameEditText: EditText = view.findViewById(R.id.userName)
        val firstNameEditText: EditText = view.findViewById(R.id.firstName)
        val lastNameEditText: EditText = view.findViewById(R.id.lastName)
        val emailRegisterEditText: EditText = view.findViewById(R.id.editTextEmailAddress)
        val passwordRegisterEditText: EditText = view.findViewById(R.id.editTextTextRegistrationPassword)
        val passwordValidationRegisterEditText: EditText = view.findViewById(R.id.editTextTextPasswordValidation)
        val slideToActSeekbar: SeekBar = view.findViewById(R.id.slideToActSeekbar)
        val submitButton: Button = view.findViewById((R.id.registerPersonalAccountButton))
        val subscriptionSpinner: Spinner = view.findViewById(R.id.subscriptionSpinner)
        val subscriptions = listOf(
            Subscription("hint", "Choose subscription"),
            Subscription("free", "FREE"),
            Subscription("bronze", "Bronze - $20 per month"),
            Subscription("silver", "Silver - $30 per month"),
            Subscription("gold", "Gold - $40 per month")
        )

        // Adapter should be initialized using the displayString of each Subscription
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subscriptions.map { it.displayString })
        subscriptionSpinner.adapter = adapter
        subscriptionSpinner.post { subscriptionSpinner.setSelection(0, false) }
        subscriptionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedSubscription = subscriptions[position]
                Log.d("DEBUG_SUBSCRIPTION", "Selected type: ${selectedSubscription.type}")
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        fun getSubscriptionPrice(type: String): String {
            return when (type) {
                "gold" -> "$40 per month"
                "silver" -> "$30 per month"
                "bronze" -> "$20 per month"
                "free" -> "FREE"
                else -> "Unknown Subscription"
            }
        }



        submitButton.setOnClickListener {

            // 1. Check for empty fields
            if (userNameEditText.text.isNullOrEmpty() ||
                firstNameEditText.text.isNullOrEmpty() ||
                lastNameEditText.text.isNullOrEmpty() ||
                emailRegisterEditText.text.isNullOrEmpty() ||
                passwordRegisterEditText.text.isNullOrEmpty() ||
                passwordValidationRegisterEditText.text.isNullOrEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Check for valid email format
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (!emailRegisterEditText.text.toString().trim().matches(emailPattern.toRegex())) {
                Toast.makeText(context, "Invalid email format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Check if passwords match
            if (passwordRegisterEditText.text.toString() != passwordValidationRegisterEditText.text.toString()) {
                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 4. Determine the selected subscription
            val selectedPosition = subscriptionSpinner.selectedItemPosition
            val selectedSubscriptionType = subscriptions[selectedPosition].type

            // 5. Ensure the selected type isn't "hint"
            if (selectedSubscriptionType == "hint") {
                Toast.makeText(context, "Please select a valid subscription.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }



            val registrationRequest = PersonalUserData(
                userName = userNameEditText.text.toString().trim(),
                subscriptionType = selectedSubscriptionType,
                firstName = firstNameEditText.text.toString().trim(),
                lastName = lastNameEditText.text.toString().trim(),
                email = emailRegisterEditText.text.toString().trim(),
                password = passwordRegisterEditText.text.toString().trim(),
                )



            val call = PersonalApiClient.service.registerPersonal(registrationRequest)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    Log.d("API_RESPONSE", "Received status code: ${response.code()}")
                    try {
                        val responseBody = response.body()?.string()
                        Log.d("API_RESPONSE", "Received response: $responseBody")

                        if (response.isSuccessful && responseBody != null) {
                            val successMessage = responseBody.toString()
                            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()

                            requireActivity().getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("username", userNameEditText.text.toString().trim())
                                .apply()

                            val subscriptionPrice = getSubscriptionPrice(selectedSubscriptionType)
                            val intent = Intent(activity, PayScreenActivity::class.java).apply {
                                putExtra("username", userNameEditText.text.toString().trim())
                                putExtra("subscription", subscriptionPrice)
                                putExtra("firstname", firstNameEditText.text.toString().trim())
                                putExtra("lastname", lastNameEditText.text.toString().trim())
                                putExtra("email", emailRegisterEditText.text.toString().trim())
                            }
                            startActivity(intent)

                        } else {

                            if (response.code() == 400) {
                                val errorMessage = response.errorBody()?.string()
                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                            } else {
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Exception in onResponse: ${e.message}", e)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    try {
                        // Handle failure cases as needed
                        // ...
                    } catch (e: Exception) {
                        Log.e("API_FAILURE", "Exception in onFailure: ${e.message}", e)
                    }
                }
            })


        }


        slideToActSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 100) {
                    // Launch the intent when progress is 100
                    val intent = Intent(activity, LoginActivity::class.java)
                    activity?.startActivity(intent)

                }
            }



            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Reset the SeekBar back to start when the touch is released (unless it reached 100)
                if (seekBar?.progress ?: 0 < 100) {
                    seekBar?.progress = 0
                }
            }
        })


    }
}

