package com.udemy.projectmanage.ui.adapters.task

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.Task
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ItemTaskBinding
import com.udemy.projectmanage.ui.adapters.card.CardListItemsAdapter
import java.util.*
import kotlin.collections.ArrayList

class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemTaskBinding.bind(view)
    private lateinit var adapter: CardListItemsAdapter

    private var mDraggedPositionFrom = -1
    private var mDraggedPositionTo = -1

    fun render(
        task: Task,
        listSize: Int,
        assignedList: ArrayList<User>,
        onCreateNewTask: (String) -> Unit,
        onEditTask: (Int, Task) -> Unit,
        onDeleteTask: (Int, String) -> Unit,
        onCreateCard: (Int, String) -> Unit,
        onCardPressed: (Int, Int) -> Unit
    ) {

        if (layoutPosition == listSize - 1) {
            binding.tvAddTaskList.visibility = View.VISIBLE
            binding.llTaskItem.visibility = View.GONE
        } else {
            binding.tvAddTaskList.visibility = View.GONE
            binding.llTaskItem.visibility = View.VISIBLE
        }
        binding.tvTaskListTitle.text = task.title
        binding.ibDeleteList.setOnClickListener {
            onDeleteTask(layoutPosition, task.title)
        }

        addTaskListListeners(onCreateNewTask)
        onEditTaskListListeners(task, onEditTask, layoutPosition)

        //add card behavior
        cardInTaskListListeners(onCreateCard, layoutPosition, task, assignedList, onCardPressed)


    }

    private fun cardInTaskListListeners(
        onCreateCard: (Int, String) -> Unit, position: Int, task: Task, assignedList: ArrayList<User>, onCardPressed: (Int, Int) -> Unit
    ) {
        binding.tvAddCard.setOnClickListener {
            binding.tvAddCard.visibility = View.GONE
            binding.cvAddCard.visibility = View.VISIBLE
        }

        binding.ibCloseCardName.setOnClickListener {
            binding.tvAddCard.visibility = View.VISIBLE
            binding.cvAddCard.visibility = View.GONE
        }

        binding.ibDoneCardName.setOnClickListener {
            val cardName = binding.etCardName.text.toString()
            if (cardName.isNotEmpty()) {
                onCreateCard(position, cardName)
            } else {
                Toast.makeText(binding.cvAddCard.context, "Please enter card name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvCardList.layoutManager = LinearLayoutManager(binding.rvCardList.context)
        binding.rvCardList.setHasFixedSize(true)
        adapter = CardListItemsAdapter(task.cards, position, assignedList, onCardPressed)
        binding.rvCardList.adapter = adapter

        val dividerItemDecoration = DividerItemDecoration(binding.rvCardList.context, DividerItemDecoration.HORIZONTAL)
        binding.rvCardList.addItemDecoration(dividerItemDecoration)

    }

    fun createDragAndDrop(taskList: ArrayList<Task>, onCardSwiped: (position: Int, cards: ArrayList<Card>) -> Unit) {

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {

            override fun onMove(recyclerView: RecyclerView, dragged: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val draggedPosition = dragged.adapterPosition
                val targetPosition = target.adapterPosition

                if (mDraggedPositionFrom == -1) {
                    mDraggedPositionFrom = draggedPosition
                }

                mDraggedPositionTo = targetPosition
                Collections.swap(taskList[layoutPosition].cards, draggedPosition, targetPosition)
                adapter.notifyItemMoved(draggedPosition, targetPosition)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                if (mDraggedPositionFrom != -1 && mDraggedPositionTo != -1 && mDraggedPositionTo != mDraggedPositionFrom) {
                    onCardSwiped(layoutPosition, taskList[layoutPosition].cards)
                }
                mDraggedPositionFrom=-1
                mDraggedPositionTo-1
            }
        })

        touchHelper.attachToRecyclerView(binding.rvCardList)
    }


    private fun onEditTaskListListeners(task: Task, onEditTask: (Int, Task) -> Unit, position: Int) {
        binding.ibEditListName.setOnClickListener {
            binding.etEditTaskListName.setText(task.title)
            binding.llTitleView.visibility = View.GONE
            binding.cvEditTaskListName.visibility = View.VISIBLE
        }

        binding.ibCloseEditableView.setOnClickListener {
            binding.llTitleView.visibility = View.VISIBLE
            binding.cvEditTaskListName.visibility = View.GONE
        }

        binding.ibDoneEditListName.setOnClickListener {
            val listName = binding.etEditTaskListName.text.toString()
            if (listName.isNotEmpty()) {
                task.title = listName
                onEditTask(position, task)
            } else {
                Toast.makeText(binding.cvAddCard.context, "Please enter list name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addTaskListListeners(onCreateNewTask: (String) -> Unit) {
        binding.tvAddTaskList.setOnClickListener {
            binding.cvAddTaskListName.visibility = View.GONE
            binding.cvAddTaskListName.visibility = View.VISIBLE
        }

        binding.ibCloseListName.setOnClickListener {
            binding.cvAddTaskListName.visibility = View.VISIBLE
            binding.cvAddTaskListName.visibility = View.GONE
        }

        binding.ibDoneListName.setOnClickListener {
            val listName = binding.etTaskListName.text.toString()
            if (listName.isNotEmpty()) {
                onCreateNewTask(listName)
            } else {
                Toast.makeText(binding.cvAddCard.context, "Please enter list name", Toast.LENGTH_SHORT).show()
            }
        }
    }


}