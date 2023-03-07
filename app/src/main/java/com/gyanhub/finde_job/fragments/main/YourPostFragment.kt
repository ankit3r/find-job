package com.gyanhub.finde_job.fragments.main


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.HolderActivity
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.adapters.HomeAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.HomeInterface
import com.gyanhub.finde_job.databinding.FragmentYourPostBinding
import com.gyanhub.finde_job.databinding.PostJobBottomBinding
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel


class YourPostFragment : Fragment(), HomeInterface {
    private lateinit var binding: FragmentYourPostBinding
    private lateinit var dbModel: DbViewModel
    private lateinit var bottomBinding: PostJobBottomBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var authModel: AuthViewModel
    private lateinit var progressBar: AlertDialog
    private lateinit var list: List<String>
    private lateinit var jobType: String
    private lateinit var state: String
    private var life = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        life = true
        binding = FragmentYourPostBinding.inflate(layoutInflater, container, false)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        authModel = ViewModelProvider(this)[AuthViewModel::class.java]
        progressBar()
        list = listOf()

        dbModel.progressBarVisible.observe(viewLifecycleOwner) { visible ->
            if (visible) progressBar.show() else progressBar.dismiss()
        }
        dbModel.showProgressBar()
        authModel.getUser { success, user, error ->
            if (success && !user?.job.isNullOrEmpty()) {
                dbModel.getYourJobs(user!!.job) { s, e ->
                    if (s) {
                        if (life) {
                            dbModel.yourJob.observe(viewLifecycleOwner) {
                                val dapter = HomeAdapter(requireContext(),it, this)
                                binding.rcYourPost.adapter = dapter
                                binding.textView.visibility = View.GONE
                                dbModel.hideProgressBar()

                            }
                        }
                    } else {
                        binding.textView.visibility = View.VISIBLE
                        binding.textView.text = e
                        dbModel.hideProgressBar()
                    }
                }
            } else  dbModel.hideProgressBar()
        }

        binding.btnPostJob.setOnClickListener {
            bottomSheet()
        }
        return binding.root
    }

    private fun bottomSheet() {

        val spinnerItems = mutableListOf<State>()
        val jsonString =
            context?.assets?.open("States.json")?.bufferedReader().use { it?.readText() }

        val itemType = object : TypeToken<List<State>>() {}.type
        spinnerItems.addAll(Gson().fromJson(jsonString, itemType))


        bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomBinding = PostJobBottomBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomBinding.root)
        dropdown(bottomBinding.type, listOf("Internship", "Job", "Part Time")) { success, data ->
            if (success) {
                jobType = data
            }
        }
        dropdown(bottomBinding.dState, spinnerItems.map { it.name }) { success, data ->
            if (success) {
                state = data
            }
        }

        bottomBinding.btnPostJob.setOnClickListener {
            if (!checkFiled(bottomBinding.eTxtTitle)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtCyName)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtxCity)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtPay)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtTotalPost)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtDisc)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtSkill)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtWCA)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtWhNo)) return@setOnClickListener
            else {
                val isValid = isValidMobileNumber(bottomBinding.eTxtWhNo.text.toString())
                if (!isValid) {
                    bottomBinding.eTxtWhNo.error = "Invalid Mobile No"
                    return@setOnClickListener
                }
            }
            //  converting skill into list
            dbModel.showProgressBar()
            val skillList = mutableListOf<String>()
            val tags = bottomBinding.eTxtSkill.text.toString().split(Regex("\\s*,\\s*"))
            for (tag in tags) {
                skillList.add(tag)
            }
            val firstNumber = bottomBinding.eTxtPay.text.toString().substringBefore("-")
            // posting job in firebase

            dbModel.postJob(
                bottomBinding.eTxtTitle.text.toString(),
                bottomBinding.eTxtCyName.text.toString(),
                bottomBinding.eTxtTotalPost.text.toString(),
                bottomBinding.eTxtWhNo.text.toString(),
                bottomBinding.eTxtDisc.text.toString(),
                bottomBinding.eTxtWCA.text.toString(),
                skillList,
                bottomBinding.eTxtPay.text.toString(),
                firstNumber.toInt(),
                jobType,
                state,
                bottomBinding.eTxtxCity.text.toString()
            ) { success, error ->
                if (success) {
                    bottomSheetDialog.dismiss()
                    dbModel.hideProgressBar()

                } else {
                    Log.d("ANKIT", "Error $error")
                    dbModel.hideProgressBar()
                }

            }
        }
        bottomSheetDialog.show()
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

    private fun checkFiled(view: EditText): Boolean {
        if (view.text.toString().isEmpty()) {
            view.error = "Required"
            return false
        }
        return true
    }

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        progressBar = builder.create()

    }

    override fun onDestroy() {
        life = false
        super.onDestroy()
    }

    fun isValidMobileNumber(mobileNumber: String): Boolean {
        val regex = Regex("^[6-9]\\d{9}\$")
        return regex.matches(mobileNumber)
    }

    override fun onClick(id: String) {
        itemClickOption(id)
    }

    private fun itemClickOption(id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Item View")
        builder.setMessage("What do you want? View Or Delete Item")
        builder.setCancelable(false)
        builder.setPositiveButton("View") { dialog, which ->
            val intent = Intent(context, HolderActivity::class.java)
            intent.putExtra("f", 0)
            intent.putExtra("id", id)
            requireActivity().startActivity(intent)
            dialog.dismiss()
        }



        builder.setNegativeButton("Delete") { dialog, _ ->
            deleteItemOption(id)
            dialog.dismiss()
        }

        builder.setNeutralButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteItemOption(id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialogs, which ->
            dbModel.showProgressBar()
            dbModel.deleteJob(id) { success, error ->
                if (success) {
                    dbModel.hideProgressBar()
                    Toast.makeText(context, "Item delete ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Item deleting failed", Toast.LENGTH_SHORT).show()
                    dbModel.hideProgressBar()
                }

            }
            dialogs.dismiss()
        }

        builder.setNegativeButton("No") { dialogs, which ->
            dialogs.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }


}