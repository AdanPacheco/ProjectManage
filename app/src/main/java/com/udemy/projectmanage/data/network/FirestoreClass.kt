package com.udemy.projectmanage.data.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.udemy.projectmanage.core.Constants
import com.udemy.projectmanage.data.model.Board
import com.udemy.projectmanage.data.model.User

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()
    private var currentUser: User? = null

    fun registerUser(onSuccess: () -> Unit, userInfo: User) {
        mFirestore.collection(Constants.USERS_FIRE_STORE).document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                Log.e("SignUpUser", "ErrorMessage ${e.message}")
            }
    }

    fun getBoardList(onSuccess: (ArrayList<Board>) -> Unit) {

        mFirestore.collection(Constants.BOARDS_FIRE_STORE).whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId()).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val boardList: ArrayList<Board> = ArrayList()
                    task.result.documents.forEach { document ->
                        val board = document.toObject(Board::class.java)
                        if (board != null) {
                            board.documentId = document.id
                        }
                        boardList.add(board!!)
                    }
                    onSuccess(boardList)
                }
            }.addOnFailureListener { exception ->
                Log.i("onFailedBoard", exception.message.toString())
            }
    }

    fun getSignedUser(onSuccess: (user: User) -> Unit) {
        if (currentUser != null) {
            onSuccess(currentUser!!)
            return
        }

        mFirestore.collection(Constants.USERS_FIRE_STORE).document(getCurrentUserId()).get().addOnSuccessListener { document ->
            val loggedInUser = document.toObject(User::class.java)
            if (loggedInUser != null) onSuccess(loggedInUser)
        }.addOnFailureListener { e ->
            Log.e("SignInUser", "ErrorMessage ${e.message}")
        }
    }

    fun updateUserProfileData(user: User, onResponse: (message: String) -> Unit) {
        val docRef = mFirestore.collection(Constants.USERS_FIRE_STORE).document(getCurrentUserId())
        user.id = docRef.id
        mFirestore.collection(Constants.USERS_FIRE_STORE).document(getCurrentUserId()).set(user).addOnSuccessListener {
            onResponse("User data updated successfully")
            currentUser = user
        }.addOnFailureListener { exception ->
            onResponse(exception.message.toString())
        }
    }

    fun createBoard(board: Board, onResponse: (message: String) -> Unit) {
        val docRef = mFirestore.collection(Constants.BOARDS_FIRE_STORE).document()
        board.documentId = docRef.id
        docRef.set(board, SetOptions.merge()).addOnSuccessListener {
            onResponse("Board created successfully")
        }.addOnFailureListener { e ->
            onResponse(e.message.toString())
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun addUpdateBoardTaskList(board: Board, onSuccess: (Board) -> Unit) {
        mFirestore.collection(Constants.BOARDS_FIRE_STORE).document(board.documentId).set(board).addOnSuccessListener {
            onSuccess(board)
        }
    }

    fun getBoardDetails(documentId: String, onSuccess: (Board) -> Unit) {
        mFirestore.collection(Constants.BOARDS_FIRE_STORE).document(documentId).get().addOnSuccessListener { document ->
            val board = document.toObject(Board::class.java)!!

            onSuccess(board)
        }
    }

    fun getAssignedMembersListDetails(assignedTo: ArrayList<String>, onSuccessSetupMembers: (ArrayList<User>) -> Unit) {
        mFirestore.collection(Constants.USERS_FIRE_STORE).whereIn(Constants.ID, assignedTo).get().addOnSuccessListener { result ->
            val userList: ArrayList<User> = ArrayList()
            result.documents.forEach { document ->
                val user = document.toObject(User::class.java)!!
                userList.add(user)
            }
            onSuccessSetupMembers(userList)
        }
    }

    fun getMemberDetails(email: String, onSuccessMemberDetails: (User) -> Unit, onFailedMemberDetails: () -> Unit) {
        mFirestore.collection(Constants.USERS_FIRE_STORE).whereEqualTo(Constants.EMAIL, email).get().addOnSuccessListener { result ->
            if (result.documents.size > 0) {
                val user = result.documents[0].toObject(User::class.java)!!
                onSuccessMemberDetails(user)
            } else {
                onFailedMemberDetails()
            }
        }
    }

    fun asignMemberToBoard(board: Board, user: User, onMemberAssignedSuccess: (User) -> Unit) {
        mFirestore.collection(Constants.BOARDS_FIRE_STORE).document(board.documentId).set(board).addOnSuccessListener { onMemberAssignedSuccess(user)}
    }
}