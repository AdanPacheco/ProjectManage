package com.udemy.projectmanage.ui.adapters.task

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.R
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.Task
import com.udemy.projectmanage.data.model.User
import java.util.*
import kotlin.collections.ArrayList

class TaskListItemAdapter(
    private val taskList: ArrayList<Task>,
    private val assignedList:ArrayList<User>,
    private val onNewTaskList: (String) -> Unit,
    private val onEditTask: (Int, Task) -> Unit,
    private val onDeleteTaskList: (Int, String) -> Unit,
    private val onCreateCard: (Int, String) -> Unit,
    private val onCardPressed: (Int, Int) -> Unit,
    private val onCardSwiped: (position: Int, cards: ArrayList<Card>) -> Unit
) : RecyclerView.Adapter<TaskViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams((parent.width * 0.7).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(15.toDp().toPx(), 0, (40.toDp()).toPx(), 0)
        layoutInflater.layoutParams = layoutParams
        return TaskViewHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.render(taskList[position], itemCount,assignedList, onNewTaskList, onEditTask, onDeleteTaskList, onCreateCard,onCardPressed)
        holder.createDragAndDrop(taskList,onCardSwiped)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}