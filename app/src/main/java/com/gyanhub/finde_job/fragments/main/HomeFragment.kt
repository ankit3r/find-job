package com.gyanhub.finde_job.fragments.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
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
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.adapters.HomeAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.HomeInterface
import com.gyanhub.finde_job.databinding.FragmentHomeBinding
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.viewModle.DbViewModel
import com.gyanhub.finde_job.viewModle.MainViewModel

class HomeFragment() : Fragment(),HomeInterface {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var filterLayout: RelativeLayout
    private lateinit var filter: CardView
    private lateinit var filter1: Spinner
    private lateinit var filter2: Spinner
    private lateinit var filter3: Spinner
    private lateinit var searchView: SearchView
    private lateinit var jobModel: DbViewModel
    private lateinit var mainModel: MainViewModel
    private lateinit var adapter: HomeAdapter
    private lateinit var dialog: AlertDialog

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        filterLayout = requireActivity().findViewById(R.id.filterLayout)
        filter = requireActivity().findViewById(R.id.fllterBtn)
        filter1 = requireActivity().findViewById(R.id.btnFilter1)
        filter2 = requireActivity().findViewById(R.id.btnFilter2)
        filter3 = requireActivity().findViewById(R.id.btnFilter3)
        jobModel = ViewModelProvider(this)[DbViewModel::class.java]
        mainModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        progressBar()
        jobModel.getJob.observe(viewLifecycleOwner) {
            adapter = HomeAdapter(it,this)
            binding.rcJob.adapter = adapter
            dialog.dismiss()
        }
        setUpDropDownFilter()
        filter.setOnClickListener {
            val payField = "filterPay"
            val stateField = "state"
            val typeField = "jobType"
            dialog.show()
            if (mainModel.filterByJobType != "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByState != "All"
            ) {
                massage("All Filed Filter")
                jobModel.multiplFieldFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByState,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (
                mainModel.filterByJobType != "All"
                && mainModel.filterByPay == "All"
                && mainModel.filterByState != "All"
            ) {
                massage("job type or state")
                jobModel.multiplFieldFilter(
                    0,
                    mainModel.filterByState,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (
                mainModel.filterByJobType == "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByState != "All"
            ) {
                massage("pay or state")
                jobModel.doubleValueFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByState,
                    stateField
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (
                mainModel.filterByState == "All"
                && mainModel.filterByPay != "All"
                && mainModel.filterByJobType != "All"
            ) {
                massage("pay or job type")
                jobModel.doubleValueFilter(
                    mainModel.filterByPay.toInt(),
                    mainModel.filterByJobType,
                    typeField

                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (
                mainModel.filterByJobType == "All"
                && mainModel.filterByPay == "All"
                && mainModel.filterByState == "All"
            ) {
                massage("no filter")
                jobModel.getAllJob()
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }
            else if (mainModel.filterByJobType != "All") {
                massage("job type")
                jobModel.singlFieldFilter(
                    typeField,
                    mainModel.filterByJobType
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (mainModel.filterByPay != "All") {
                massage("Pay")
                jobModel.filterPay(
                    payField,
                    mainModel.filterByPay.toInt()
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
            else if (this.mainModel.filterByState != "All") {
                massage("state")
                jobModel.singlFieldFilter(
                    stateField,
                    mainModel.filterByState
                ) { success, data, error ->
                    if (success) {
                        jobModel.data.postValue(data)
                        Toast.makeText(context, "filtered", Toast.LENGTH_SHORT).show()
                        adapter.notifyDataSetChanged()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }
            }
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.hintSearchMess)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(context, "enter: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(context, "enter: $newText", Toast.LENGTH_SHORT).show()
                return false
            }

        })

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnFilter -> {
                Toast.makeText(context, "click on filter", Toast.LENGTH_SHORT).show()
                if (filterLayout.visibility == View.VISIBLE) filterLayout.visibility =
                    View.GONE
                else filterLayout.visibility = View.VISIBLE
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    private fun setUpDropDownFilter() {
        val spinnerItems = mutableListOf<State>()
        spinnerItems.add(0, State("a", "All"))
        val jsonString =
            context?.assets?.open("States.json")?.bufferedReader().use { it?.readText() }
        val itemType = object : TypeToken<List<State>>() {}.type
        spinnerItems.addAll(Gson().fromJson(jsonString, itemType))
        dropdown(
            filter1,
            listOf("All", "Internship", "Job", "Part Time")
        ) { success, data ->
            if (success) {
                mainModel.filterByJobType = data
            }

        }
        dropdown(
            filter2,
            listOf("All", "5000", "10000", "15000", "20000")
        ) { success, data ->
            if (success) {
                mainModel.filterByPay = data
            }

        }
        dropdown(
            filter3,
            spinnerItems.map { it.name }
        ) { success, data ->
            if (success) {
                mainModel.filterByState = data
            }

        }
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

    private fun massage(massage:String){
        Toast.makeText(context, massage, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
         filterLayout.visibility = View.GONE
        super.onDestroy()
    }

    override fun onClick(id: String) {
        val intent = Intent(context,HolderActivity::class.java)
        intent.putExtra("f",0)
        intent.putExtra("id",id)
        requireActivity().startActivity(intent)
    }

}