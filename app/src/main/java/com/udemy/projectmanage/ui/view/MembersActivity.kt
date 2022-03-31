package com.udemy.projectmanage.ui.view

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.data.network.FirestoreClass
import com.udemy.projectmanage.databinding.ActivityMembersBinding
import com.udemy.projectmanage.databinding.DialogSearchMemberBinding
import com.udemy.projectmanage.ui.adapters.member.MembersListItemsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {

    private lateinit var mBoard: Board
    private lateinit var mAssignedToMembersList: ArrayList<User>

    private lateinit var binding: ActivityMembersBinding
    private lateinit var bindingDialog: DialogSearchMemberBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkExtras()
        setupActionBar()
    }

    private fun setupMemberList(memberList: ArrayList<User>) {
        hideProgressDialog()
        mAssignedToMembersList = memberList
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MembersListItemsAdapter(memberList) { user, _ -> onSuccessMemberDetails(user) }
        binding.rvMembersList.adapter = adapter
    }

    private fun checkExtras() {
        if (intent.hasExtra(Constants.BOARD_DETAILS)) {
            mBoard = intent.getParcelableExtra(Constants.BOARD_DETAILS)!!
            showProgressDialog(getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersListDetails(mBoard.assignedTo) { memberList -> setupMemberList(memberList) }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.members)
        }
        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        bindingDialog = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(bindingDialog.root)
        bindingDialog.tvAdd.setOnClickListener {
            val email = bindingDialog.etEmailSearchMember.text.toString()
            if (email.isNotEmpty()) {
                dialog.dismiss()
                showProgressDialog(getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(email, { user -> onSuccessMemberDetails(user) }, { onFailedMemberDetails() })
            } else {
                Toast.makeText(this, "Please enter member email address", Toast.LENGTH_SHORT).show()
            }
        }
        bindingDialog.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun onSuccessMemberDetails(user: User) {
        mBoard.assignedTo.add(user.id)
        FirestoreClass().asignMemberToBoard(mBoard, user) { user -> onMemberAssignedSuccess(user) }
    }

    private fun onFailedMemberDetails() {
        hideProgressDialog()
        showErrorSnackBar("No such member found")
    }

    private fun onMemberAssignedSuccess(user: User) {
        hideProgressDialog()
        mAssignedToMembersList.add(user)
        setResult(Activity.RESULT_OK)
        setupMemberList(mAssignedToMembersList)

        lifecycleScope.launch(Dispatchers.IO) {
            val result = sendNotification(mBoard.name, user.fcmToken)
            Toast.makeText(this@MembersActivity, result, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification(name: String, fcmToken: String): String {
        var result: String
        var connection: HttpURLConnection? = null
        try {
            val url = URL(Constants.FCM_BASE_URL)
            connection = url.openConnection() as HttpURLConnection
            connection.doOutput = true
            connection.doInput = true
            connection.instanceFollowRedirects = false
            connection.requestMethod = "POST"

            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Accept", "application/json")

            connection.setRequestProperty(Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}")

            connection.useCaches = false

            val writer = DataOutputStream(connection.outputStream)
            val jsonRequest = JSONObject()
            val dataObject = JSONObject()
            dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $name")
            dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the board by ${mAssignedToMembersList[0].name}")

            jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
            jsonRequest.put(Constants.FCM_KEY_TO, fcmToken)

            writer.writeBytes(jsonRequest.toString())
            writer.flush()
            writer.close()

            val httpResult: Int = connection.responseCode
            if (httpResult == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))

                val stringBuilder = StringBuilder()
                var line: String?

                try {

                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line + "\n")
                    }
                } catch (e: IOException) {

                    e.printStackTrace()
                } finally {

                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                result = stringBuilder.toString()
            } else {
                result = connection.responseMessage
            }
        } catch (e: SocketTimeoutException) {
            e.printStackTrace()
            result = "Connection Timeout"
        } catch (e: Exception) {
            result = "Error: ${e.message}"
        } finally {
            connection?.disconnect()
        }
        return result
    }
}