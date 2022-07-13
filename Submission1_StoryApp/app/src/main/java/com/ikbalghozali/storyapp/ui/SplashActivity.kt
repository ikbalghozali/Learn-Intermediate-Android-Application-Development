package com.ikbalghozali.storyapp.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.data.local.UserPreference
import com.ikbalghozali.storyapp.databinding.ActivitySplashBinding
import com.ikbalghozali.storyapp.ui.main.MainActivity
import com.ikbalghozali.storyapp.ui.signIn.SignInActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var viewModel: UserViewModel
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate((layoutInflater))
        setContentView(binding.root)
        supportActionBar?.hide()
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[UserViewModel::class.java]

        viewModel.getUser().observe(this) {
            user = User(
                it.name,
                it.token
            )
        }

        viewModel.getUser().observe(this) {
            if (it.token.isEmpty()) {
                binding.imageView.alpha= 0f
                binding.imageView.animate().setDuration(2000L).alpha(1f).withEndAction {
                    val i = Intent(this, SignInActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }
            } else {
                binding.imageView.alpha= 0f
                binding.imageView.animate().setDuration(2000L).alpha(1f).withEndAction {
                    val moveToListStoryActivity = Intent(this, MainActivity::class.java)
                    moveToListStoryActivity.putExtra(MainActivity.EXTRA_USER, user)
                    startActivity(moveToListStoryActivity)
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()
                }
            }
        }
    }
    }