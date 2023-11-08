package com.mattvu.emmurse.payscreen

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.mattvu.emmurse.R
import com.mattvu.emmurse.dashboard.DashboardActivity


class PayScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payscreen)



        val expiryDateEditText: EditText = findViewById(R.id.expiryDateEditText)
        expiryDateEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 2 && count > before) {
                    expiryDateEditText.setText("$s/")
                    expiryDateEditText.setSelection(expiryDateEditText.text.length)
                }
                // Limit to 5 characters (4 numbers and a /)
                if (s?.length ?: 0 > 5) {
                    expiryDateEditText.setText(s?.subSequence(0, 5))
                    expiryDateEditText.setSelection(5)
                }
            }
        })



        val cardNumberEditText: EditText = findViewById(R.id.cardNumberEditText)
        cardNumberEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Remove spaces and store only digits
                val cardNumber = s?.toString()?.replace(" ", "") ?: ""

                val formattedCardNumber = StringBuilder()
                for (i in cardNumber.indices) {
                    formattedCardNumber.append(cardNumber[i])
                    if (i % 4 == 3 && i != cardNumber.length - 1) {
                        formattedCardNumber.append(" ")
                    }
                }

                cardNumberEditText.removeTextChangedListener(this)
                cardNumberEditText.setText(formattedCardNumber)
                cardNumberEditText.setSelection(formattedCardNumber.length)
                cardNumberEditText.addTextChangedListener(this)
            }
        })




        // Retrieve data from intent
        val username = intent.getStringExtra("username")
        val subscription = intent.getStringExtra("subscription")
        val firstname = intent.getStringExtra("firstname")
        val lastname = intent.getStringExtra("lastname")
        val email = intent.getStringExtra("email")

        // Set the username and subscription TextViews
        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        val subscriptionTextView: TextView = findViewById(R.id.subscriptionTextView)
        val firstNameEditText: EditText = findViewById(R.id.firstNameEditText)
        val lastNameEditText: EditText = findViewById(R.id.lastNameEditText)
        val emailEditText: EditText = findViewById(R.id.payPalEmailEditText)

        Log.d("PayScreenActivity", "Username: $username, Subscription: $subscription, First Name: $firstname, Last Name: $lastname")

        usernameTextView.text = "Username: $username"
        subscriptionTextView.text = "Subscription: $subscription"
        emailEditText.setText(email)
        firstNameEditText.setText(firstname)
        lastNameEditText.setText(lastname)

        val creditCardForm: LinearLayout = findViewById(R.id.creditCardForm)
        creditCardForm.visibility = View.VISIBLE

        // Make the PayPal form hidden by default
        val payPalForm: LinearLayout = findViewById(R.id.payPalForm)
        payPalForm.visibility = View.GONE


        val creditCardOption: ImageView = findViewById(R.id.creditCardOption)
        creditCardOption.setOnClickListener {
            toggleVisibility(R.id.creditCardForm, R.id.payPalForm)
        }

        val payPalOption: ImageView = findViewById(R.id.payPalOption)
        payPalOption.setOnClickListener {
            toggleVisibility(R.id.payPalForm, R.id.creditCardForm)
        }

        // Set up the pay button click listener
        val payButton: Button = findViewById(R.id.payButton)
        payButton.setOnClickListener {
            val creditCardForm: View = findViewById(R.id.creditCardForm)
            val payPalForm: View = findViewById(R.id.payPalForm)

            if (creditCardForm.visibility == View.VISIBLE) {
                if (validateCreditCardForm()) {
                    processPayment()
                }
            } else if (payPalForm.visibility == View.VISIBLE) {
                if (validatePayPalForm()) {
                    processPayment()
                }
            } else {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            }
        }


    }
    private fun processPayment() {
        // Assuming payment is successful (as this is a dummy form)
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    // Validate the credit card form
    private fun validateCreditCardForm(): Boolean {
        val cardNumberEditText: EditText = findViewById(R.id.cardNumberEditText)
        val expiryDateEditText: EditText = findViewById(R.id.expiryDateEditText)
        val cvvEditText: EditText = findViewById(R.id.cvvEditText)

        // Add more validation as necessary, such as Luhn check for card number
        if (cardNumberEditText.text.length != 19 ||
            expiryDateEditText.text.length != 5 ||
            cvvEditText.text.length != 3) {
            Toast.makeText(this, "Invalid credit card information", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Validate the PayPal form
    private fun validatePayPalForm(): Boolean {
        val emailEditText: EditText = findViewById(R.id.payPalEmailEditText)
        val passwordEditText: EditText = findViewById(R.id.payPalPassword)

        // Simple validation to check if fields are not empty
        if (emailEditText.text.isBlank() || passwordEditText.text.isBlank()) {
            Toast.makeText(this, "Please enter your PayPal email and password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    private fun toggleVisibility(showViewId: Int, hideViewId: Int) {
        val showView: View = findViewById(showViewId)
        val hideView: View = findViewById(hideViewId)

        showView.visibility = View.VISIBLE
        hideView.visibility = View.GONE
    }
}
