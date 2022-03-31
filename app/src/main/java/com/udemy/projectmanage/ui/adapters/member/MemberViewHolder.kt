package com.udemy.projectmanage.ui.adapters.member

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ItemMemberBinding


class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemMemberBinding.bind(view)

    fun render(member: User, onSelectedMember: (User, String) -> Unit) {
        Glide.with(binding.ivMemberImage.context).load(member.image).centerCrop().placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivMemberImage)

        binding.tvMemberName.text = member.name
        binding.tvMemberEmail.text = member.email

        if (member.selected) {
            binding.ivSelectedMember.visibility = View.VISIBLE
        }

        itemView.setOnClickListener {
            if (member.selected) {
                onSelectedMember(member, Constants.UN_SELECT)
                return@setOnClickListener
            }
            onSelectedMember(member, Constants.SELECT)
        }
    }
}
