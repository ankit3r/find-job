package com.gyanhub.finde_job.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.onClickInterface.AppliedClick
import com.gyanhub.finde_job.databinding.LayoutAppliedCarBinding
import com.gyanhub.finde_job.model.Job

class AppliedAdapter(private val context:Context,private val appliedJobList: List<Job>,private val click:AppliedClick)
    :RecyclerView.Adapter<AppliedAdapter.AppliedViewHolder>(){
    inner class AppliedViewHolder(private val binding: LayoutAppliedCarBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(data: Job, click: AppliedClick) {
          binding.apply {
            data.apply {
                txtTitleA.text = context.getString(R.string.job_type_and_state, jobTitle, state)
                txtJobTypeA.text = context.getString(R.string.job_type_and_state, jobType, pay)
                txtTotalA.text = context.getString(R.string.job_total_applied,totalApplied.toString())
            }
              btnViewResponse.setOnClickListener {
                 click.onClick(data.whNo)
              }
          }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppliedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutAppliedCarBinding.inflate(inflater, parent, false)
        return AppliedViewHolder(binding)
    }

    override fun getItemCount(): Int {
      return appliedJobList.size
    }

    override fun onBindViewHolder(holder: AppliedViewHolder, position: Int) {
        holder.bind(appliedJobList[position],click)
    }
}