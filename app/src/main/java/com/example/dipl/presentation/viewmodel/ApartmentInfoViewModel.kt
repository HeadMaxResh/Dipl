package com.example.dipl.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diplback.diplserver.model.Review
import com.example.dipl.data.api.Api
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.domain.dto.ApartmentInfoDto
import com.example.dipl.domain.dto.ReviewDto
import com.example.dipl.domain.model.ApartmentInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApartmentInfoViewModel() : ViewModel() {

    private val api = Api.apartmentInfoApiService

    private val _addedApartment = MutableLiveData<ApartmentInfo>()
    val addedApartment: LiveData<ApartmentInfo>
        get() = _addedApartment

    private val selectedApartment = MutableLiveData<ApartmentInfo>()
    fun getSelectedApartment(): LiveData<ApartmentInfo> {
        return selectedApartment
    }

    private val _addedReview= MutableLiveData<Review>()
    val addedReview: LiveData<Review>
        get() = _addedReview

    fun addApartment(apartmentInfoDto: ApartmentInfoDto) {
        api.addApartment(apartmentInfoDto).enqueue(object : Callback<ApartmentInfo> {
            override fun onResponse(call: Call<ApartmentInfo>, response: Response<ApartmentInfo>) {
                if (response.isSuccessful) {
                    _addedApartment.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AddApartmentError", "Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<ApartmentInfo>, t: Throwable) {
                Log.e("AddApartmentFailure", "Error: ${t.message}")
            }
        })
    }

    fun getApartmentById(id: Int) {
        api.getApartmentById(id).enqueue(object : Callback<ApartmentInfo> {
            override fun onResponse(call: Call<ApartmentInfo>, response: Response<ApartmentInfo>) {
                if (response.isSuccessful) {
                    selectedApartment.value = response.body()
                } else {
                    // Обработка ошибки
                    val errorBody = response.errorBody()?.string()
                    Log.e("GetApartmentByIdError", "Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<ApartmentInfo>, t: Throwable) {
                // Обработка ошибки
                Log.e("GetApartmentByIdFailure", "Error: ${t.message}")
            }
        })
    }

    fun addReviewToApartment(apartmentId: Int, reviewDto: ReviewDto) {

        api.addReviewToApartment(apartmentId, reviewDto).enqueue(object : Callback<Review> {
            override fun onResponse(call: Call<Review>, response: Response<Review>) {
                if (response.isSuccessful) {
                    _addedReview.value = response.body()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AddReviewToApartmentError", "Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Review>, t: Throwable) {
                Log.e("AddReviewToApartmentFailure", "Error: ${t.message}")
            }
        })

    }

    fun addToFavorites(userId: Int, apartmentInfo: ApartmentInfo) {
        userApiService.addFavoriteApartment(userId, apartmentInfo.id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    apartmentInfo.isFavorite = true
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Handle failure
            }
        })
    }

    fun removeFromFavorites(userId: Int, apartmentInfo: ApartmentInfo) {
        userApiService.removeFavoriteApartment(userId, apartmentInfo.id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful && response.body() == true) {
                    apartmentInfo.isFavorite = false
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Handle failure
            }
        })
    }

    fun checkIfApartmentIsFavorite(userId: Int, apartmentInfo: ApartmentInfo) {
        userApiService.checkIfApartmentIsFavorite(userId, apartmentInfo.id).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.isSuccessful) {
                    val isFavorite = response.body() ?: false
                    apartmentInfo.isFavorite = isFavorite
                    // Обработка полученного статуса избранного (isFavorite)
                    // Можно обновить UI или сохранить статус в ApartmentInfo или другом месте по вашему усмотрению
                } else {
                    // Обработка неуспешного ответа
                    val errorBody = response.errorBody()?.string()
                    Log.e("CheckFavoriteError", "Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                // Обработка ошибки при выполнении запроса
                Log.e("CheckFavoriteFailure", "Error: ${t.message}")
            }
        })
    }

}
