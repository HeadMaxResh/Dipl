package com.example.dipl.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.diplback.diplserver.model.Ein
import com.diplback.diplserver.model.Passport
import com.example.dipl.R
import com.example.dipl.data.api.Api.einApiService
import com.example.dipl.data.api.Api.passportApiService
import com.example.dipl.databinding.FragmentAddBinding
import com.example.dipl.domain.dto.ApartmentInfoDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ImageAddSliderAdapter
import com.example.dipl.presentation.viewmodel.ApartmentInfoViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding: FragmentAddBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentAddBinding == null")

    private val apartmentInfoViewModel: ApartmentInfoViewModel by viewModels()

    private lateinit var viewPager: ViewPager
    private lateinit var imageAdapter: ImageAddSliderAdapter
    private val addedImages = mutableListOf<ByteArray>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            selectedImageUri?.let {
                val imageByteArray = getImageByteArrayFromUri(selectedImageUri)
                if (!addedImages.contains(imageByteArray)) {
                    //addedImages.add(imageByteArray)
                    imageAdapter.addImage(imageByteArray)
                }
            }
        }
    }

    private fun getImageByteArrayFromUri(uri: Uri): ByteArray {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        binding.btnAddApartment.setOnClickListener {
            performAddApartment()
        }

        viewPager = binding.vpAddImage
        imageAdapter = ImageAddSliderAdapter(viewPager, addedImages)
        viewPager.adapter = imageAdapter

        setupCitySpinner()

        binding.btnAddImage.setOnClickListener {
            val pickImageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickImageLauncher.launch(pickImageIntent)
        }

        return binding.root
    }

    private fun performAddApartment() {
        /*val name = binding.etName.text.toString()
        val city = binding.spinnerCity.selectedItem.toString()
        val rent = binding.etRent.text.toString().toInt()
        val selectedRadioButtonId = binding.rgCountRooms.checkedRadioButtonId
        val selectedRadioButton =
            binding.rgCountRooms.findViewById<RadioButton>(selectedRadioButtonId)
        val countRooms = selectedRadioButton?.text.toString().toInt()
        val area = binding.etArea.text.toString().toFloat()
        val description = binding.etDescription.text.toString()*/

        val user = PrefManager.getUser(requireContext())

        user?.let { currentUser ->
            // Проверяем, что у пользователя есть паспорт и ЕИН
            val userId = currentUser.id
            passportApiService.getPassportByUserId(userId).enqueue(object : Callback<Passport> {
                override fun onResponse(call: Call<Passport>, response: Response<Passport>) {
                    val passport = response.body()
                    if (passport != null) {
                        // Паспорт найден, теперь проверяем ЕИН
                        einApiService.getEinByUserId(userId).enqueue(object : Callback<Ein> {
                            override fun onResponse(call: Call<Ein>, response: Response<Ein>) {
                                val ein = response.body()
                                if (ein != null) {

                                    if (setApartment(user)) return

                                } else {
                                    showError("Добавьте в профиле Инн")
                                }
                            }

                            override fun onFailure(call: Call<Ein>, t: Throwable) {
                                showError("Добавьте в профиле Инн")
                            }
                        })
                    } else {
                        showError("Добавьте в профиле паспорт")
                    }
                }

                override fun onFailure(call: Call<Passport>, t: Throwable) {
                    showError("Добавьте в профиле паспорт")
                }
            })
        }
    }

    private fun setApartment(user: User?): Boolean {
        val name =
            binding.etName.text.toString().takeIf { it.isNotBlank() }
                ?: run {
                    showError("Введите название")
                    return true
                }

        val city = binding.spinnerCity.selectedItem.toString()
            .takeIf { it.isNotBlank() } ?: run {
            showError("Выберите город")
            return true
        }

        val rent = binding.etRent.text.toString().takeIf {
            it.isNotBlank()
        }?.toIntOrNull() ?: run {
            showError("Неверный формат стоимости аренды")
            return true
        }

        val selectedRadioButtonId =
            binding.rgCountRooms.checkedRadioButtonId
        val selectedRadioButton =
            binding.rgCountRooms.findViewById<RadioButton>(
                selectedRadioButtonId
            )
        val countRooms =
            selectedRadioButton?.text.toString().toIntOrNull() ?: run {
                showError("Выберите количество комнат")
                return true
            }

        val area = binding.etArea.text.toString().takeIf {
            it.isNotBlank()
        }?.toFloatOrNull() ?: run {
            showError("Неверный формат площади")
            return true
        }

        val description = binding.etDescription.text.toString()
            .takeIf { it.isNotBlank() } ?: run {
            showError("Введите описание")
            return true
        }
        val imagePaths = if (addedImages.isNotEmpty()) {
            addedImages.map { saveByteArrayToInternalStorage(it) }
        } else {
            showError("Добавьте изображения")
            return true
        }

        user?.let {
            val apartmentInfoDto = ApartmentInfoDto(
                name,
                city,
                rent,
                0.0,
                area,
                imagePaths,
                countRooms,
                it,
                description
            )
            apartmentInfoViewModel.addApartment(apartmentInfoDto)
        }
        return false
    }

    private fun saveByteArrayToInternalStorage(byteArray: ByteArray): String {

        val context = requireContext()
        val imageHash = byteArray.contentHashCode() // Получаем хэш-код массива байтов
        val fileName = "$imageHash.jpg"

        val file = File(context.filesDir, fileName)
        if (!file.exists()) { // Проверяем, не существует ли уже файл с таким именем
            FileOutputStream(file).use { it.write(byteArray) }
        }
        return file.absolutePath
        /*val context = requireContext()
        val copiedByteArray = byteArray.copyOf()
        val file = File(context.filesDir, "${UUID.randomUUID()}.jpg")
        FileOutputStream(file).use { it.write(copiedByteArray) }
        return file.absolutePath*/
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun setupCitySpinner() {
        val russianCityNames = resources.getStringArray(R.array.city_names)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            russianCityNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCity.adapter = adapter
    }
}