package com.gyanhub.finde_job.fragments.main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.HolderActivity
import com.gyanhub.finde_job.activity.comp.CheckPhoNo.Companion.isValidMobileNumber
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.adapters.AdapterForHome
import com.gyanhub.finde_job.adapters.onClickInterface.HomeInterface
import com.gyanhub.finde_job.databinding.FragmentHomeBinding
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.utils.UserResult
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel
import com.gyanhub.finde_job.viewModle.MainViewModel

class HomeFragment : Fragment(), HomeInterface {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var filterLayout: RelativeLayout
    private lateinit var filter: CardView
    private lateinit var filter1: Spinner
    private lateinit var filter2: Spinner
    private lateinit var filter3: Spinner
    private lateinit var searchView: SearchView
    private lateinit var jobModel: DbViewModel
    private lateinit var mainModel: MainViewModel
    private lateinit var authModel: AuthViewModel
    private lateinit var adapter: AdapterForHome
    private lateinit var dialog :AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        jobModel = ViewModelProvider(this)[DbViewModel::class.java]
        mainModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        authModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        adapter = AdapterForHome(requireContext(), jobModel.getJob, jobModel.yourJoblist,
            jobModel.appliedJobList, this
        )
        dialog = AlertDialog.Builder(context).create()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        filterLayout = requireActivity().findViewById(R.id.filterLayout)
        filter = requireActivity().findViewById(R.id.fllterBtn)
        filter1 = requireActivity().findViewById(R.id.btnFilter1)
        filter2 = requireActivity().findViewById(R.id.btnFilter2)
        filter3 = requireActivity().findViewById(R.id.btnFilter3)
        setRecyclear()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = true
            setRecyclear()
            binding.swipeRefreshLayout.isRefreshing = false
        }
        setUpDropDownFilter()
        filter.setOnClickListener {
            if (mainModel.filterByJobType != "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByState != "All"
            ) {
                jobModel.multiplFieldFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByState,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (
                mainModel.filterByJobType != "All"
                && mainModel.filterByPay == "All"
                && mainModel.filterByState != "All"
            ) {
                jobModel.multiplFieldFilter(
                    0,
                    mainModel.filterByState,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (
                mainModel.filterByJobType == "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByState != "All"
            ) {

                jobModel.doubleValueFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByState,
                    jobModel.stateField
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (
                mainModel.filterByState == "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByJobType != "All"
            ) {

                jobModel.doubleValueFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByJobType,
                    jobModel.typeField

                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (
                mainModel.filterByJobType == "All"
                && mainModel.filterByPay == "All"
                && mainModel.filterByState == "All"
            ) {
                jobModel.getAllJob()
            } else if (mainModel.filterByJobType != "All") {

                jobModel.singlFieldFilter(
                    jobModel.typeField,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (mainModel.filterByPay != "All") {
                jobModel.filterPay(
                    jobModel.payField,
                    mainModel.filterByPay.toInt()
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (this.mainModel.filterByState != "All") {
                jobModel.singlFieldFilter(
                    jobModel.stateField,
                    mainModel.filterByState
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.hintSearchMess)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                hideKeyboard()
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnFilter -> {
                if (filterLayout.visibility == View.VISIBLE) filterLayout.visibility =
                    View.GONE
                else filterLayout.visibility = View.VISIBLE
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpDropDownFilter() {
        val spinnerItems = mutableListOf<State>()
        spinnerItems.add(0, State("a", "All"))
        val jsonString =
            context?.assets?.open("States.json")?.bufferedReader().use { it?.readText() }
        val itemType = object : TypeToken<List<State>>() {}.type
        spinnerItems.addAll(Gson().fromJson(jsonString, itemType))


        dropdown(filter1, jobModel.listOfJobType) { success, data ->
            if (success) {
                mainModel.filterByJobType = data
            }
        }
        dropdown(filter2, jobModel.listOfPay) { success, data ->
            if (success) {
                mainModel.filterByPay = data
            }

        }
        dropdown(filter3, spinnerItems.map { it.name }) { success, data ->
            if (success) {
                mainModel.filterByState = data
            }

        }
    }

    private fun dropdown(view: Spinner, list: List<String>, callback: (Boolean, String) -> Unit) {
        hideKeyboard()
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

    override fun onDestroy() {
        filterLayout.visibility = View.GONE
        jobModel.life = false
        super.onDestroy()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun itemClickOption(id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("This Job Posted By You")
        builder.setMessage("What do you want? View Or Delete Job")
        builder.setCancelable(false)
        builder.setPositiveButton("View") { dialog, _ ->
            val intent = Intent(context, HolderActivity::class.java)
            intent.putExtra("f", 1)
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
        builder.setTitle("Delete Job")
        builder.setMessage("Are you sure you want to delete this Job?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialogs, _ ->
            jobModel.deleteJob(id) { success, _ ->
                if (success) {
                    jobModel.getAllJob()
                    adapter.setData(jobModel.getJob)
                } else {
                    Toast.makeText(context, "Item deleting failed", Toast.LENGTH_SHORT).show()
                }

            }
            dialogs.dismiss()
        }

        builder.setNegativeButton("No") { dialogs, _ ->
            dialogs.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onClick(id: String, applied: Boolean) {
        if (applied) {
            Toast.makeText(context, "You have already tried to apply this one if you want Reapply hold job", Toast.LENGTH_LONG).show() }
        else { if (id in jobModel.yourJoblist) { itemClickOption(id) } else {
                    val intent = Intent(context, HolderActivity::class.java)
                    intent.putExtra("f", 0)
                    intent.putExtra("id", id)
                    requireActivity().startActivity(intent)
            } }
    }

    override fun onReApplied(id: String, applied: Boolean) {
        val intent = Intent(context, HolderActivity::class.java)
        intent.putExtra("f", 2)
        intent.putExtra("id", id)
        requireActivity().startActivity(intent)
    }

    private fun phoneNoDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update_phone, null)
        val dialogInput = dialogView.findViewById<EditText>(R.id.editTextNewPhone)
        val dialogOkButton = dialogView.findViewById<Button>(R.id.buttonUpdatePhone)
         dialog.setView(dialogView)
        dialog.setCancelable(false)
        dialogOkButton.setOnClickListener {
            val newPhoneNumber = dialogInput.text.toString()
            if (isValidMobileNumber(newPhoneNumber)) {
                authModel.updatePhNo(newPhoneNumber, requireContext())
            } else dialogInput.error = "Invalid Mobile No"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setRecyclear() {
        jobModel.getAllJob()
        authModel.getUser(requireActivity()).observe(viewLifecycleOwner) { u ->
            when (u) {
                is UserResult.Success -> {
                    val data = u.user
                    jobModel.yourJoblist = data.job
                    jobModel.appliedJobList = data.youApply
                    jobModel.resume = data.resume
                    jobModel.getJob.observe(viewLifecycleOwner) {
                        jobModel.list = it
                        adapter = AdapterForHome(requireContext(), jobModel.getJob, jobModel.yourJoblist,
                            jobModel.appliedJobList, this
                        )
                        binding.rcJob.adapter = adapter
                    }
                    if (u.user.phNo.isEmpty())
                        phoneNoDialog()
                }

                is UserResult.Error -> {
                    Toast.makeText(requireContext(), "User Data Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setRecyclear()
    }

}