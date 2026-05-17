package com.example.dipl.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.dipl.R
import com.example.dipl.data.api.Api
import com.example.dipl.domain.model.Contract
import com.example.dipl.domain.model.ResponseApartment
import com.example.dipl.domain.model.User
import com.example.dipl.presentation.fragment.ContractListFragmentDirections
import com.example.dipl.presentation.fragment.ModerContractListFragmentDirections
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModerContractAdapter(
    private val responseList: List<Contract>,
    private val user: User?
) : RecyclerView.Adapter<ModerContractAdapter.ViewHolder>() {

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
        val btnCreate: Button = view.findViewById(R.id.btn_check)
        val btnOpen: Button = view.findViewById(R.id.btn_open)
        val btnCheck: Button = view.findViewById(R.id.btn_check)
        val tv: TextView = view.findViewById(R.id.tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.response_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ModerContractAdapter.ViewHolder, position: Int) {
        val currentItem = responseList[position]


        if (user?.id == currentItem.apartmentInfo.userOwner.id) {
            holder.tv.text = "Договор о сдаче"
        } else {
            holder.tv.text = "Договор о съеме"
        }
        holder.tvName.text = currentItem.apartmentInfo.name
        holder.tvCity.text = currentItem.apartmentInfo.city
        holder.tvRent.text = currentItem.apartmentInfo.rent.toString()

        holder.tvStatus.visibility = View.GONE

        holder.btnOpen.visibility = View.VISIBLE
        holder.btnCheck.visibility = View.GONE

        checkContractExistence(currentItem, holder)

        holder.btnOpen.setOnClickListener {
            val action = ModerContractListFragmentDirections.actionModerContractListFragmentToContractFragment2(currentItem)
            holder.itemView.findNavController().navigate(action)
        }

    }

    private fun checkContractExistence(currentItem: Contract, holder: ViewHolder) {
        val call = Api.contractApiService.getContractByUsersAndApartmentInfo(
            user?.id ?: -1,
            currentItem.apartmentInfo.userOwner.id,
            currentItem.apartmentInfo.id
        )

        call.enqueue(object : Callback<Contract> {
            override fun onResponse(call: Call<Contract>, response: Response<Contract>) {
                if (response.isSuccessful) {
                    val contract = response.body()

                    if (contract != null) {

                        holder.btnCreate.visibility = View.GONE
                        holder.btnOpen.visibility = View.VISIBLE

                        holder.btnOpen.setOnClickListener {

                            val action = contract.let {
                                ModerContractListFragmentDirections.actionModerContractListFragmentToContractFragment2(
                                    it
                                )
                            }
                            action.let { holder.itemView.findNavController().navigate(it) }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Contract>, t: Throwable) {
                Log.d("checkContractExistence", "checkContractExistence")
            }
        })
    }

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
                    Log.d("updateResponseStatus", "updateResponseStatus")
                }
            }

            override fun onFailure(call: Call<ResponseApartment>, t: Throwable) {
                Log.d("updateResponseStatus", "updateResponseStatus")
            }
        })
    }

}