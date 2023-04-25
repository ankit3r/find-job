package com.gyanhub.finde_job.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.onClickInterface.HomeInterface
import com.gyanhub.finde_job.databinding.JobCardBinding
import com.gyanhub.finde_job.model.Job
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("NotifyDataSetChanged")
class AdapterForHome(
    private val context: Context,
    private val jobList: LiveData<List<Job>>,
    private val yourJobList: List<String>,
    private val appliedJobList: List<String>,
    private val onClick: HomeInterface
) : RecyclerView.Adapter<AdapterForHome.ForHomeViewHolder>() , Filterable {

    private var listOfJob: List<Job> = emptyList()

    init {
      setData(jobList)
    }

    inner class ForHomeViewHolder(private val binding: JobCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: Job, click: HomeInterface) {

            binding.apply {
                job.apply {
                    cardTextTitle.text = jobTitle
                    cardTxtCyName.text =
                        context.getString(R.string.job_title_and_state, jobCyName, city, state)
                    cardTxtJobType.text = context.getString(R.string.job_type_and_pay, jobType, pay)
                    cardTxtPay.text = postDate(timestamp)
                }
            }
            binding.itemCard.setOnClickListener {
                if (job.jobId in appliedJobList) {
                    click.onClick(job.jobId, true)
                } else
                    click.onClick(job.jobId, false)
            }
            binding.itemCard.setOnLongClickListener {
                if (job.jobId in appliedJobList) {
                    click.onReApplied(job.jobId, true)
                } else {
                    click.onClick(job.jobId, false)
                }
                true
            }
        }

        private fun postDate(time: Timestamp): String {
            val timestamp: Timestamp = time
            val postDate = timestamp.toDate()
            val currentDate = Date()
            val dateString =
                when (TimeUnit.DAYS.convert(
                    currentDate.time - postDate.time,
                    TimeUnit.MILLISECONDS
                )) {
                    0L -> "today"
                    1L -> "yesterday"
                    else -> DateUtils.formatDateTime(
                        context,
                        postDate.time,
                        DateUtils.FORMAT_SHOW_DATE
                    )
                }
            return "Posted: $dateString"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForHomeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = JobCardBinding.inflate(inflater, parent, false)
        return ForHomeViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfJob.size

    override fun onBindViewHolder(holder: ForHomeViewHolder, position: Int) =holder.bind(listOfJob[position], onClick)
    
    fun setData(jobList: LiveData<List<Job>>){
        jobList.observe((context as LifecycleOwner)) { newData ->
            listOfJob = emptyList()
            listOfJob=newData
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val query = constraint?.toString()?.trim()?.lowercase(Locale.ROOT) ?: ""
            listOfJob = if (query.isEmpty()) {
                jobList.value.orEmpty()
            } else {
                jobList.value.orEmpty()
                    .filter { it.jobTitle.lowercase(Locale.ROOT).startsWith(query) }
            }
            return FilterResults().apply {
                values = listOfJob
            }
        }


        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val data = results?.values as List<Job>? ?: emptyList()
            listOfJob = data
            notifyDataSetChanged()
        }
    }

}