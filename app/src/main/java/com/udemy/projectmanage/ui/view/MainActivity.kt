package com.udemy.projectmanage.ui.view


import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.data.network.FirestoreClass
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ActivityMainBinding
import com.udemy.projectmanage.databinding.NavHeaderMainBinding
import com.udemy.projectmanage.ui.adapters.board.BoardItemsAdapter

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navViewHeaderBinding: NavHeaderMainBinding
    private var profileLauncher: ActivityResultLauncher<Intent> = profileResultLauncher()
    private var createBoardLauncher: ActivityResultLauncher<Intent> = createOrDetailBoardResultLauncher()
    private var detailBoardLauncher: ActivityResultLauncher<Intent> = createOrDetailBoardResultLauncher()
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navViewHeaderBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))
        setContentView(binding.root)
        FirestoreClass().getSignedUser { user -> updateNavigationDrawerUserDetails(user) }
        setupActionBar()
        initListeners()
        getBoards()

    }

    private fun getSharedPreferences() {
            mSharedPreferences = this.getSharedPreferences(Constants.PROGEMANAG_PREFERENCES, MODE_PRIVATE)
            val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

            if (!tokenUpdated) {
                FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) updateFCMToken(task.result!!.token)
                }
            }
    }

    private fun getBoards() {
        showProgressDialog(getString(R.string.please_wait))
        FirestoreClass().getBoardList { boardList -> populateBoardListToUi(boardList) }
    }

    private fun initListeners() {
        binding.navView.setNavigationItemSelectedListener(this)

        binding.layoutAppBar.fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUser.name)
            createBoardLauncher.launch(intent)
        }
    }

    private fun populateBoardListToUi(boardList: ArrayList<Board>) {
        hideProgressDialog()
        if (boardList.size > 0) {
            binding.layoutAppBar.mainContent.rvBoardsList.visibility = View.VISIBLE
            binding.layoutAppBar.mainContent.tvNoBoardsAvailable.visibility = View.GONE

            binding.layoutAppBar.mainContent.rvBoardsList.layoutManager = LinearLayoutManager(this)
            val adapter = BoardItemsAdapter(boardList) { board ->
                onClick(board)
            }
            binding.layoutAppBar.mainContent.rvBoardsList.setHasFixedSize(true)
            binding.layoutAppBar.mainContent.rvBoardsList.adapter = adapter
        } else {
            binding.layoutAppBar.mainContent.rvBoardsList.visibility = View.GONE
            binding.layoutAppBar.mainContent.tvNoBoardsAvailable.visibility = View.VISIBLE
        }

    }

    private fun onClick(board: Board) { //handle click on board item
        val intent = Intent(this, TaskListActivity::class.java)
        intent.putExtra(Constants.BOARD_ITEM, board)
        detailBoardLauncher.launch(intent)
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.layoutAppBar.toolbarMainActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_nav)
        }

        binding.layoutAppBar.toolbarMainActivity.setNavigationOnClickListener { toggleDrawer() }
    }

    private fun updateNavigationDrawerUserDetails(user: User) {
        mUser = user
        Glide.with(this).load(user.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(navViewHeaderBinding.ivUser)
        navViewHeaderBinding.tvUsername.text = user.name
        getSharedPreferences()
    }

    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                profileLauncher.launch(Intent(this, MyProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                mSharedPreferences.edit().clear().apply()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun createOrDetailBoardResultLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                showProgressDialog(getString(R.string.please_wait))
                FirestoreClass().getBoardList { boardList -> populateBoardListToUi(boardList) }
            }
        }

    private fun profileResultLauncher() = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            FirestoreClass().getSignedUser { user -> updateNavigationDrawerUserDetails(user) }
        }
    }

    private fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        FirestoreClass().getSignedUser { user -> updateNavigationDrawerUserDetails(user) }
    }

    private fun updateFCMToken(token: String) {
        mUser.fcmToken = token
        FirestoreClass().updateUserProfileData(mUser) { tokenUpdateSuccess() }
    }

}