package com.example.writableapp.Fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import com.example.writableapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.writableapp.ChangeUserProfileActivity
import com.example.writableapp.LoginActivity
import com.example.writableapp.MainMenuActivity
import com.example.writableapp.Model.User
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.DesignUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.transaction_password_reset.view.*
import okhttp3.*
import java.io.IOException

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settings_change_profile.setOnClickListener {
            val mainMenuIntent = requireActivity().intent
            val changeProfileIntent = Intent(context, ChangeUserProfileActivity::class.java)
            changeProfileIntent.putExtra("USER_DISPLAY_NAME", mainMenuIntent.extras!!.get("USER_DISPLAY_NAME").toString())
            changeProfileIntent.putExtra("USER_EMAIL", mainMenuIntent.extras!!.get("USER_EMAIL").toString())
            changeProfileIntent.putExtra("USER_AVATAR_URL", mainMenuIntent.extras!!.get("USER_AVATAR_URL").toString())
            changeProfileIntent.putExtra("USER_UID", mainMenuIntent.extras!!.get("USER_UID").toString())
            startActivity(changeProfileIntent)
        }

        settings_reset_password.setOnClickListener {
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.transaction_password_reset, null)

            AlertDialog.Builder(context)
                .setTitle("Reset Password")
                .setView(dialogView)
                .setPositiveButton("Confirm") { _, _ ->
                    run {
                        val currentPassword = dialogView.reset_password_current.text.toString()
                        val newPassword = dialogView.reset_password_new.text.toString()

                        if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                            Toast.makeText(context, "Please fill out all fields!", Toast.LENGTH_LONG).show()
                        } else {
                            val okHttpClient = OkHttpClient()

                            val requestBody = FormBody.Builder()
                                .add("user_uid", requireActivity().intent.extras!!.get("USER_UID").toString())
                                .add("current_password", currentPassword)
                                .add("new_password", newPassword)
                                .build()

                            val httpRequest = Request.Builder().url(Constants.serverURL + "/resetPassword")
                                .post(requestBody)
                                .build()

                            okHttpClient.newCall(httpRequest).enqueue(object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    Toast.makeText(context, "Could not connect to server!", Toast.LENGTH_LONG).show()
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    var message : String = ""
                                    when (val responseBody = response.body!!.string()) {
                                        "501" -> {
                                            message = "Internal database error! No user could be found for the current session!"
                                        }
                                        "502" -> {
                                            message = "The password you entered is incorrect! Please try again."

                                        }
                                        "200" -> {
                                            message = "Password was changed successfully!"
                                        }
                                        else -> {
                                            message = "An unexpected error occurred! Please try again."
                                        }
                                    }
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            })
                        }
                    }
                }
                .show()
        }

        settings_delete_account.setOnClickListener {
            //TODO
        }

        settings_log_out.setOnClickListener {
            val loginIntent = Intent(context, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        settings_delete_history.setOnClickListener {
            //TODO
        }
    }

    private fun deleteDatabaseAuth() {

    }

    private fun deletePlayingHistory() {

    }
}