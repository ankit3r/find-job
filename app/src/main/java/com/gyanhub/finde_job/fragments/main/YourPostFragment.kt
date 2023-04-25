package com.gyanhub.finde_job.fragments.main
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.activity.HolderActivity
import com.gyanhub.finde_job.adapters.YourPostAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.YourJobClick
import com.gyanhub.finde_job.databinding.FragmentYourPostBinding
import com.gyanhub.finde_job.utils.UserResult
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel
class YourPostFragment : Fragment(), YourJobClick {
    private var _binding: FragmentYourPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbModel: DbViewModel
    private lateinit var authModel: AuthViewModel
    private lateinit var list: List<String>
    private lateinit var adapter: YourPostAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentYourPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        authModel = ViewModelProvider(this)[AuthViewModel::class.java]
        list = listOf()
        getYourPost()
        binding.btnPostJob.setOnClickListener {
            val intent = Intent(context, HolderActivity::class.java)
            intent.putExtra("f", 5)
            requireActivity().startActivity(intent)

        }
    }

    override fun onResume() {
        super.onResume()
        getYourPost()
    }

    override fun onDestroy() {
        dbModel.life = false
        super.onDestroy()
    }

    private fun deleteItemOption(id: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Item")
        builder.setMessage("Are you sure you want to delete this item?")
        builder.setCancelable(false)
        builder.setPositiveButton("Yes") { dialogs, _ ->
            dbModel.showProgressBar()
            dbModel.deleteJob(id) { success, _ ->
                if (success) {
                    getYourPost()
                    dbModel.hideProgressBar()
                    Toast.makeText(context, "Item delete ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Item deleting failed", Toast.LENGTH_SHORT).show()
                    dbModel.hideProgressBar()
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

    override fun onClickView(id: String) {
        val intent = Intent(context, HolderActivity::class.java)
        intent.putExtra("f", 1)
        intent.putExtra("id", id)
        requireActivity().startActivity(intent)
    }

    override fun onClickDelete(id: String) {
        deleteItemOption(id)
    }

    override fun viewApplicant(id: String) {
        val intent = Intent(context, HolderActivity::class.java)
        intent.putExtra("f", 4)
        intent.putExtra("id", id)
        requireActivity().startActivity(intent)
    }

    private fun getYourPost() {
        dbModel.showProgressBar()
        val user = authModel.getUser(requireActivity())
        user.observe(viewLifecycleOwner) { u ->
            when (u) {
                is UserResult.Success -> {
                    dbModel.getYourJobs(u.user.job) { s, e ->
                        if (s) {
                            if (dbModel.life) {
                                adapter = YourPostAdapter(requireContext(), dbModel.yourJob, this)
                                binding.rcYourPost.adapter = adapter
                                binding.textView.visibility = View.GONE
                                dbModel.hideProgressBar()
                            }
                        } else {
                            binding.textView.visibility = View.VISIBLE
                            binding.textView.text = e
                            dbModel.hideProgressBar()
                        }
                    }
                }
                is UserResult.Error -> {
                    Toast.makeText(requireContext(), "User Data Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}