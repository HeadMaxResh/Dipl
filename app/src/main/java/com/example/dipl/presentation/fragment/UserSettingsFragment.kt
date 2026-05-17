package com.example.dipl.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.dipl.CircleTransformation
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentUserSettingsBinding
import com.example.dipl.domain.dto.UpdatedUserDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class UserSettingsFragment : Fragment() {

    private var _binding: FragmentUserSettingsBinding? = null
    private val binding: FragmentUserSettingsBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentUserSettingsBinding == null")
    private var user: User? = null

    private var selectedImageUri: Uri? = null
    private lateinit var userImage: ImageView
    private val addedImages: ByteArray? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserSettingsBinding.inflate(inflater, container, false)

        user = PrefManager.getUser(requireContext())

        userImage = binding.imageView


        user?.let {
            binding.etName.setText(it.name)
            binding.etSurname.setText(it.surname)
            binding.etEmail.setText(it.email)
            binding.etNumber.setText(it.phone)
            val imagePath = user?.photoUser
            imagePath?.let {
                val file = File(it)
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .transform(CircleTransformation())
                        .into(userImage)
                }
            }

        }

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnImage.setOnClickListener {
            val pickImageIntent = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            pickImageLauncher.launch(pickImageIntent)
        }

        binding.btnSave.setOnClickListener {
            updateUser()

        }

        return binding.root
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                selectedImageUri = data?.data
                selectedImageUri?.let {
                    Picasso.get()
                        .load(it)
                        .transform(CircleTransformation())
                        .into(userImage)
                }
            }
        }

    private fun saveByteArrayToInternalStorage(byteArray: ByteArray): String {
        val context = requireContext()
        val imageHash = byteArray.contentHashCode()
        val fileName = "$imageHash.jpg"

        val file = File(context.filesDir, fileName)
        if (!file.exists()) {
            FileOutputStream(file).use { it.write(byteArray) }
        }
        return file.absolutePath
    }

    private fun getImageByteArrayFromUri(uri: Uri): ByteArray {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: byteArrayOf()
    }

    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        val phonePattern = "^8[0-9]{10}$"
        return phoneNumber.matches(Regex(phonePattern))
    }

    private fun updateUser() {
        user?.let {
            val imagePaths = selectedImageUri?.let { getImageByteArrayFromUri(it) }?.let {
                saveByteArrayToInternalStorage(
                    it
                )
            } ?: it.photoUser

            val name = binding.etName.text.toString().takeIf { it.isNotEmpty() } ?: it.name
            val surname = binding.etSurname.text.toString().takeIf { it.isNotEmpty() } ?: it.surname
            val email = binding.etEmail.text.toString().takeIf { it.isNotEmpty() } ?: it.email
            val phone = binding.etNumber.text.toString().takeIf { it.isNotEmpty() && isPhoneNumberValid(it) } ?: it.phone

            val currentPassword = user?.password ?: ""
            val newPassword =
                binding.etPassword.text.toString().takeIf { it.isNotEmpty() } ?: currentPassword

            val updatedUserDto = UpdatedUserDto(
                name,
                surname,
                email,
                phone,
                newPassword,
                imagePaths
            )

            userApiService.updateUser(it.id, updatedUserDto).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {

                        val updatedUser = response.body()
                        if (updatedUser != null) {
                            PrefManager.updateUser(requireContext(), updatedUser)
                            Toast.makeText(
                                requireContext(),
                                "Данные пользователя обновлены",
                                Toast.LENGTH_SHORT
                            ).show()

                            findNavController().navigateUp()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Не удалось обновить данные пользователя",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось обновить данные пользователя",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Не удалось обновить данные пользователя",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }


}