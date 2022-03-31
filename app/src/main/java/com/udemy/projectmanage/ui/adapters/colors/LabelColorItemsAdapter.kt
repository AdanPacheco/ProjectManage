package com.udemy.projectmanage.ui.adapters.colors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.R

class LabelColorItemsAdapter(
    private val colorList: ArrayList<String>,
    private val selectedColor: String,
    private val onSelectColor: (String) -> Unit
) : RecyclerView.Adapter<ColorsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_label_color, parent, false)
        return ColorsViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ColorsViewHolder, position: Int) {
        holder.render(colorList[position], selectedColor,onSelectColor)
    }

    override fun getItemCount(): Int = colorList.size
}