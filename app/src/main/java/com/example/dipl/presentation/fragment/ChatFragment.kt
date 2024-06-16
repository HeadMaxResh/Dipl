package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.data.api.Api.chatApiService
import com.example.dipl.data.api.Api.userApiService
import com.example.dipl.databinding.FragmentChatBinding
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ChatUserAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatFragment : Fragment(), ChatUserAdapter.OnChatClickListener {

    private var _binding: FragmentChatBinding? = null
    private val binding: FragmentChatBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentChatBinding == null")

    private lateinit var recyclerView: RecyclerView
    private lateinit var chatUserAdapter: ChatUserAdapter

    private var user: User? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        recyclerView = binding.rvChat

        user = PrefManager.getUser(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        getUserChatHistory()

    }

    private fun getUserChatHistory() {
        user?.let {
            chatApiService.getChatHistoryUserIds(it.id).enqueue(object :
                Callback<List<Int>> {
                override fun onResponse(call: Call<List<Int>>, response: Response<List<Int>>) {
                    if (response.isSuccessful) {
                        val userIds = response.body()
                        val userList = mutableListOf<User>()
                        getUsersFromUserIds(userIds, userList)

                    } else {
                        // Обработка ошибки при получении списка userIds
                    }
                }

                override fun onFailure(call: Call<List<Int>>, t: Throwable) {
                    // Обработка ошибки при выполнении запроса
                }
            })
        }
    }

    private fun getUsersFromUserIds(userIds: List<Int>?, userList: MutableList<User>) {
        if (userIds != null) {
            for (userId in userIds) {
                userApiService.getUserById(userId).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            user?.let {
                                userList.add(user)
                                // Проверка, если список пользователей уже содержит всех пользователей
                                if (userList.size == userIds.size) {
                                    setChatAdapter(userList, recyclerView)
                                }
                            }
                        } else {
                            // Обработка ошибки при получении пользователя
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        // Обработка ошибки при выполнении запроса
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
        val action = ChatFragmentDirections.actionChatFragmentToChatDetailFragment(user)
        findNavController().navigate(action)
    }

    /*lateinit var recyclerView: RecyclerView
    private val userListAdapter: UserListAdapter? = null
    lateinit var userList: List<User>

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        //find recyclerView in ChatFragment
        recyclerView = view.findViewById(R.id.rv_chat)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        //find recyclerView in ChatFragment ends
        userList = ArrayList<User>()

//        userAdapter = new UserAdapter(getContext(),userList);
//        recyclerView.setAdapter(userAdapter);
        return view
    }*/
}