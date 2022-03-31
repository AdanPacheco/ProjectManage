package com.udemy.projectmanage.ui.adapters.colors

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.databinding.ItemLabelColorBinding

class ColorsViewHolder(view: View):RecyclerView.ViewHolder(view){

    private val binding=  ItemLabelColorBinding.bind(view)

    fun render(color:String,selectedColor:String,onSelectColor: (String) -> Unit){

        binding.viewMain.setBackgroundColor(Color.parseColor(color))
        if(selectedColor==color){
            binding.ivSelectedColor.visibility = View.VISIBLE
        }else{
            binding.ivSelectedColor.visibility = View.GONE
        }
        itemView.setOnClickListener{
            onSelectColor(color)
        }
    }
}
