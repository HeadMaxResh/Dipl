package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.data.api.Api.analysisApiService
import com.example.dipl.databinding.FragmentAddAddressBinding
import com.example.dipl.presentation.utils.toJsonObject
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddAddressFragment : Fragment() {

    private var _binding: FragmentAddAddressBinding? = null
    private val binding: FragmentAddAddressBinding
        get() = _binding ?: throw RuntimeException("FragmentAddAddressBinding == null")

    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)

        setupCitySpinner()
        setupClickListeners()

        return binding.root
    }

    private fun setupCitySpinner() {
        val cityNames = resources.getStringArray(R.array.city_names)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            cityNames
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            validateAndAnalyzeAddress()
        }
    }

    private fun validateAndAnalyzeAddress() {
        val city = binding.spinnerCity.selectedItem?.toString()?.trim().orEmpty()

        val address = binding.etAddress.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val apartmentNumber = binding.etApartmentNumber.text
            ?.toString()
            ?.trim()
            .orEmpty()

        val selectedRadioButtonId = binding.rgCountRooms.checkedRadioButtonId

        val selectedRadioButton = if (selectedRadioButtonId != -1) {
            binding.rgCountRooms.findViewById<RadioButton>(selectedRadioButtonId)
        } else {
            null
        }

        val rooms = selectedRadioButton
            ?.text
            ?.toString()
            ?.toIntOrNull()

        val area = binding.etArea.text
            ?.toString()
            ?.trim()
            ?.replace(",", ".")
            ?.toFloatOrNull()

        val cadastr = binding.etCadastr.text
            ?.toString()
            ?.trim()
            .orEmpty()

        if (city.isBlank()) {
            showError("Выберите город")
            return
        }

        if (address.isBlank()) {
            showError("Введите адрес дома")
            return
        }

        if (apartmentNumber.isBlank()) {
            showError("Введите номер квартиры")
            return
        }

        if (rooms == null) {
            showError("Выберите количество комнат")
            return
        }

        if (area == null || area <= 0f) {
            showError("Введите корректную площадь")
            return
        }

        if (cadastr.length != 12) {
            showError("Введите корректный кадастровый номер")
            return
        }

        sharedViewModel.city = city
        sharedViewModel.address = address
        sharedViewModel.apartmentNumber = apartmentNumber
        sharedViewModel.rooms = rooms
        sharedViewModel.area = area
        sharedViewModel.cadastr = cadastr

        analyzeLocation(sharedViewModel.fullAddress())
    }

    private fun analyzeLocation(fullAddress: String) {
        setLoading(true)

        analysisApiService.analyzeLocation(fullAddress)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    setLoading(false)

                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        val json = body.toJsonObject()

                        sharedViewModel.geoAnalysis = json

                        showAnalysisDialog(
                            title = "Геоанализ адреса",
                            message = buildGeoAnalysisMessage(json)
                        ) {
                            findNavController().navigate(
                                R.id.action_addAddressFragment_to_addDescriptionFragment
                            )
                        }
                    } else {
                        showError("Не удалось выполнить геоанализ")
                    }
                }

                override fun onFailure(
                    call: Call<ResponseBody>,
                    t: Throwable
                ) {
                    setLoading(false)
                    showError("Ошибка подключения: ${t.message}")
                }
            })
    }

    private fun buildGeoAnalysisMessage(json: JsonObject): String {
        val qualityLevel = json.get("qualityLevel")?.asString ?: "-"

        val scores = json.getAsJsonObject("scores")
        val locationScore = scores?.get("locationScore")?.asString ?: "-"
        val transportScore = scores?.get("transportScore")?.asString ?: "-"
        val educationScore = scores?.get("educationScore")?.asString ?: "-"
        val healthcareScore = scores?.get("healthcareScore")?.asString ?: "-"

        val priceImpact = json.getAsJsonObject("priceImpact")
        val coefficient = priceImpact?.get("coefficient")?.asString ?: "-"
        val impactDescription = priceImpact?.get("description")?.asString ?: ""

        val nearest = json.getAsJsonObject("nearestInfrastructure")

        val busStop = nearest
            ?.getAsJsonArray("busStops")
            ?.firstOrNull()
            ?.asJsonObject

        val school = nearest
            ?.getAsJsonArray("schools")
            ?.firstOrNull()
            ?.asJsonObject

        val kindergarten = nearest
            ?.getAsJsonArray("kindergartens")
            ?.firstOrNull()
            ?.asJsonObject

        val hospital = nearest
            ?.getAsJsonArray("hospitals")
            ?.firstOrNull()
            ?.asJsonObject

        val shop = nearest
            ?.getAsJsonArray("shops")
            ?.firstOrNull()
            ?.asJsonObject

        val recommendations = json
            .getAsJsonArray("recommendations")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "Рекомендации отсутствуют"

        return """
            Уровень локации: $qualityLevel
            Итоговая оценка: $locationScore
            
            Оценки:
            Транспорт: $transportScore
            Образование: $educationScore
            Медицина: $healthcareScore
            
            Ближайшие объекты:
            Остановка: ${formatNearest(busStop)}
            Школа: ${formatNearest(school)}
            Детский сад: ${formatNearest(kindergarten)}
            Больница: ${formatNearest(hospital)}
            Магазин: ${formatNearest(shop)}
            
            Коэффициент влияния на цену: $coefficient
            $impactDescription
            
            Нюансы и рекомендации:
            $recommendations
        """.trimIndent()
    }

    private fun formatNearest(obj: JsonObject?): String {
        if (obj == null) return "не найдено"

        val name = obj.get("name")?.asString ?: "Без названия"
        val distance = obj.get("distanceMeters")?.asString ?: "-"

        return "$name, $distance м"
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnNext.isEnabled = !isLoading

        binding.btnNext.text = if (isLoading) {
            "Анализируем..."
        } else {
            "Далее"
        }
    }

    private fun showAnalysisDialog(
        title: String,
        message: String,
        onContinue: () -> Unit
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Продолжить") { _, _ ->
                onContinue()
            }
            .setNegativeButton("Остаться", null)
            .show()
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}