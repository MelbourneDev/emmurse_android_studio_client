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
import com.mattvu.emmurse.register.models.BusinessUserData
import com.mattvu.emmurse.register.models.SubscriptionBusiness
import com.mattvu.emmurse.register.service.BusinessApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusinessFragment : Fragment() {

    // This function is called to create the view for this Fragment.
    // Here, we inflate the layout we defined above.
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_business, container, false)
    }

    // This function is called once the view is created. Here, you can
    // initialize your UI components.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val businessNameEditText: EditText = view.findViewById(R.id.businessName)
        val firstNameEditText: EditText = view.findViewById(R.id.firstName)
        val lastNameEditText: EditText = view.findViewById(R.id.lastName)
        val businessEmailEditText: EditText = view.findViewById(R.id.editTextBusinessEmailAddress)
        val passwordRegisterEditText: EditText = view.findViewById(R.id.editTextTextRegistrationPassword)
        val passwordValidationRegisterEditText: EditText = view.findViewById(R.id.editTextTextPasswordValidation)
        val submitButton: Button = view.findViewById((R.id.registerBusinessAccountButton))
        val slideToActSeekbar: SeekBar = view.findViewById(R.id.slideToActSeekbar)
        val subscriptionSpinner: Spinner = view.findViewById(R.id.subscriptionSpinner)
        val subscriptions = listOf(
            SubscriptionBusiness("hint", "Choose subscription"),
            SubscriptionBusiness("bronze", "Bronze - $150 per month"),
            SubscriptionBusiness("silver", "Silver - $200 per month"),
            SubscriptionBusiness("gold", "Gold - $250 per month")
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
                "gold" -> "$250 per month"
                "silver" -> "$200 per month"
                "bronze" -> "$150 per month"
                "free" -> "FREE"
                else -> "Unknown Subscription"
            }
        }

        submitButton.setOnClickListener {
            // 1. Check for empty fields
            if (businessEmailEditText.text.isNullOrEmpty() ||
                firstNameEditText.text.isNullOrEmpty() ||
                lastNameEditText.text.isNullOrEmpty() ||
                businessEmailEditText.text.isNullOrEmpty() ||
                passwordRegisterEditText.text.isNullOrEmpty() ||
                passwordValidationRegisterEditText.text.isNullOrEmpty()) {
                Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Check for valid email format
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            if (!businessEmailEditText.text.toString().trim().matches(emailPattern.toRegex())) {
                Toast.makeText(context, "Invalid email format!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val password = passwordRegisterEditText.text.toString()
            val passwordValidation = passwordValidationRegisterEditText.text.toString()

            if (password != passwordValidation) {
                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check password complexity (at least one letter, one number, one special character)
            val passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]+$".toRegex()

            if (!password.matches(passwordPattern)) {
                Toast.makeText(context, "Password must contain at least one letter, one number, and one special character.", Toast.LENGTH_SHORT).show()
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

            val registrationRequest = BusinessUserData(
                businessName = businessNameEditText.text.toString().trim(),
                subscriptionType = selectedSubscriptionType,
                firstName = firstNameEditText.text.toString().trim(),
                lastName = lastNameEditText.text.toString().trim(),
                businessEmail = businessEmailEditText.text.toString().trim(),
                password = passwordRegisterEditText.text.toString().trim(),
                )

            // Make API Call
            val call = BusinessApiClient.service.registerBusiness(registrationRequest)
            call.enqueue(object: Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    try {
                        val responseBody = response.body()?.string()
                        Log.d("API_RESPONSE", "Received response: $responseBody")

                        if (response.isSuccessful && responseBody != null) {
                            // Show the toast with the received message
                            Toast.makeText(context, responseBody, Toast.LENGTH_LONG).show()

                            requireActivity().getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
                                .edit()
                                .putString("username", businessNameEditText.text.toString().trim())
                                .apply()

                            // Handle successful registration, e.g., navigating to another activity.
                            val subscriptionPrice = getSubscriptionPrice(selectedSubscriptionType)
                            val intent = Intent(activity, PayScreenActivity::class.java).apply {
                                putExtra("username", businessNameEditText.text.toString().trim())
                                putExtra("subscription", subscriptionPrice)
                                putExtra("firstname", firstNameEditText.text.toString().trim())
                                putExtra("lastname", lastNameEditText.text.toString().trim())
                                putExtra("email", businessEmailEditText.text.toString().trim())
                            }
                            startActivity(intent)
                        } else {
                            // Handle the error case...
                        }
                    } catch (e: Exception) {
                        Log.e("API_ERROR", "Exception in onResponse: ${e.message}", e)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    try {
                        // existing code...
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
