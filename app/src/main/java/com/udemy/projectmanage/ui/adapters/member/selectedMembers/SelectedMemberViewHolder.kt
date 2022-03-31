package com.udemy.projectmanage.ui.adapters.member.selectedMembers

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.projemanag.model.SelectedMembers
import com.udemy.projectmanage.R
import com.udemy.projectmanage.databinding.ItemCardSelectedMemberBinding

class SelectedMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCardSelectedMemberBinding.bind(view)

    fun render(selectedMember: SelectedMembers, assignMembers: Boolean, listSize: Int) {
        if (layoutPosition == listSize - 1 && assignMembers) {
            binding.ivAddMember.visibility = View.VISIBLE
            binding.ivSelectedMemberImage.visibility = View.GONE
        } else {
            binding.ivAddMember.visibility = View.GONE
            binding.ivSelectedMemberImage.visibility = View.VISIBLE

            Glide.with(binding.ivSelectedMemberImage.context).load(selectedMember.image).centerCrop()
                .placeholder(R.drawable.ic_user_place_holder).into(binding.ivSelectedMemberImage)
        }

    }
}
