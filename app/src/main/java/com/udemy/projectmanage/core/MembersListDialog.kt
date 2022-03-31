package com.udemy.projectmanage.core

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.DialogListBinding
import com.udemy.projectmanage.ui.adapters.colors.LabelColorItemsAdapter
import com.udemy.projectmanage.ui.adapters.member.MembersListItemsAdapter

abstract class MembersListDialog(
    context: Context, private val list: ArrayList<User>, private val title: String = ""
) : Dialog(context) {

    private var adapter: MembersListItemsAdapter? = null
    private lateinit var binding: DialogListBinding

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_list, null)

        binding = DialogListBinding.bind(view)
        setContentView(binding.root)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        binding.tvTitle.text = title
        binding.rvList.layoutManager = LinearLayoutManager(context)
        adapter = MembersListItemsAdapter(list) { member, action -> onSelectedMember(member, action) }
        binding.rvList.adapter = adapter
    }

    private fun onSelectedMember(member: User, action:String) {
        dismiss()
        onItemSelected(member,action)
    }

    protected abstract fun onItemSelected(member:User,action:String)
}