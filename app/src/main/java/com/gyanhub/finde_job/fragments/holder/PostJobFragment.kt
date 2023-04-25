package com.gyanhub.finde_job.fragments.holder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.CheckPhoNo.Companion.isValidMobileNumber
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.databinding.FragmentPostJobBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class PostJobFragment : Fragment() {
    private var _binding :FragmentPostJobBinding? =null
    private val binding get() = _binding!!
    private lateinit var dbModel: DbViewModel
    private lateinit var authModel: AuthViewModel
    private lateinit var jobType: String
    private lateinit var state: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View{
       _binding = FragmentPostJobBinding.inflate(layoutInflater,container,false)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        authModel = ViewModelProvider(this)[AuthViewModel::class.java]
        return binding .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       if (isAdded) requireActivity().title = getString(R.string.post_job)
        postJob()
    }

    private fun postJob() {
        val spinnerItems = mutableListOf<State>()
        val jsonString = context?.assets?.open("States.json")?.bufferedReader().use { it?.readText() }
        val itemType = object : TypeToken<List<State>>() {}.type
        spinnerItems.addAll(Gson().fromJson(jsonString, itemType))

        dropdown(binding.type, listOf("Internship", "Job", "Part Time")) { success, data ->
            if (success) jobType = data
        }
        dropdown(binding.dState, spinnerItems.map { it.name }) { success, data ->
            if (success) state = data
        }
        val loading = LoaderClass(requireContext())
        loading.loading("Job Posting")
        binding.btnPostJob.setOnClickListener {
            if (!checkInputFiled()) return@setOnClickListener
            else { val isValid = isValidMobileNumber(binding.eTxtWhNo.text.toString())
                if (!isValid) { binding.eTxtWhNo.error = "Invalid Mobile No"
                    return@setOnClickListener
                }
            }
            //  converting skill into list

            val skillList = mutableListOf<String>()
            val tags = binding.eTxtSkill.text.toString().split(Regex("\\s*,\\s*"))
            for (tag in tags) { skillList.add(tag) }
            val firstNumber = binding.eTxtPay.text.toString().substringBefore("-")
            // posting job in firebase
            loading.showLoder()
            dbModel.postJob(
                binding.eTxtTitle.text.toString(),
                binding.eTxtCyName.text.toString(),
                binding.eTxtTotalPost.text.toString(),
                binding.eTxtWhNo.text.toString(),
                binding.eTxtDisc.text.toString(),
                binding.eTxtWCA.text.toString(),
                skillList,
                binding.eTxtPay.text.toString(),
                firstNumber.toInt(),
                jobType,
                state,
                binding.eTxtxCity.text.toString()
            ) { success, _ ->
                if (success) {
                    clearFiled()
                    Toast.makeText(requireContext(), "Job Posted!!", Toast.LENGTH_SHORT).show()
                    loading.hideLoder()
                }
                else {
                    Toast.makeText(requireContext(), "Job Posting failed", Toast.LENGTH_SHORT).show()
                    loading.hideLoder()
                }
            }
        }
    }

    private fun checkFiled(view: EditText): Boolean {
        if (view.text.toString().isEmpty()) {
            view.error = "Required"
            return false
        }
        return true
    }

    private fun dropdown(view: Spinner, list: List<String>, callback: (Boolean, String) -> Unit) {
        val adapter = CustomSpinner(requireActivity(), list)
        view.adapter = adapter
        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                callback(true, list[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                callback(false, "")
            }
        }
    }

    private fun checkInputFiled():Boolean{
        if (!checkFiled(binding.eTxtTitle)) return false
        if (!checkFiled(binding.eTxtCyName)) return false
        if (!checkFiled(binding.eTxtxCity)) return false
        if (!checkFiled(binding.eTxtPay)) return false
        if (!checkFiled(binding.eTxtTotalPost)) return false
        if (!checkFiled(binding.eTxtDisc)) return false
        if (!checkFiled(binding.eTxtSkill)) return false
        if (!checkFiled(binding.eTxtWCA)) return false
        if (!checkFiled(binding.eTxtWhNo)) return false
        return true
    }

    private fun clearFiled(){
        binding.eTxtTitle.text = null
        binding.eTxtCyName.text = null
        binding.eTxtxCity.text = null
        binding.eTxtPay.text = null
        binding.eTxtTotalPost.text = null
        binding.eTxtDisc.text = null
        binding.eTxtSkill.text = null
        binding.eTxtWCA.text = null
        binding.eTxtWhNo.text = null
    }
}