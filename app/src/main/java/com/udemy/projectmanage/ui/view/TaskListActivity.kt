package com.udemy.projectmanage.ui.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.core.DatePickerFragment
import com.udemy.projectmanage.data.network.FirestoreClass
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.Task
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ActivityTaskListBinding
import com.udemy.projectmanage.ui.adapters.task.TaskListItemAdapter

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoard: Board
    private val membersLauncher: ActivityResultLauncher<Intent> = refreshBoardData()
    private val cardDetailLauncher: ActivityResultLauncher<Intent> = refreshBoardData()
    lateinit var mAssignedMemberDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkExtras()
    }

    private fun checkExtras() {
        if (intent.hasExtra(Constants.BOARD_ITEM)) {
            val boardItem = intent.getParcelableExtra<Board>(Constants.BOARD_ITEM)!!
            setupActionBar(boardItem)
            setUpTaskRecyclerView(boardItem)
        }
    }

    private fun setUpTaskRecyclerView(boardItem: Board) {
        mBoard = boardItem
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(mBoard.assignedTo) { list -> boardMembersDetailList(list) }
    }

    private fun setupActionBar(board: Board) {
        setSupportActionBar(binding.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = board.name
        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_members -> {
                val intent = Intent(this@TaskListActivity, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAILS, mBoard)
                membersLauncher.launch(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createNewTask(taskListName: String) {
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoard.taskList.add(0, task)
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        setResult(Activity.RESULT_OK)
        FirestoreClass().addUpdateBoardTaskList(mBoard) { board -> setUpTaskRecyclerView(board) }
    }

    private fun updateTaskList(position: Int, model: Task) {
        mBoard.taskList[position].title = model.title
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        setResult(Activity.RESULT_OK)
        FirestoreClass().addUpdateBoardTaskList(mBoard) { board -> setUpTaskRecyclerView(board) }
    }

    private fun deleteTaskList(position: Int, listName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $listName.")
        builder.setIcon(R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            mBoard.taskList.removeAt(position)
            mBoard.taskList.removeAt(mBoard.taskList.size - 1)
            setResult(Activity.RESULT_OK)
            FirestoreClass().addUpdateBoardTaskList(mBoard) { board -> setUpTaskRecyclerView(board) }
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun addCardToTaskList(position: Int, cardName: String) {
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)

        val userId = FirestoreClass().getCurrentUserId()
        val cardAssignedUserList: ArrayList<String> = ArrayList()
        cardAssignedUserList.add(userId)

        val card = Card(cardName, userId, cardAssignedUserList)
        val cardsList = mBoard.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoard.taskList[position].title, mBoard.taskList[position].createdBy, cardsList
        )

        mBoard.taskList[position] = task
        setResult(Activity.RESULT_OK)
        FirestoreClass().addUpdateBoardTaskList(mBoard) { board -> setUpTaskRecyclerView(board) }
    }

    private fun refreshBoardData() = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().getBoardDetails(mBoard.documentId) { board -> setUpTaskRecyclerView(board) }
        }
    }

    private fun onCardPressed(taskPosition: Int, cardPosition: Int) {
        val intent = Intent(this, CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAILS, mBoard)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION, taskPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION, cardPosition)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST, mAssignedMemberDetailList)
        cardDetailLauncher.launch(intent)
    }

    private fun boardMembersDetailList(list: ArrayList<User>) {
        mAssignedMemberDetailList = list
        hideProgressDialog()
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoard.taskList.add(addTaskList)
        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(mBoard.taskList,
            mAssignedMemberDetailList,
            { taskListName -> createNewTask(taskListName) },
            { position, task -> updateTaskList(position, task) },
            { position, listName -> deleteTaskList(position, listName) },
            { position, cardName -> addCardToTaskList(position, cardName) },
            { taskPosition, cardPosition -> onCardPressed(taskPosition, cardPosition) },
            { taskListPosition, cards -> onCardSwiped(taskListPosition, cards) })
        binding.rvTaskList.adapter = adapter
    }

    private fun onCardSwiped(taskListPosition: Int, cards: ArrayList<Card>) {
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        mBoard.taskList[taskListPosition].cards = cards
        setResult(Activity.RESULT_OK)
        FirestoreClass().addUpdateBoardTaskList(mBoard) { board -> setUpTaskRecyclerView(board) }
    }

}