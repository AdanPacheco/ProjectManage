package com.udemy.projectmanage.ui.adapters.board

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.ui.adapters.board.BoardViewHolder


class BoardItemsAdapter(private val boardList: ArrayList<Board>,private val onClick:(Board)->Unit) : RecyclerView.Adapter<BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BoardViewHolder(inflater.inflate(R.layout.item_board, parent, false))
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.render(boardList[position]) { board -> onClick(board) }
    }

    override fun getItemCount(): Int {
        return boardList.size
    }

}