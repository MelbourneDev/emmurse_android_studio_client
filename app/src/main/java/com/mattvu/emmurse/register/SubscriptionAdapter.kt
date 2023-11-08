package com.mattvu.emmurse.register

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SubscriptionAdapter(context: Context, resource: Int, objects: List<String>) : ArrayAdapter<String>(context, resource, objects) {

    override fun getCount(): Int {
        return super.getCount()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (position == 0) {
            // Return an empty view for the hint
            view = TextView(context)
            view.height = 0
        } else {
            view = super.getDropDownView(position, null, parent)
        }
        return view
    }
}
