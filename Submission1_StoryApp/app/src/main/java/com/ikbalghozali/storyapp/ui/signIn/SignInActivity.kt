package com.ikbalghozali.storyapp.ui.signIn

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.data.local.UserPreference
import com.ikbalghozali.storyapp.databinding.ActivitySignInBinding
import com.ikbalghozali.storyapp.ui.ViewModelFactory
import com.ikbalghozali.storyapp.ui.main.MainActivity
import com.ikbalghozali.storyapp.ui.signUp.SignUpActivity
import com.ikbalghozali.storyapp.utils.ApiCallbackString
import com.ikbalghozali.storyapp.utils.isEmailValid

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class SignInActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        signInViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[SignInViewModel::class.java]

        signInViewModel.getUser().observe(this) {
            user = User(
                it.name,
                it.token
            )
        }
        setupAnimation()
        setButtonEnable()
        setEditTextListener()
        setButtonListener()
        showLoading()
    }

    private fun setupAnimation() {
        supportActionBar?.hide()
        val anim = findViewById<ConstraintLayout>(R.id.constraintLayout)
        val animDrawable = anim.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(1)
        animDrawable.setExitFadeDuration(3000)
        animDrawable.start()

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -60f, 60f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(800)
        val pass = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(800)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(800)
        val btnReg = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(800)

        AnimatorSet().apply {
            playSequentially(email, pass, btnLogin, btnReg)
            startDelay = 800
        }.start()
    }

    private fun setEditTextListener() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.etPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setButtonEnable()
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
            finish()
        }
    }

    private fun setButtonEnable() {
        val resultPass = binding.etPass.text
        val resultEmail = binding.etEmail.text

        binding.btnSignIn.isEnabled = resultPass != null && resultEmail != null &&
                binding.etPass.text.toString().length >= 6 &&
                isEmailValid(binding.etEmail.text.toString())
    }

    private fun setButtonListener() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val pass = binding.etPass.text.toString()

            signInViewModel.login(email, pass, object : ApiCallbackString {
                override fun onResponse(success: Boolean, message: String) {
                    if (success) {
                        Toast.makeText(this@SignInActivity, getString(R.string.signin_success), Toast.LENGTH_LONG)
                            .show()
                        val moveToListStoryActivity =
                            Intent(this@SignInActivity, MainActivity::class.java)
                        moveToListStoryActivity.putExtra(MainActivity.EXTRA_USER, user)
                        startActivity(moveToListStoryActivity)
                        finish()
                    } else {
                        Toast.makeText(this@SignInActivity, getString(R.string.signin_failed) + message, Toast.LENGTH_LONG).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun showLoading() {
        signInViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) progressBar.visibility = View.VISIBLE
                else progressBar.visibility = View.GONE
            }
        }
    }

}