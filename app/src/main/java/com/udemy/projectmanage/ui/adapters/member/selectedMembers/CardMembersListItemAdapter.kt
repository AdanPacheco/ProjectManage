package com.udemy.projectmanage.ui.adapters.member.selectedMembers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.model.SelectedMembers
import com.udemy.projectmanage.R

open class CardMembersListItemAdapter(
    private val selectedMembers: ArrayList<SelectedMembers>,
    private val assignMembers: Boolean,
    private val onSelectedMemberClicked: () -> Unit
) : RecyclerView.Adapter<SelectedMemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedMemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_card_selected_member, parent, false)
        return SelectedMemberViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: SelectedMemberViewHolder, position: Int) {
        holder.render(selectedMembers[position],assignMembers, itemCount)
        holder.itemView.setOnClickListener {
            onSelectedMemberClicked()
        }
    }

    override fun getItemCount(): Int = selectedMembers.size
}