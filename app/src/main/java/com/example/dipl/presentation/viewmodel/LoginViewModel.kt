package com.example.dipl.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.dipl.data.api.Api
import com.example.dipl.domain.dto.UserDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import kotlinx.coroutines.launch

class LoginViewModel (application: Application) : AndroidViewModel(application) {

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    private val _enterResult: MutableLiveData<LoginResult> = MutableLiveData()
    val enterResult: LiveData<LoginResult> = _enterResult

    lateinit var loggedUser: User

    fun auth(phone: String, password: String) {
        viewModelScope.launch {
            try {
                val api = Api.userApiService
                val userDto = UserDto("", "", phone, password)

                val response = api.loginUser(userDto)

                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        PrefManager.saveUser(getApplication(), user)
                        PrefManager.setLoggedInState(getApplication(), true)

                        _enterResult.value = LoginResult.Success(user)
                    } else {
                        _enterResult.value = LoginResult.Error("Получен пустой ответ от сервера")
                    }
                } else {
                    _enterResult.value = LoginResult.Error("Неверный пароль или номер")
                }
            } catch (e: Exception) {
                _enterResult.value = LoginResult.Error("Ошибка авторизации")
            }
        }
    }
}