package com.gyanhub.finde_job.activity.comp


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.gyanhub.finde_job.R


class CustomSpinner(context: Context, items: List<String>) :
    ArrayAdapter<String>(context, R.layout.custom_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(R.id.spinner_item_text)
        textView.text = getItem(position)
        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(R.id.spinner_item_text)
        textView.setTextColor(context.resources.getColor(R.color.black))
        textView.text = getItem(position)
        return view
    }
}