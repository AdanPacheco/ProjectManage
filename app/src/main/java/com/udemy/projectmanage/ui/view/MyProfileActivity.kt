package com.udemy.projectmanage.ui.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.udemy.projectmanage.R
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.data.network.FirestoreClass
import com.udemy.projectmanage.data.network.FirestoreStorageClass
import com.udemy.projectmanage.data.model.User
import com.udemy.projectmanage.databinding.ActivityMyProfileBinding
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var binding: ActivityMyProfileBinding
    private val openGalleryLauncher: ActivityResultLauncher<Intent> = openGalleryResultLauncher()
    private lateinit var mUserDetails: User
    private var mImageProfileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        FirestoreClass().getSignedUser { user -> getSignedUserData(user) }
        initListeners()
    }


    private fun initListeners() {
        binding.btnUpdate.setOnClickListener {
            uploadImageToStorage()
        }

        binding.ivProfileUserImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(galleryIntent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            }
        }
    }

    private fun openGalleryResultLauncher() = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                mImageProfileUri = result.data?.data
                Glide.with(this).load(mImageProfileUri).centerCrop().placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivProfileUserImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            openGalleryLauncher.launch(galleryIntent)
        } else {
            Toast.makeText(
                this, "Oops, you just denied the permission for storage. You can also allow it from settings.", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarMyProfileActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = resources.getString(R.string.my_profile)
        }

        binding.toolbarMyProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getSignedUserData(user: User) {
        mUserDetails = user
        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) binding.etMobile.setText("${user.mobile}")
        Glide.with(this).load(user.image).centerCrop().placeholder(R.drawable.ic_user_place_holder).into(binding.ivProfileUserImage)
    }

    private fun uploadImageToStorage() {
        if (mImageProfileUri != null) {
            showProgressDialog(resources.getString(R.string.please_wait))
            Constants.getFileExtension(this,mImageProfileUri)?.let { extension ->
                FirestoreStorageClass().uploadImageToStorage(extension, mImageProfileUri!!) { uri, message ->
                    onResponseUploadImage(uri, message)
                }
            }
        }
    }

    private fun setDataToUserObject() {
        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()) {
            mUserDetails.mobile = binding.etMobile.text.toString().toLong()
        }
        if (binding.etName.text.toString() != mUserDetails.name) {
            mUserDetails.name = binding.etName.text.toString()
        }

        if (mImageProfileUri != null && mImageProfileUri.toString() != mUserDetails.image) {
            mUserDetails.image = mImageProfileUri.toString()
        }

        FirestoreClass().updateUserProfileData(mUserDetails) { message -> onResponseUploadUserData(message) }

    }

    private fun onResponseUploadImage(uri: Uri, message: String) {
        mImageProfileUri = uri
        hideProgressDialog()
        if (message.isEmpty()) {
            setDataToUserObject()
            return
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun onResponseUploadUserData(message: String) {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }



}