package com.gyanhub.finde_job.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.model.Job

class HomeAdapter(private val list: List<Job>) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    inner class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title :TextView = view.findViewById(R.id.cardTextTitle)
        val cy :TextView = view.findViewById(R.id.cardTxtCyName)
        val type :TextView = view.findViewById(R.id.cardTxtJobType)
        val pay :TextView = view.findViewById(R.id.cardTxtPay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.job_card, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
       val job = list[position]
        holder.title.text = job.jobTitle
        holder.cy.text = job.jobCyName
        holder.type.text = job.jobType
        holder.pay.text = job.pay
    }

    override fun getItemCount(): Int {
        return list.size
    }
}