package com.example.dipl.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dipl.data.api.Api
import com.example.dipl.data.api.service.UserApiService
import com.example.dipl.domain.dto.UserDto
import com.example.dipl.domain.request.RegistrationRequest
import com.example.dipl.domain.responce.AuthenticationResponse
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class RegistrationViewModel : ViewModel() {

    private val api = Api.userApiService

    sealed class RegisterResult {
        data class Success(val response: Response<Boolean>) : RegisterResult()
        data class Error(val message: String) : RegisterResult()
    }

    private val _registerResult: MutableLiveData<RegisterResult> = MutableLiveData()
    val registerResult: LiveData<RegisterResult> = _registerResult

    fun register(name: String, surname: String, phone: String, password: String) {
        val userDto = UserDto(name, surname, phone, password)
        viewModelScope.launch {
            try {
                val response = api.registerUser(userDto)
                _registerResult.value = RegisterResult.Success(response)
            } catch (e: Exception) {
                _registerResult.value = RegisterResult.Error("Ошибка при выполнении запроса")
            }
        }
    }
}