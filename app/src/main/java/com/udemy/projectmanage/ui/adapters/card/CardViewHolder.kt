package com.udemy.projectmanage.ui.adapters.card

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.model.SelectedMembers
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ItemCardBinding
import com.udemy.projectmanage.ui.adapters.member.selectedMembers.CardMembersListItemAdapter
import com.udemy.projectmanage.ui.view.TaskListActivity

class CardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCardBinding.bind(view)

    fun render(card: Card, assignedList: ArrayList<User>) {
        binding.tvCardName.text = card.name
        if (card.labelColor.isNotEmpty()) {
            binding.viewLabelColor.visibility = View.VISIBLE
            binding.viewLabelColor.setBackgroundColor(Color.parseColor(card.labelColor))
        } else {
            binding.viewLabelColor.visibility = View.GONE
        }

        setupRecyclerCardMembers(card,assignedList)
    }

    private fun setupRecyclerCardMembers(card: Card, assignedList: ArrayList<User>) {
        if (assignedList.isNotEmpty()) {
            val selectedMemberList: ArrayList<SelectedMembers> = ArrayList()
            val assignedUsersId = assignedList.map { member -> member.id }
            val commonMembers = card.assignedTo.intersect(assignedUsersId)

            commonMembers.forEach { memberId ->
                val member = assignedList.firstOrNull { member -> member.id == memberId }
                val selectedMember = member?.let { m -> SelectedMembers(m.id, m.image) }
                if (selectedMember != null) {
                    selectedMemberList.add(selectedMember)
                }
            }
            if (selectedMemberList.isNotEmpty()) {
                if (selectedMemberList.size == 1 && selectedMemberList[0].id == card.createdBy) {
                    binding.rvCardSelectedMembersList.visibility = View.GONE
                    return
                }

                binding.rvCardSelectedMembersList.visibility = View.VISIBLE
                binding.rvCardSelectedMembersList.layoutManager = GridLayoutManager(binding.rvCardSelectedMembersList.context, 4)
                val adapter = CardMembersListItemAdapter(selectedMemberList, false) {}
                binding.rvCardSelectedMembersList.adapter = adapter
                return
            }
        }
        binding.rvCardSelectedMembersList.visibility = View.GONE
    }
}
