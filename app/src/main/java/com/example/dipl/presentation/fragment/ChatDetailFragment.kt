package com.example.dipl.presentation.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.config.InternetAddress
import com.example.dipl.data.api.Api.chatApiService
import com.example.dipl.databinding.FragmentChatBinding
import com.example.dipl.databinding.FragmentChatDetailBinding
import com.example.dipl.domain.dto.MessageDto
import com.example.dipl.domain.model.Chat
import com.example.dipl.domain.model.Message
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.PrefManager
import com.example.dipl.presentation.adapter.ChatMessageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatDetailFragment : Fragment() {

    private var _binding: FragmentChatDetailBinding? = null
    private val binding: FragmentChatDetailBinding
        get() = _binding ?: throw java.lang.RuntimeException("FragmentChatDetailBinding == null")

    private val args: ChatDetailFragmentArgs by navArgs()
    private var userSender: User? = null
    private var userReceiver: User? = null
    private var handler: Handler? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatMessageAdapter: ChatMessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(layoutInflater, container, false)

        userReceiver = args.user
        userSender = PrefManager.getUser(requireContext())

        userReceiver.let { binding.userName.text = "${it?.name} ${it?.surname}" }

        recyclerView = binding.rvChatDetail
        chatMessageAdapter =
            userReceiver?.let { ChatMessageAdapter(emptyList(), it) }!! // Пустой список сообщений
        recyclerView.adapter = chatMessageAdapter
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        loadChatHistory()

        sendMessage()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handler = Handler(Looper.getMainLooper())
        pollNewMessages()
    }

    private fun sendMessage() {
        binding.messageSendButton.setOnClickListener {

            val messageContent = binding.messageEditText.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val messageDto = MessageDto(
                    senderId = userSender?.id ?: 0,
                    receiverId = userReceiver?.id ?: 0,
                    content = messageContent
                )
                chatApiService.sendMessage(messageDto).enqueue(object : Callback<Message> {
                    override fun onResponse(call: Call<Message>, response: Response<Message>) {
                        if (response.isSuccessful) {
                            // Обновить список сообщений после успешной отправки
                            loadChatHistory()
                            binding.messageEditText.text.clear()
                            recyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                                recyclerView.scrollToPosition(chatMessageAdapter.itemCount - 1)
                            }
                        } else {
                            // Обработка ошибки
                        }
                    }

                    override fun onFailure(call: Call<Message>, t: Throwable) {
                        // Обработка ошибки
                    }
                })
            }
        }
    }

    private fun loadChatHistory() {
        userSender?.id?.let { senderId ->
            userReceiver?.id?.let { receiverId ->
                chatApiService.getChatHistory(senderId, receiverId).enqueue(object :
                    Callback<List<Message>> {
                    override fun onResponse(
                        call: Call<List<Message>>,
                        response: Response<List<Message>>
                    ) {
                        if (response.isSuccessful) {
                            val messages = response.body()
                            messages?.let {
                                chatMessageAdapter.messages = it
                                chatMessageAdapter.notifyDataSetChanged()
                            }
                        } else {
                            // Обработка ошибки
                        }
                    }

                    override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                        // Обработка ошибки
                    }
                })
            }
        }
    }

    private fun pollNewMessages() {

        val delay: Long = 2000

        handler?.postDelayed(object : Runnable {
            override fun run() {

                loadChatHistory()
                handler?.postDelayed(this, delay)
            }
        }, delay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler?.removeCallbacksAndMessages(null)
        handler = null
        _binding = null
    }

    /*private lateinit var targetUserNameTextView: TextView
    private lateinit var messageEditText: EditText
    private lateinit var messageSendButton: ImageButton
    private lateinit var currentUser: User
    private lateinit var targetUser: User
    private lateinit var chatDeliver: ChatDeliver
    private lateinit var chatListener: ChatListener
    private lateinit var messageAdapter: MessageAdapter
    private var chats: List<Chat> = ArrayList()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_detail, container, false)

        currentUser = arguments?.getSerializable("CurrentUserKey") as User
        targetUser = arguments?.getSerializable("TargetUserKey") as User

        targetUserNameTextView = view.findViewById(R.id.targetUserNameTextView)
        messageEditText = view.findViewById(R.id.messageEditText)
        messageSendButton = view.findViewById(R.id.messageSendButton)
        recyclerView = view.findViewById(R.id.rv_chat_detail)

        // Set target user name
        targetUserNameTextView.text = targetUser.name

        // Configure recycler view
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        // Start listening to chat list for current user and target user
        chatListener = ChatListener(currentUser.id.toString(), targetUser.id.toString())
        val topicHandler = chatListener.subscribe("/topics/event/${currentUser.id}/${targetUser.id}")
        topicHandler.addListener { message ->
            activity?.runOnUiThread {
                if (message.content == "[]") {
                    return@runOnUiThread
                }
                val stompMessageSerializer = StompMessageSerializer()
                chats = stompMessageSerializer.putChatListStompMessageToListOfChats(message)
                messageAdapter = MessageAdapter(requireContext(), chats, currentUser, targetUser)
                recyclerView.adapter = messageAdapter
            }
        }
        chatListener.connect(InternetAddress.webSocketAddress)

        // Send button click listener
        messageSendButton.setOnClickListener {
            if (messageEditText.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "You cannot send empty message", Toast.LENGTH_SHORT).show()
            } else {
                chatDeliver = ChatDeliver(currentUser.id.toString(), targetUser.id.toString(), messageEditText.text.toString())
            }
        }

        return view
    }*/
}