package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.data.api.Api
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentChatBinding
import com.example.dipl.databinding.FragmentModerChatBinding
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ChatUserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModerChatFragment : Fragment(), ChatUserAdapter.OnChatClickListener {

    private var _binding: FragmentModerChatBinding? = null
    private val binding: FragmentModerChatBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentModerChatBinding == null")

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatUserAdapter: ChatUserAdapter

    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentModerChatBinding.inflate(layoutInflater, container, false)
        recyclerView = binding.rvChat

        user = PrefManager.getUser(requireContext())

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        getAllUsers()
    }

    private fun getAllUsers() {
        userApiService.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val userList = response.body() ?: emptyList()
                    val filteredUserList = userList.filter { it.id != user?.id }
                    setChatAdapter(filteredUserList, recyclerView)
                } else {
                    Log.d("getAllUsers", "getAllUsers")
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Log.d("getAllUsers", "getAllUsers")
            }
        })
    }

    private fun getUsersFromUserIds(userIds: List<Int>?, userList: MutableList<User>) {
        if (userIds != null) {
            for (userId in userIds) {
                Api.userApiService.getUserById(userId).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            user?.let {
                                userList.add(user)
                                if (userList.size == userIds.size) {
                                    setChatAdapter(userList, recyclerView)
                                }
                            }
                        } else {
                            Log.d("getUsersFromUserIds", "getUsersFromUserIds")
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.d("getUsersFromUserIds", "getUsersFromUserIds")
                    }
                })
            }
        }
    }

    private fun setChatAdapter(
        userList: List<User>,
        recyclerView: RecyclerView
    ) {
        chatUserAdapter = ChatUserAdapter(userList)
        chatUserAdapter.setOnChatClickListener(this)
        recyclerView.adapter = chatUserAdapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onChatClick(user: User) {
        val action = ModerChatFragmentDirections.actionModerChatFragmentToChatDetailFragment2(user)
        findNavController().navigate(action)
    }

}