package com.example.dipl.presentation.fragment.screen

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dipl.data.api.Api.analysisApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AddApartmentComposeViewModel(
    private val app: Application
) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(AddApartmentState())
    val state: StateFlow<AddApartmentState> = _state

    fun addPhotos(uris: List<Uri>) {
        val current = _state.value.photoUris
        val unique = (current + uris).distinct()
        _state.value = _state.value.copy(photoUris = unique)
    }

    fun removePhoto(uri: Uri) {
        _state.value = _state.value.copy(
            photoUris = _state.value.photoUris - uri
        )
    }

    fun updateAddress(
        city: String,
        address: String,
        apartmentNumber: String,
        rooms: Int,
        area: Float,
        cadastr: String
    ) {
        _state.value = _state.value.copy(
            city = city,
            address = address,
            apartmentNumber = apartmentNumber,
            rooms = rooms,
            area = area,
            cadastr = cadastr
        )
    }

    fun updateDescription(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    fun analyzePhotos(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val parts = _state.value.photoUris.mapIndexed { index, uri ->
                    uri.toMultipartPart(app, "photos", "photo_$index.jpg")
                }

                val response = analysisApiService.analyzePhotos(parts)

                /*if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        imageAnalysis = response.body()
                    )
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                    onError("Не удалось выполнить анализ фотографий")
                }*/
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                onError(e.message ?: "Ошибка анализа фотографий")
            }
        }
    }

    fun analyzeLocation(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val response = analysisApiService.analyzeLocation(
                    _state.value.fullAddress(),
                    _state.value.rooms,
                    _state.value.area.toDouble()
                )

                /*if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        geoAnalysis = response.body()
                    )
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                    onError("Не удалось выполнить геоанализ")
                }*/
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                onError(e.message ?: "Ошибка геоанализа")
            }
        }
    }

    fun analyzeText(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)

                val response = analysisApiService.analyzeText(
                    _state.value.description
                )

                /*if (response.isSuccessful && response.body() != null) {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        textAnalysis = response.body()
                    )
                    onSuccess()
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                    onError("Не удалось выполнить анализ описания")
                }*/
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                onError(e.message ?: "Ошибка анализа описания")
            }
        }
    }

    fun calculatePrice(
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val current = _state.value

                val text = current.textAnalysis
                val image = current.imageAnalysis
                val geo = current.geoAnalysis

                if (text == null || image == null || geo == null) {
                    onError("Не все результаты анализа получены")
                    return@launch
                }

                _state.value = current.copy(isLoading = true)

                val request = CalculateFromAnalysisRequest(
                    area = current.area,
                    rooms = current.rooms,
                    textAnalysis = text,
                    imageAnalysis = image,
                    geoAnalysis = geo
                )

                //val response = analysisApiService.calculateFromAnalysis(request)

                /*if (response.isSuccessful && response.body() != null) {
                    val price = response.body()!!

                    _state.value = _state.value.copy(
                        isLoading = false,
                        priceAnalysis = price,
                        finalRent = price.price.recommendedPrice
                    )
                } else {
                    _state.value = _state.value.copy(isLoading = false)
                    onError("Не удалось рассчитать стоимость")
                }*/
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
                onError(e.message ?: "Ошибка расчета стоимости")
            }
        }
    }

    fun updateFinalRent(value: Int) {
        _state.value = _state.value.copy(finalRent = value)
    }
}