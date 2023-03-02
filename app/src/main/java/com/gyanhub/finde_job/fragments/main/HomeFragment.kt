package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.adapters.HomeAdapter
import com.gyanhub.finde_job.databinding.FragmentHomeBinding
import com.gyanhub.finde_job.viewModle.DbViewModel

class HomeFragment() : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var filterLayout: RelativeLayout
    private lateinit var searchView: SearchView
    private lateinit var jobModel: DbViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        setHasOptionsMenu(true)
        filterLayout = requireActivity().findViewById(R.id.filterLayout)
        jobModel = ViewModelProvider(this)[DbViewModel::class.java]




        jobModel.getJob.observe(viewLifecycleOwner){
            binding.rcJob.adapter = HomeAdapter(it)
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

    private fun dropdown(view: Spinner, list: List<String>) {
        val adapter = CustomSpinner(requireActivity(), list)
        view.adapter = adapter
        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(context, list[position], Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
    }

}