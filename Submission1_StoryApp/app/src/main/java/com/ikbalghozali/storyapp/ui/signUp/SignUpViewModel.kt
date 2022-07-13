package com.ikbalghozali.storyapp.ui.signUp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ikbalghozali.storyapp.data.remote.response.ApiResponse
import com.ikbalghozali.storyapp.data.remote.api.ApiConfig
import com.ikbalghozali.storyapp.utils.ApiCallbackString
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpViewModel : ViewModel() {

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  fun register(name: String, email: String, pass: String, callback: ApiCallbackString){
    _isLoading.value = true

    val service = ApiConfig.getApiService().register(name, email, pass)
    service.enqueue(object : Callback<ApiResponse> {
      override fun onResponse(
        call: Call<ApiResponse>,
        response: Response<ApiResponse>
      ) {
        _isLoading.value = false
        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody != null && !responseBody.error)
            callback.onResponse(response.body() != null, SUCCESS)

        } else {
          Log.e(TAG, "onFailure1: ${response.message()}")
          val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
          val message = jsonObject.getString("message")
          callback.onResponse(false, message)
        }
      }

      override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure2: ${t.message}")
        callback.onResponse(false, t.message.toString())
      }
    })
  }

  companion object {
    private const val TAG = "SignUpViewModel"
    private const val SUCCESS = "success"
  }
}