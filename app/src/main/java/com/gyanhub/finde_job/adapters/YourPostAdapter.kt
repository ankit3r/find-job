package com.gyanhub.finde_job.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.onClickInterface.YourJobClick
import com.gyanhub.finde_job.databinding.LayoutYourPostCardBinding
import com.gyanhub.finde_job.model.Job

class YourPostAdapter(
    private val context: Context,
    private val listOfJob : LiveData<List<Job>>,
    private val click: YourJobClick
) : RecyclerView.Adapter<YourPostAdapter.YourPostViewHolder>() {
    private var yourPostJobList: List<Job> = emptyList()
    init {
        setData(listOfJob)
    }
    inner class YourPostViewHolder(private val binding: LayoutYourPostCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Job, click: YourJobClick) {
            binding.apply {
                data.apply {
                    txtTitleP.text =
                        context.getString(R.string.job_title_and_state, jobTitle, city, state)
                    txtJobTypeP.text = context.getString(R.string.job_type_and_pay, jobType, pay)
                    txtTotalP.text =
                        context.getString(R.string.job_total_applied, totalApplied.toString())
                }
                btnView.setOnClickListener {
                click.viewApplicant(data.jobId)
                }
                btnDelete.setOnClickListener {
                    click.onClickDelete(data.jobId)
                }
                binding.root.setOnClickListener {
                    click.onClickView(data.jobId)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YourPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutYourPostCardBinding.inflate(inflater, parent, false)
        return YourPostViewHolder(binding)
    }

    override fun getItemCount(): Int = yourPostJobList.size

    override fun onBindViewHolder(holder: YourPostViewHolder, position: Int) = holder.bind(yourPostJobList[position], click)

    private fun setData(jobList: LiveData<List<Job>>){
        jobList.observe((context as LifecycleOwner)) { newData ->
            yourPostJobList = emptyList()
            yourPostJobList=newData
            notifyDataSetChanged()
        }
    }
}