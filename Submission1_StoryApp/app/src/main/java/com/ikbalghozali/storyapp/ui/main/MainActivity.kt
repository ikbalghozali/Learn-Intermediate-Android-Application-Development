package com.ikbalghozali.storyapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.databinding.ActivityMainBinding
import com.ikbalghozali.storyapp.ui.ListStoryAdapter
import com.ikbalghozali.storyapp.ui.story.PostStoryActivity
import com.ikbalghozali.storyapp.data.local.UserPreference
import com.ikbalghozali.storyapp.ui.ViewModelFactory

import com.ikbalghozali.storyapp.ui.signIn.SignInActivity
import com.ikbalghozali.storyapp.ui.UserViewModel

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding
    private lateinit var user: User
    private lateinit var adapter: ListStoryAdapter
    private lateinit var mainViewModel: UserViewModel
    private val viewModel by viewModels<MainViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        user = intent.getParcelableExtra(EXTRA_USER)!!
        val name = findViewById<TextView>(R.id.namauser)

        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        mainViewModel.getUser().observe(this) {
            user = User(
                it.name,
                it.token
            )
            name.text = user.name
        }

        setListStory()
        ListStoryAdapter().also { adapter = it }
        showToast()
        setupRecycleView()
        showLoading()
        showNotFound()
    }


    private fun showToast() {
        viewModel.toastText.observe(this) { it ->
            it.getContentIfNotHandled()?.let { toastText ->
                Toast.makeText(
                    this, toastText, Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupRecycleView() {
        binding?.recyclerView?.layoutManager = LinearLayoutManager(this)
        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.adapter = adapter
    }

    private fun showLoading() {
        viewModel.isLoading.observe(this) {
            binding?.apply {
                if (it) {
                    progressBar.visibility = View.VISIBLE
                    recyclerView.visibility = View.INVISIBLE
                } else {
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showNotFound() {
        viewModel.dataFound.observe(this) {
            binding?.apply {
                if (it) {
                    recyclerView.visibility = View.VISIBLE
                    tvNoStory.visibility = View.GONE
                } else {
                    recyclerView.visibility = View.GONE
                    tvNoStory.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setListStory() {
        viewModel.showListStory(user.token)
        viewModel.itemStory.observe(this) {
            adapter.setList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        setListStory()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.optionsmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.post_story -> {
                val moveToAddStoryActivity = Intent(this, PostStoryActivity::class.java)
                moveToAddStoryActivity.putExtra(PostStoryActivity.EXTRA_USER, user)
                startActivity(moveToAddStoryActivity)
            }
            R.id.language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                mainViewModel.logout()
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_USER = "user"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
    }
}