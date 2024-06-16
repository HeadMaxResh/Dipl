package com.example.dipl.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentSettingsBinding
import com.example.dipl.databinding.FragmentUserSettingsBinding
import com.example.dipl.domain.dto.UpdatedUserDto
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.PrefManager.updateUser
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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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

    private fun updateUser() {

        val imagePaths = selectedImageUri?.let { getImageByteArrayFromUri(it) }?.let {
            saveByteArrayToInternalStorage(
                it
            )
        }

        val updatedUserDto = UpdatedUserDto(
            binding.etName.text.toString(),
            binding.etSurname.text.toString(),
            binding.etEmail.text.toString(),
            binding.etNumber.text.toString(),
            binding.etPassword.text.toString(),
            imagePaths
        )

        user?.let {
            userApiService.updateUser(it.id, updatedUserDto).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {

                        val updatedUser = response.body()
                        if (updatedUser != null) {
                            // Обновляем пользователя в PrefManager
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