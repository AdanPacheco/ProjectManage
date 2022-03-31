package com.udemy.projectmanage.core

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.udemy.projectmanage.R

object Constants {

    const val BOARD_ITEM: String = "boardItem"
    const val ASSIGNED_TO: String = "assignedTo"
    const val BOARDS_FIRE_STORE: String = "boards"
    const val USERS_FIRE_STORE: String = "users"
    const val IMAGE_PATH: String = "image"
    const val NAME: String = "name"
    const val BOARD_DETAILS: String = "board_detail"
    const val ID: String = "id"
    const val EMAIL: String = "email"
    const val TASK_LIST_ITEM_POSITION = "task_list_item_position"
    const val CARD_LIST_ITEM_POSITION = "card_list_item_position"
    const val BOARD_MEMBERS_LIST = "board_member_list"
    const val SELECT: String = "select"
    const val UN_SELECT: String = "Unselect"
    const val PROGEMANAG_PREFERENCES: String = "ProjemanagPrefs"
    const val FCM_TOKEN:String = "fcmToken"
    const val FCM_TOKEN_UPDATED:String = "fcmTokenUpdated"

    const val FCM_BASE_URL:String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION:String = "authorization"
    const val FCM_KEY:String = "key"
    const val FCM_SERVER_KEY:String = "AAAAS89Dyr8:APA91bGv8zWOwYD4bhxmFFtyAjvWAr7W6oGY94OtwpGMXWXzbRE_taHcxde6Bt1DYTqrt-q9d81aSFMumaYH8EBSQKFAwmO8n4nxRlCbPWOMOETMRl6uxYgGle366ZzKguhabqfN1EIi"
    const val FCM_KEY_TITLE:String = "title"
    const val FCM_KEY_MESSAGE:String = "message"
    const val FCM_KEY_DATA:String = "data"
    const val FCM_KEY_TO:String = "to"

    fun getFileExtension(context: Context, uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri!!))
    }

    fun colorsList(context: Context): ArrayList<String> {
        return ArrayList(context.resources.getStringArray(R.array.label_colors).asList())
    }
}