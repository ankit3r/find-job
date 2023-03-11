package com.gyanhub.finde_job.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.onClickInterface.HomeInterface
import com.gyanhub.finde_job.model.Job
import java.util.*
import java.util.concurrent.TimeUnit

class HomeAdapter(private val context: Context, private val jobList: List<Job>, private val yourJobList: List<String>,private val appliedJobList: List<String>, private val onClick:HomeInterface) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(),Filterable {

    private var filteredList = jobList.toMutableList()


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
        val job = filteredList[position]
        if(job.jobId in yourJobList){
            holder.itemView.background = ColorDrawable(R.drawable.ic_launcher_background)
        }
        holder.title.text = job.jobTitle
        holder.cy.text = job.jobCyName
        holder.type.text = job.jobType
        holder.pay.text = postDate(job.timestamp)
        holder.itemView.setOnClickListener {
            if (job.jobId in appliedJobList){
                onClick.onClick(job.jobId,true)
            }else
                onClick.onClick(job.jobId,false)
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val query = constraint.toString().lowercase(Locale.ROOT).trim()
                filteredList = if (query.isEmpty()) {
                    jobList.toMutableList()
                } else {
                    jobList.filter { it.jobTitle.lowercase(Locale.ROOT).startsWith(query) }.toMutableList()
                }
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as MutableList<Job>
                notifyDataSetChanged()
            }
        }
    }

    private fun postDate(time:Timestamp):String{
        val timestamp: Timestamp = time
        val postDate = timestamp.toDate()
        val currentDate = Date()

        val daysDiff = TimeUnit.DAYS.convert(currentDate.time - postDate.time, TimeUnit.MILLISECONDS)

        val dateString = when (daysDiff) {
            0L -> "today"
            1L -> "yesterday"
            else -> DateUtils.formatDateTime(context, postDate.time, DateUtils.FORMAT_SHOW_DATE)
        }

       return "Posted $dateString"
    }



}