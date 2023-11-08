package com.mattvu.emmurse.contact_us

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.mattvu.emmurse.R
// Define a ChatFragment class that extends DialogFragment
class ChatFragment : DialogFragment() {

    companion object {
        // Factory method to create a new instance of ChatFragment with a greeting message
        fun newInstance(greeting: String): ChatFragment {
            val fragment = ChatFragment()
            val args = Bundle()
            args.putString("greeting", greeting)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Find the chat layout in the fragment's view
        val chatLayout = view.findViewById<LinearLayout>(R.id.chatLayout) // Make sure the ID matches your XML
        // Retrieve the user's username from shared preferences
        val sharedPreferences = this.activity?.getSharedPreferences("emmurse_prefs", Context.MODE_PRIVATE)
        val username = sharedPreferences?.getString("username", "User")
        val botGreeting = "Hello, $username!\nWhat can I help you with? Select an option:" +
                "\n1. Upcoming events with eMmerse" +
                "\n2. Where to find suitable hardware for eMmerse VR experiences" +
                "\n3. Contact Support" +
                "\n4. Job opportunities"
         // Add the bot greeting message to the chat layout
        addMessageToLayout(botGreeting, false, chatLayout)
         // Find UI elements in the fragment's view
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val getUserInput = view.findViewById<EditText>(R.id.getUserInput)
         // Set a click listener for the send button
        btnSend.setOnClickListener {
            val userInput = getUserInput.text.toString()
            getUserInput.setText("")

            // Add user message to the layout
            addMessageToLayout(userInput, true, chatLayout)

            // Handle the bot response here
            val botResponse = when (userInput) {
                "1" -> "To keep up to date with all our latest event news, follow us on our socials found in the home dashboard."
                "2" -> "All good electronic shops now store VR headsets suitable for eMmerse events."
                "3" -> "To contact support, write a message in the chat box in contact us and an eMmerse official representative will contact you via email as soon as possible."
                "4" -> "To find out the latest eMmerse job opportunities, follow us on LinkedIn."
                else -> "Sorry, I do not understand the question, please choose from one of the following options."
            }

            // Add bot response to the layout
            addMessageToLayout(botResponse, false, chatLayout)
        }
         // Set an editor action listener for the user input field
        getUserInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val userInput = getUserInput.text.toString()
                getUserInput.setText("")

                // Add user message to the layout
                addMessageToLayout(userInput, true, chatLayout)

                true
            } else {
                false
            }
        }
         // Find and set a click listener for the leave chat button
        val btnLeaveChat = view.findViewById<Button>(R.id.btnLeaveChat)
        btnLeaveChat.setOnClickListener {
            dismiss()
        }
    }
    // Function to add a message to the chat layout
    private fun addMessageToLayout(message: String, isUser: Boolean, chatLayout: LinearLayout) {
        val layoutInflater = LayoutInflater.from(context)
        val messageLayout = if (isUser) {
            layoutInflater.inflate(R.layout.user_message_layout, null)
        } else {
            layoutInflater.inflate(R.layout.bot_message_layout, null)
        }
        // Find the message text view in the message layout
        val textView = messageLayout.findViewById<TextView>(R.id.messageTextView)
        textView.text = message
        // Set layout parameters for the message layout
        val layoutParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, // This line ensures the message takes up to 75% of the screen width
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = if (isUser) Gravity.END else Gravity.START
            setMargins(20, 10, 20, 10) // You can adjust the margins here as per your design needs
        }

        messageLayout.layoutParams = layoutParam
        chatLayout.addView(messageLayout)
    }

}
