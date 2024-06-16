package com.example.dipl.presentation.adapter

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.diplback.diplserver.dto.ContractDto
import com.example.dipl.CircleTransformation
import com.example.dipl.R
import com.example.dipl.data.api.Api
import com.example.dipl.data.api.Api.contractApiService
import com.example.dipl.domain.model.Contract
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.fragment.ResponseListFragmentDirections
import com.example.dipl.presentation.fragment.ResponseSendListFragmentDirections
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.time.LocalDate
import java.time.ZoneId

class ResponseSendAdapter(
    private val responseList: List<ResponseApartment>,
    private val user: User?
) : RecyclerView.Adapter<ResponseSendAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        //val ivProfile: ImageView = view.findViewById(R.id.iv_profile)
        //val tvResponderName: TextView = view.findViewById(R.id.tv_responder_name)
        //val btnChat: Button = view.findViewById(R.id.btn_chat)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvCity: TextView = view.findViewById(R.id.tv_city)
        val tvRent: TextView = view.findViewById(R.id.tv_rent)
        /*val btnApproved: Button = view.findViewById(R.id.btn_approved)
        val btnReject: Button = view.findViewById(R.id.btn_reject)*/
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btnCreate: Button = view.findViewById(R.id.btn_create)
        val btnOpen: Button = view.findViewById(R.id.btn_open)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.response_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResponseSendAdapter.ViewHolder, position: Int) {
        val currentItem = responseList[position]

        //holder.tvResponderName.text = currentItem.user.name + " " + currentItem.user.surname

        /*val imagePath = currentItem.user.photoUser
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                Picasso.get()
                    .load(file)
                    .transform(CircleTransformation())
                    .into(holder.ivProfile)
            }
        }*/

        holder.tvName.text = currentItem.apartmentInfo.name
        holder.tvCity.text = currentItem.apartmentInfo.city
        holder.tvRent.text = currentItem.apartmentInfo.rent.toString()

        holder.tvStatus.text = currentItem.status

        if (currentItem.status == "В ожидании" || currentItem.status == "Отклонено") {
            holder.btnCreate.visibility = View.GONE
        } else {
            holder.btnCreate.visibility = View.VISIBLE
        }

        checkContractExistence(currentItem, holder)

        holder.btnCreate.setOnClickListener {

            user?.let { user ->
                val contractDto = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContractDto(
                        apartmentInfoId = currentItem.apartmentInfo.id,
                        userOwnerId = currentItem.apartmentInfo.userOwner.id,
                        userSenderId = user.id,
                        date = LocalDate.now().toString()
                    )
                } else {
                    TODO("VERSION.SDK_INT < O")
                }

                val call = contractApiService.createContract(contractDto)
                call.enqueue(object : Callback<Contract> {
                    override fun onResponse(call: Call<Contract>, response: Response<Contract>) {
                        if (response.isSuccessful) {
                            val contract = response.body()
                            val action = contract?.let { it1 ->
                                ResponseSendListFragmentDirections.actionResponseSendListFragmentToContractFragment(
                                    it1
                                )
                            }
                            action?.let { it1 -> holder.itemView.findNavController().navigate(it1) }
                            //Toast.makeText(context, "Контракт успешно создан", Toast.LENGTH_SHORT).show()
                        } else {
                            // Обработка ошибки при создании контракта
                            //Toast.makeText(context, "Ошибка при создании контракта", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Contract>, t: Throwable) {
                        // Обработка ошибки сети или запроса
                        //Toast.makeText(context, "Ошибка сети", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }



        /*if (currentItem.status == "В ожидании") {
            holder.btnApproved.visibility = View.VISIBLE
            holder.btnReject.visibility = View.VISIBLE
        } else {
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }*/

        /*holder.btnChat.setOnClickListener {

            val action = ResponseListFragmentDirections.actionResponseListFragmentToChatDetailFragment(currentItem.user)
            holder.itemView.findNavController().navigate(action)

        }*/

        /*holder.btnApproved.setOnClickListener {
            updateResponseStatus(currentItem.id, "Одобрено", holder)
            currentItem.status = "Одобрено"
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }

        holder.btnReject.setOnClickListener {
            updateResponseStatus(currentItem.id, "Отклонено", holder)
            currentItem.status = "Отклонено"
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }*/
    }

    private fun checkContractExistence(currentItem: ResponseApartment, holder: ViewHolder) {
        val call = contractApiService.getContractByUsersAndApartmentInfo(
            user?.id ?: -1,
            currentItem.apartmentInfo.userOwner.id,
            currentItem.apartmentInfo.id
        )

        call.enqueue(object : Callback<Contract> {
            override fun onResponse(call: Call<Contract>, response: Response<Contract>) {
                if (response.isSuccessful) {
                    val contract = response.body()

                    if (contract != null) {
                        // Контракт уже существует, скрываем кнопку создания и показываем кнопку открытия
                        holder.btnCreate.visibility = View.GONE
                        holder.btnOpen.visibility = View.VISIBLE

                        holder.btnOpen.setOnClickListener {
                            // Передаем контракт в навигационный контроллер
                            val action = contract.let {
                                ResponseSendListFragmentDirections.actionResponseSendListFragmentToContractFragment(
                                    it
                                )
                            }
                            action.let { holder.itemView.findNavController().navigate(it) }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Contract>, t: Throwable) {
                // Обработка ошибки при получении контракта
                Log.d("jkl", "kl;")
                //Toast.makeText(context, "Ошибка при получении контракта", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = responseList[position]

        holder.tvResponderName.text = currentItem.user.name + " " + currentItem.user.surname

        val imagePath = currentItem.user.photoUser
        imagePath?.let {
            val file = File(it)
            if (file.exists()) {
                Picasso.get()
                    .load(file)
                    .transform(CircleTransformation())
                    .into(holder.ivProfile)
            }
        }

        holder.tvName.text = currentItem.apartmentInfo.name
        holder.tvCity.text = currentItem.apartmentInfo.city
        holder.tvRent.text = currentItem.apartmentInfo.rent.toString()

        holder.tvStatus.text = currentItem.status

        if (currentItem.status == "В ожидании") {
            holder.btnApproved.visibility = View.VISIBLE
            holder.btnReject.visibility = View.VISIBLE
        } else {
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }

        holder.btnChat.setOnClickListener {

            val action = ResponseListFragmentDirections.actionResponseListFragmentToChatDetailFragment(currentItem.user)
            holder.itemView.findNavController().navigate(action)

        }



        holder.btnApproved.setOnClickListener {
            updateResponseStatus(currentItem.id, "Одобрено", holder)
            currentItem.status = "Одобрено"
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }

        holder.btnReject.setOnClickListener {
            updateResponseStatus(currentItem.id, "Отклонено", holder)
            currentItem.status = "Отклонено"
            holder.btnApproved.visibility = View.GONE
            holder.btnReject.visibility = View.GONE
        }


        //holder.bind(currentItem)
    }*/

    override fun getItemCount(): Int {
        return responseList.size
    }

    private fun updateResponseStatus(responseId: Int, status: String, holder: ViewHolder) {
        val call = Api.responseApartmentApiService.updateResponseStatus(responseId, status)
        call.enqueue(object : Callback<ResponseApartment> {
            override fun onResponse(call: Call<ResponseApartment>, response: Response<ResponseApartment>) {
                if (response.isSuccessful) {
                    holder.tvStatus.text = status

                } else {
                    // Обработать ошибку
                }
            }

            override fun onFailure(call: Call<ResponseApartment>, t: Throwable) {
                // Обработать сбой запроса
            }
        })
    }

}