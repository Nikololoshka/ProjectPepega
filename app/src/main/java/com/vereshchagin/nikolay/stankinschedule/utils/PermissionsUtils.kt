package com.vereshchagin.nikolay.stankinschedule.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat


class PermissionsUtils {
    companion object {

        fun isGrand(grandResult: IntArray): Boolean {
            return grandResult.isNotEmpty() &&
                grandResult.first() == PackageManager.PERMISSION_GRANTED
        }

        fun checkGrandPermission(context: Context, permission: String): Boolean {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                permission
            )
            return permissionStatus != PackageManager.PERMISSION_GRANTED
        }
    }
}