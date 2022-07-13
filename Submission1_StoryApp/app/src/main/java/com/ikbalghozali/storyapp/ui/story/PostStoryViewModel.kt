package com.ikbalghozali.storyapp.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.data.remote.response.ApiResponse
import com.ikbalghozali.storyapp.data.remote.api.ApiConfig
import com.ikbalghozali.storyapp.utils.ApiCallbackString
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostStoryViewModel : ViewModel() {
  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading


  fun uploadImage(
    user: User,
    description: RequestBody,
    imageMultipart: MultipartBody.Part,
    callback: ApiCallbackString
  ) {
    _isLoading.value = true
      val service = ApiConfig.getApiService().addStories("Bearer ${user.token}", description, imageMultipart)
      service.enqueue(object : Callback<ApiResponse> {
        override fun onResponse(
          call: Call<ApiResponse>,
          response: Response<ApiResponse>
        ) {
          if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null && !responseBody.error) {
              callback.onResponse(response.body() != null, SUCCESS)
            }
          } else {
            Log.e(TAG, "onFailure: ${response.message()}")
            val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
            val message = jsonObject.getString("message")
            callback.onResponse(false, message)
          }
        }
        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
          _isLoading.value = false
          Log.e(TAG, "onFailure: ${t.message}")
          callback.onResponse(false, t.message.toString())
        }
      })

  }

  companion object {
    private const val TAG = "PostStoryViewModel"
    private const val SUCCESS = "success"
  }
}