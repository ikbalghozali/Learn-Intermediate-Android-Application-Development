package com.ikbalghozali.storyapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.data.local.UserPreference
import kotlinx.coroutines.launch

class UserViewModel (private val pref: UserPreference) : ViewModel()  {

    fun getUser(): LiveData<User> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}