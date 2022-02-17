package com.example.writableapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.app.AlertDialog
import android.content.DialogInterface
import com.example.writableapp.Model.User
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.DesignUtils
import com.example.writableapp.Utils.ImageLoaderUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_change_user_profile.*
import okhttp3.*
import java.io.IOException

class ChangeUserProfileActivity : AppCompatActivity() {

    private var avatarImageView: CircleImageView ?= null
    private var nameTextView: TextInputEditText ?= null
    private var emailTextView: TextInputEditText ?= null
    private var passwordTextLayout: TextInputLayout ?= null
    private var passwordTextView: TextInputEditText ?= null
    private var saveChangesButton: MaterialButton ?= null

    private var selectedPhotoUri: Uri?= null
    private var pictureWasUpdated = false

    private var originalDisplayName: String ?= null
    private var originalEmail: String ?= null
    private var originalAvatarURL: String ?= null
    private var userUID: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_user_profile)

        setupLayoutElements()

        setOriginalUserData()

        button_change_name.setOnClickListener {
            val wasEnabled = nameTextView!!.isEnabled
            nameTextView!!.isEnabled = !wasEnabled
            enableConfirmationAndSaving()
        }

        button_change_email.setOnClickListener {
            val wasEnabled = emailTextView!!.isEnabled
            emailTextView!!.isEnabled = !wasEnabled
            enableConfirmationAndSaving()
        }

        change_icon.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        change_icon_remove.setOnClickListener {
            change_icon_remove.visibility = View.GONE
            selectedPhotoUri = null
            ImageLoaderUtils(applicationContext).loadImage(originalAvatarURL!!, avatarImageView!!)
            enableConfirmationAndSaving()
        }

        change_confirm_button.setOnClickListener {
            if (passwordTextView!!.text!!.isEmpty() && wereChangesMade()) {
                DesignUtils.showSnackbar(window.decorView.rootView, "Please provide the password associated with your account in order to confirm the account changes!", this)
            } else {
                updateUserData()
            }
        }
    }

    private fun setupLayoutElements() {
        avatarImageView = findViewById(R.id.change_icon)
        nameTextView = findViewById(R.id.change_name)
        emailTextView = findViewById(R.id.change_email)
        passwordTextLayout = findViewById(R.id.input_layout_confirm_password)
        passwordTextView = findViewById(R.id.change_confirm_password)
        saveChangesButton = findViewById(R.id.change_confirm_button)
    }

    private fun setOriginalUserData() {
        userUID = intent.extras!!.get("USER_UID").toString()
        originalDisplayName = intent.extras!!.get("USER_DISPLAY_NAME").toString()
        originalEmail = intent.extras!!.get("USER_EMAIL").toString()
        originalAvatarURL = intent.extras!!.get("USER_AVATAR_URL").toString()

        nameTextView!!.setText(originalDisplayName)
        emailTextView!!.setText(originalEmail)
        ImageLoaderUtils(applicationContext).loadImage(originalAvatarURL!!, avatarImageView!!)
    }

    private fun enableConfirmationAndSaving() {
        if (passwordTextLayout!!.visibility == View.GONE) {
            passwordTextLayout!!.visibility = View.VISIBLE
            saveChangesButton!!.visibility = View.VISIBLE
        } else {
            if (!wereChangesMade()) {
                passwordTextLayout!!.visibility = View.GONE
                saveChangesButton!!.visibility = View.GONE
            }
        }
    }

    private fun wereChangesMade(): Boolean {
        return selectedPhotoUri != null ||
               emailTextView!!.text.toString() != originalEmail ||
               nameTextView!!.text.toString() != originalDisplayName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            change_icon_remove.visibility = View.VISIBLE
            setSelectedAvatar(data)
            enableConfirmationAndSaving()
        }
    }

    @SuppressLint("NewApi")
    private fun setSelectedAvatar(uploadData: Intent) {
        selectedPhotoUri = uploadData.data
        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
        val bitmap = ImageDecoder.decodeBitmap(source)
        change_icon.setImageBitmap(bitmap)
    }

    private fun updateUserData() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("user_uid", userUID!!)
            .add("new_display_name", nameTextView!!.text.toString())
            .add("new_email", emailTextView!!.text.toString())
            .add("new_avatar_url", "/content/drive/MyDrive/Colab/20220216080043.png")
            .add("confirm_password", passwordTextView!!.text.toString())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/updateUser")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DesignUtils.showSnackbar(
                    window.decorView.rootView,
                    "Could not connect to server!",
                    this@ChangeUserProfileActivity
                )
            }

            override fun onResponse(call: Call, response: Response) {
                when (val responseBody = response.body!!.string()) {
                    "501" -> {
                        DesignUtils.showSnackbar(
                            window.decorView.rootView,
                            "Internal database error! No user could be found for the current session!",
                            this@ChangeUserProfileActivity
                        )
                    }
                    "502" -> {
                        DesignUtils.showSnackbar(
                            window.decorView.rootView,
                            "The password you entered is incorrect! Please try again.",
                            this@ChangeUserProfileActivity
                        )
                    }
                    else -> {
                        runOnUiThread {
                            AlertDialog.Builder(this@ChangeUserProfileActivity)
                                .setTitle("Success")
                                .setMessage("Account updated successfully!")
                                .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                                    run {
                                        val user = Gson().fromJson(responseBody, User::class.java)
                                        val mainMenuIntent = Intent(this@ChangeUserProfileActivity, MainMenuActivity::class.java)
                                        mainMenuIntent.putExtra("USER_UID", user.getUID())
                                        mainMenuIntent.putExtra("USER_DISPLAY_NAME", user.getDisplayName())
                                        mainMenuIntent.putExtra("USER_AVATAR_URL", user.getAvatarURL())
                                        mainMenuIntent.putExtra("USER_EMAIL", user.getEmail())
                                        startActivity(mainMenuIntent)
                                    }
                                })
                                .show()
                        }
                    }
                }
            }
        })
    }
}