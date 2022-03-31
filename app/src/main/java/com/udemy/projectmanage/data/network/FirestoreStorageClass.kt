package com.udemy.projectmanage.data.network

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.udemy.projectmanage.core.Constants

class FirestoreStorageClass {

    private val mFireStorage = FirebaseStorage.getInstance()

    fun uploadImageToStorage(extension: String, imageUri: Uri, onResponse: (uri: Uri, message: String) -> Unit) {
        var uri = imageUri

        val sRef: StorageReference = mFireStorage.reference.child(Constants.IMAGE_PATH + System.currentTimeMillis() + "." + extension)

        sRef.putFile(imageUri).addOnSuccessListener { task ->
            task.metadata!!.reference!!.downloadUrl.addOnSuccessListener { URI ->
                uri = URI
                onResponse(uri, "")
            }.addOnFailureListener { exception ->
                onResponse(uri, exception.message.toString())
            }
        }.addOnFailureListener { exception ->
            onResponse(uri, exception.message.toString())
        }

    }
}