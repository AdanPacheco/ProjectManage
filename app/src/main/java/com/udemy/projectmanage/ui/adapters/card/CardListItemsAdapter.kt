package com.udemy.projectmanage.ui.adapters.card

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.User

class CardListItemsAdapter(private val cardList: ArrayList<Card>, private val taskPosition: Int, private val assignedList:ArrayList<User>, private val onCardPressed: (Int, Int) -> Unit) :
    RecyclerView.Adapter<CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.render(cardList[position],assignedList)
        holder.itemView.setOnClickListener {
            onCardPressed(taskPosition,position)
        }
    }

    override fun getItemCount(): Int = cardList.size
}