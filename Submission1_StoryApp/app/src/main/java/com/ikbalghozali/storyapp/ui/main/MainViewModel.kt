package com.ikbalghozali.storyapp.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.ikbalghozali.storyapp.data.remote.response.AllStoriesResponse
import com.ikbalghozali.storyapp.data.remote.response.ListStoryItem
import com.ikbalghozali.storyapp.data.remote.api.ApiConfig
import com.ikbalghozali.storyapp.utils.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainViewModel : ViewModel() {
    private val _itemStory = MutableLiveData<List<ListStoryItem>>()
    val itemStory: LiveData<List<ListStoryItem>> = _itemStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _dataFound = MutableLiveData<Boolean>()
    val dataFound: LiveData<Boolean> = _dataFound

    private val toast = MutableLiveData<Event<String>>()
    val toastText: LiveData<Event<String>> = toast

    fun showListStory(token: String) {
        _isLoading.value = true
        _dataFound.value = true
        val client = ApiConfig.getApiService().getAllStories("Bearer $token")
        client.enqueue(object : Callback<AllStoriesResponse> {
            override fun onResponse(call: Call<AllStoriesResponse>, response: Response<AllStoriesResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (!responseBody.error) {
                            _itemStory.value = response.body()?.listStory
                            _dataFound.value = responseBody.message == "Stories fetched successfully"
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    toast.value = Event(response.message())
                }
            }

            override fun onFailure(call: Call<AllStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
                toast.value = Event(t.message.toString())
            }
        })
    }

    companion object {
        private const val TAG = "ListStoryViewModel"
    }
}
