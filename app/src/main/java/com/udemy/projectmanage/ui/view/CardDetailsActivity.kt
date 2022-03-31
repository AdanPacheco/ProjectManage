package com.udemy.projectmanage.ui.view


import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.view.View
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.projemanag.model.SelectedMembers
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.core.DatePickerFragment
import com.udemy.projectmanage.core.LabelColorListDialog
import com.udemy.projectmanage.core.MembersListDialog
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.data.model.Card
import com.udemy.projectmanage.data.model.Task
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.data.network.FirestoreClass
import com.udemy.projectmanage.databinding.ActivityCardDetailsBinding
import com.udemy.projectmanage.ui.adapters.member.selectedMembers.CardMembersListItemAdapter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class CardDetailsActivity : BaseActivity() {


    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var mBoard: Board
    private var taskPosition = -1
    private var cardPosition = -1
    private var mSelectedColor = ""
    private var mSelectedDueDate: Long = 0
    private lateinit var mMemberDetailsList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkExtras()
        initListeners()
        setupSelectedMembersList()

    }

    private fun initListeners() {
        binding.btnUpdateCardDetails.setOnClickListener {
            if (binding.etNameCardDetails.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter a card name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            updateCardDetails()
        }

        binding.tvSelectLabelColor.setOnClickListener {
            labelColorsDialog()
        }

        binding.rvSelectedMembersList.setOnClickListener {
            memberListDialog()
        }
        binding.tvSelectMembers.setOnClickListener {
            memberListDialog()
        }

        binding.tvSelectDueDate.setOnClickListener {
            showDatePicker()
        }
    }

    private fun checkExtras() {

        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            mBoard = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            taskPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            cardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMemberDetailsList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }

        setupActionBar()
    }


    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val card = mBoard.taskList[taskPosition].cards[cardPosition]
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = card.name
        }

        loadCardData(card)
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener { onBackPressed() }
    }


    private fun loadCardData(card: Card) {
        binding.etNameCardDetails.setText(card.name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
        mSelectedDueDate = card.dueDate

        if (card.labelColor.isNotEmpty()) {
            mSelectedColor = card.labelColor
            setColor()
        }

        if (mSelectedDueDate > 0) {
            val simpleDF = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val selectedDate = simpleDF.format(Date(mSelectedDueDate))
            binding.tvSelectDueDate.text = selectedDate
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoard.taskList[taskPosition].cards[cardPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateCardDetails() {
        val card = Card(
            binding.etNameCardDetails.text.toString(),
            mBoard.taskList[taskPosition].cards[cardPosition].createdBy,
            mBoard.taskList[taskPosition].cards[cardPosition].assignedTo,
            mSelectedColor,
            mSelectedDueDate
        )

        mBoard.taskList[taskPosition].cards[cardPosition] = card

        showProgressDialog(getString(R.string.please_wait))
        mBoard.taskList.removeAt(mBoard.taskList.size - 1)
        FirestoreClass().addUpdateBoardTaskList(mBoard) { onSuccessUpdateBoard() }
    }

    private fun onSuccessUpdateBoard() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun deleteCard() {
        val cardList: ArrayList<Card> = mBoard.taskList[taskPosition].cards
        cardList.removeAt(cardPosition)

        val taskList: ArrayList<Task> = mBoard.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[taskPosition].cards = cardList
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().addUpdateBoardTaskList(mBoard) { onSuccessUpdateBoard() }
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(
            resources.getString(
                R.string.confirmation_message_to_delete_card, cardName
            )
        )
        builder.setIcon(R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun memberListDialog() {
        val cardAssignedMemberList = mBoard.taskList[taskPosition].cards[cardPosition].assignedTo
        if (cardAssignedMemberList.size > 0) {
            cardAssignedMemberList.forEach { memberId ->
                mMemberDetailsList.forEach { member ->
                    if (memberId == member.id) {
                        member.selected = true
                    }
                }
            }
        } else {
            mMemberDetailsList.forEach { member ->
                member.selected = false
            }
        }

        val listDialog = object : MembersListDialog(this, mMemberDetailsList, resources.getString(R.string.str_select_member)) {
            override fun onItemSelected(member: User, action: String) {
                if (action == Constants.SELECT) {
                    if (!mBoard.taskList[taskPosition].cards[cardPosition].assignedTo.contains(member.id)) mBoard.taskList[taskPosition].cards[cardPosition].assignedTo.add(
                        member.id
                    )

                } else {
                    mBoard.taskList[taskPosition].cards[cardPosition].assignedTo.remove(member.id)
                    val index = mMemberDetailsList.indexOf(mMemberDetailsList.firstOrNull { memberObj -> memberObj.id == member.id })
                    mMemberDetailsList[index].selected = false
                }

                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }

    private fun setColor() {
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun labelColorsDialog() {
        val listDialog = object :
            LabelColorListDialog(this, Constants.colorsList(this), resources.getString(R.string.str_select_label_color), mSelectedColor) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    private fun setupSelectedMembersList() {
        val cardAssignedTo = mBoard.taskList[taskPosition].cards[cardPosition].assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        cardAssignedTo.forEach { memberId ->
            mMemberDetailsList.forEach { member ->
                if (memberId == member.id) {
                    val selectedMember = SelectedMembers(
                        member.id, member.image
                    )
                    selectedMembersList.add(selectedMember)
                }

            }
        }

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE

            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMembersListItemAdapter(selectedMembersList, true) { memberListDialog() }
            binding.rvSelectedMembersList.adapter = adapter
            return
        }

        binding.tvSelectMembers.visibility = View.VISIBLE
        binding.rvSelectedMembersList.visibility = View.GONE

    }

    private fun showDatePicker() {
        val datePicker = DatePickerFragment { year, month, day -> onDateSelected(year, month, day) }
        datePicker.show(supportFragmentManager, "datePicker")
    }

    private fun onDateSelected(year: Int, month: Int, day: Int) {
        val sDayOfMonth = if (day < 10) "0${day}" else "$day"
        val sMontOfYear = if ((month + 1) < 10) "0${month}" else "$month"
        val date = "$sDayOfMonth/$sMontOfYear/$year"

        binding.tvSelectDueDate.text = date

        val simpleDF = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        val theDate = simpleDF.parse(date)
        mSelectedDueDate = theDate!!.time
    }
}