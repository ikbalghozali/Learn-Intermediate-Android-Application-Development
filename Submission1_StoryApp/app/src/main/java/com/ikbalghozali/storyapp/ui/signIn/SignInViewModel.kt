package com.ikbalghozali.storyapp.ui.signIn

import android.util.Log
import androidx.lifecycle.*
import com.ikbalghozali.storyapp.data.local.User
import com.ikbalghozali.storyapp.data.local.UserPreference
import com.ikbalghozali.storyapp.data.remote.response.LoginResponse
import com.ikbalghozali.storyapp.data.remote.api.ApiConfig
import com.ikbalghozali.storyapp.utils.ApiCallbackString
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val pref: UserPreference) : ViewModel() {

  private val _isLoading = MutableLiveData<Boolean>()
  val isLoading: LiveData<Boolean> = _isLoading

  fun login(email: String, pass: String, callback: ApiCallbackString){
    _isLoading.value = true
    val service = ApiConfig.getApiService().login(email, pass)
    service.enqueue(object : Callback<LoginResponse> {
      override fun onResponse(
        call: Call<LoginResponse>,
        response: Response<LoginResponse>
      ) {
        if (response.isSuccessful) {
          val responseBody = response.body()
          if (responseBody != null && !responseBody.error) {
            callback.onResponse(response.body() != null, SUCCESS)
            val model = User(
              responseBody.loginResult.name,
              responseBody.loginResult.token
            )
            saveUser(model)
          }
        } else {
          Log.e(TAG, "onFailure: ${response.message()}")
          val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
          val message = jsonObject.getString("message")
          callback.onResponse(false, message)
        }
      }

      override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
        _isLoading.value = false
        Log.e(TAG, "onFailure: ${t.message}")
        callback.onResponse(false, t.message.toString())
      }
    })
  }

  fun getUser(): LiveData<User> {
    return pref.getUser().asLiveData()
  }

  fun saveUser(user: User) {
    viewModelScope.launch {
      pref.saveUser(user)
    }
  }

  companion object {
    private const val TAG = "SignInViewModel"
    private const val SUCCESS = "success"
  }
}