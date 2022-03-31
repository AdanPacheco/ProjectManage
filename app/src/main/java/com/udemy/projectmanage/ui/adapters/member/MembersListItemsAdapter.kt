package com.udemy.projectmanage.ui.adapters.member

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.User


class MembersListItemsAdapter(private val memberList: ArrayList<User>, private val onSelectedMember: (User, String) -> Unit) :
    RecyclerView.Adapter<MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.render(memberList[position], onSelectedMember)
    }

    override fun getItemCount(): Int = memberList.size
}