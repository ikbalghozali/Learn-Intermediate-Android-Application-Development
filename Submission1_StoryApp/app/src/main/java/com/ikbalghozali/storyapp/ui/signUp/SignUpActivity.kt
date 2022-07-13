package com.ikbalghozali.storyapp.ui.signUp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.ikbalghozali.storyapp.R
import com.ikbalghozali.storyapp.databinding.ActivitySignUpBinding
import com.ikbalghozali.storyapp.ui.signIn.SignInActivity
import com.ikbalghozali.storyapp.utils.ApiCallbackString
import com.ikbalghozali.storyapp.utils.isEmailValid

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val registerViewModel by viewModels<SignUpViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupAnimation()
        setButtonEnable()
        setEditTextListener()
        setButtonListener()
        showLoading()
    }

    private fun setupAnimation() {
        supportActionBar?.hide()
        val anim = findViewById<ConstraintLayout>(R.id.layoutParent)
        val animDrawable = anim.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(1)
        animDrawable.setExitFadeDuration(3000)
        animDrawable.start()

        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -60f, 60f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()


        val name = ObjectAnimator.ofFloat(binding.etName, View.ALPHA, 1f).setDuration(800)
        val email = ObjectAnimator.ofFloat(binding.etEmail, View.ALPHA, 1f).setDuration(800)
        val pass = ObjectAnimator.ofFloat(binding.etPass, View.ALPHA, 1f).setDuration(800)
        val btnReg = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(800)
        val btnLogin = ObjectAnimator.ofFloat(binding.etSignin, View.ALPHA, 1f).setDuration(800)

        AnimatorSet().apply {
            playSequentially(name, email, pass, btnReg, btnLogin)
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

        binding.etSignin.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
            finish()
        }
    }

    private fun setButtonEnable() {
        binding.btnRegister.isEnabled =
            binding.etEmail.text.toString().isNotEmpty() &&
                    binding.etPass.text.toString().isNotEmpty() &&
                    binding.etPass.text.toString().length >= 6 &&
                    isEmailValid(binding.etEmail.text.toString())
    }

    private fun setButtonListener() {
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()

            registerViewModel.register(name, email, password, object : ApiCallbackString {
                override fun onResponse(success: Boolean, message: String) {
                    if (success) {
                        Toast.makeText(this@SignUpActivity, getString(R.string.signup_success), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(this@SignUpActivity, getString(R.string.signup_failed) + message, Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE

                    }
                }
            })
        }
    }

    private fun showLoading() {
        registerViewModel.isLoading.observe(this) {
            binding.apply {
                if (it) progressBar.visibility = View.VISIBLE
                else progressBar.visibility = View.GONE
            }
        }
    }
}