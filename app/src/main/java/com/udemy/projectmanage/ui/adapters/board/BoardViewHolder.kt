package com.udemy.projectmanage.ui.adapters.board

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.databinding.ItemBoardBinding

class BoardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemBoardBinding.bind(view)

    fun render(board: Board, onClick: (Board) -> Unit) {

        Glide.with(binding.ivBoardImage.context).load(board.image).centerCrop().placeholder(R.drawable.ic_user_place_holder)
            .into(binding.ivBoardImage)

        binding.tvName.text = board.name
        val cb = "Created by ${board.createdBy}"
        binding.tvCreatedBy.text = cb

        itemView.setOnClickListener {
            onClick(board)
        }

    }
}
