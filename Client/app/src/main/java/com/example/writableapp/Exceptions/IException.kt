package com.example.writableapp.Exceptions

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.writableapp.Utils.DesignUtils
import java.lang.Exception

abstract class IException(message: String): Exception(message) {

    fun displayMessageWithSnackbar(view: View, context: Context) {
        DesignUtils.showSnackbar(view, message!!, context)
    }

    fun displayMessageWithToast(context: Context) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}