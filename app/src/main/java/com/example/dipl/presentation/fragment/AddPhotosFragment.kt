package com.example.dipl.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.R
import com.example.dipl.data.api.Api.analysisApiService
import com.example.dipl.databinding.FragmentAddPhotosBinding
import com.example.dipl.presentation.adapter.ImageAddSliderAdapter
import com.example.dipl.presentation.toPhotoPart
import com.example.dipl.presentation.utils.toJsonObject
import com.example.dipl.presentation.viewmodel.AddApartmentSharedViewModel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddPhotosFragment : Fragment() {

    private var _binding: FragmentAddPhotosBinding? = null
    private val binding: FragmentAddPhotosBinding
        get() = _binding ?: throw RuntimeException("FragmentAddPhotosBinding == null")



    private val sharedViewModel: AddApartmentSharedViewModel by activityViewModels()

    private lateinit var imageAdapter: ImageAddSliderAdapter
    private val addedImages = mutableListOf<ByteArray>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data

            selectedImageUri?.let { uri ->
                val imageBytes = getImageByteArrayFromUri(uri)

                if (imageBytes.isNotEmpty()) {
                    //addedImages.add(imageBytes)
                    imageAdapter.addImage(imageBytes)
                    updatePhotosCount()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPhotosBinding.inflate(inflater, container, false)

        setupImagesSlider()
        setupClickListeners()

        return binding.root
    }

    private fun setupImagesSlider() {
        imageAdapter = ImageAddSliderAdapter(binding.vpAddImage, addedImages)
        binding.vpAddImage.adapter = imageAdapter
        updatePhotosCount()
    }

    private fun setupClickListeners() {
        binding.btnAddImage.setOnClickListener {
            openGallery()
        }

        binding.btnNext.setOnClickListener {
            analyzePhotos()
        }
    }

    private fun openGallery() {
        val pickImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        pickImageLauncher.launch(pickImageIntent)
    }

    private fun analyzePhotos() {
        if (addedImages.isEmpty()) {
            showError("Добавьте хотя бы одно фото")
            return
        }

        sharedViewModel.photos.clear()
        sharedViewModel.photos.addAll(addedImages)

        val photoParts = addedImages.mapIndexed { index, bytes ->
            bytes.toPhotoPart(index)
        }

        setLoading(true)

        analysisApiService.analyzePhotos(photoParts)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    setLoading(false)

                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        val json = body.toJsonObject()

                        sharedViewModel.imageAnalysis = json

                        showAnalysisDialog(
                            title = "Анализ фотографий",
                            message = buildImageAnalysisMessage(json)
                        ) {
                            findNavController().navigate(
                                R.id.action_addFragment_to_photoAnalysisResultFragment
                            )
                        }
                    } else {
                        showError("Не удалось выполнить анализ фотографий")
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

    private fun buildImageAnalysisMessage(json: JsonObject): String {
        val summary = json.getAsJsonObject("apartmentSummary")

        val score = summary?.get("averageVisualScore")?.asString ?: "-"
        val level = summary?.get("visualQualityLevel")?.asString ?: "-"
        val repair = summary?.get("dominantRepairQuality")?.asString ?: "-"
        val furniture = summary?.get("dominantFurnitureCondition")?.asString ?: "-"
        val furnishing = summary?.get("dominantFurnitureLevel")?.asString ?: "-"

        val rooms = summary
            ?.getAsJsonArray("detectedRooms")
            ?.joinToString(", ") { it.asString }
            ?: "-"

        val recommendations = summary
            ?.getAsJsonArray("recommendations")
            ?.joinToString("\n") { "• ${it.asString}" }
            ?: "Рекомендации отсутствуют"

        return """
            Визуальная оценка: $score
            Уровень качества: $level
            
            Обнаруженные помещения:
            $rooms
            
            Ремонт: $repair
            Состояние мебели: $furniture
            Наполненность мебелью: $furnishing
            
            Нюансы и рекомендации:
            $recommendations
        """.trimIndent()
    }

    private fun getImageByteArrayFromUri(uri: Uri): ByteArray {
        return requireContext()
            .contentResolver
            .openInputStream(uri)
            ?.use { it.readBytes() }
            ?: byteArrayOf()
    }

    private fun updatePhotosCount() {
        binding.tvPhotosCount.text = "Добавлено фото: ${addedImages.size}"
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnNext.isEnabled = !isLoading
        binding.btnAddImage.isEnabled = !isLoading

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