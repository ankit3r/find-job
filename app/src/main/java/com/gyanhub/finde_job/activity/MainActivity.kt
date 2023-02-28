package com.gyanhub.finde_job.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.ActivityMainBinding
import com.gyanhub.finde_job.fragments.main.AppliedFragment
import com.gyanhub.finde_job.fragments.main.HomeFragment
import com.gyanhub.finde_job.fragments.main.MenuFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFragment(HomeFragment())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                   setFragment(HomeFragment())
                    true
                }
                R.id.navigation_applied -> {
                    setFragment(AppliedFragment())
                    true
                }
                R.id.navigation_menu -> {
                    setFragment(MenuFragment())
                    true
                }
                else -> false
            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logout, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = resources.getString(R.string.hintSearchMess)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, "enter: $query", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Toast.makeText(this@MainActivity, "enter: $newText", Toast.LENGTH_SHORT).show()
                return false
            }

        } )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnFilter -> {
                Toast.makeText(this, "click on filter", Toast.LENGTH_SHORT).show()
                if (binding.filterLayout.visibility == View.VISIBLE) binding.filterLayout.visibility =
                    View.GONE
                else binding.filterLayout.visibility = View.VISIBLE
            }
            R.id.btnProfile -> {
                Toast.makeText(this, "click on profile", Toast.LENGTH_SHORT).show()

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameLayout, fragment)
        fragmentTransient.commit()
    }

}