package com.ikbalghozali.storyapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ikbalghozali.storyapp.data.local.UserPreference

import com.ikbalghozali.storyapp.ui.signIn.SignInViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    return when {
      modelClass.isAssignableFrom(UserViewModel::class.java) -> {
        UserViewModel(pref) as T
      }
      modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
        SignInViewModel(pref) as T
      }
      else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
  }
}