package com.example.writableapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import com.example.writableapp.Exceptions.EmptyFieldsException
import com.example.writableapp.Exceptions.IException
import com.example.writableapp.Exceptions.UnmatchedPasswordsException
import com.example.writableapp.Model.User
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.DesignUtils
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.*
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    // Web view for the Terms and Conditions
    private var webView: WebView? = null

    // User data from the screen
    private var user: User? = null
    private var repeatedPassword: String? = null
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Create instance of the web view
        webView = WebView(this)

        // Set the Terms and Conditions part of the string to an URL
        val termsAndConditionsMessage =
            "I have read and therefore agree with the " + "<u>" + "Terms and Conditions" + "</u>" + "."
        text_terms_service.text =
            HtmlCompat.fromHtml(termsAndConditionsMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // The register button is only enabled when the user has checked the Terms and Conditions box
        checkbox_terms_service.setOnCheckedChangeListener { _, isChecked ->
            register_button.isEnabled = isChecked
        }

        register_button.setOnClickListener {
            try {
                bindLayoutData()
                checkFieldsEmpty()
                checkPasswordsMatch()
                createAccount()
            } catch (ex: IException) {
                ex.displayMessageWithSnackbar(window.decorView.rootView, this)
            }

        }

        text_terms_service.setOnClickListener {
            loadTermsAndConditions()
        }

        register_avatar_civ.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 0)
        }

        register_avatar_remove.setOnClickListener {
            register_avatar_remove.visibility = View.GONE
            selectedPhotoUri = null
            register_avatar_civ.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.default_icon,
                    null
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            register_avatar_remove.visibility = View.VISIBLE
            setSelectedAvatar(data)
        }
    }

    @SuppressLint("NewApi")
    private fun setSelectedAvatar(data: Intent) {
        selectedPhotoUri = data.data
        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
        val bitmap = ImageDecoder.decodeBitmap(source)
        register_avatar_civ.setImageBitmap(bitmap)
    }

    private fun bindLayoutData() {
        user = User(
            "",
            register_username.text.toString(),
            "/content/drive/MyDrive/Colab/20220216080735.png",
            register_email.text.toString(),
            register_password.text.toString()
        )
        repeatedPassword = register_r_password.text.toString()
    }

    private fun checkFieldsEmpty() {
        if (user!!.getPassword().isEmpty() ||
            user!!.getEmail().isEmpty() ||
            repeatedPassword!!.isEmpty() ||
            user!!.getDisplayName().isEmpty()
        ) {
            throw EmptyFieldsException()
        }
    }

    private fun checkPasswordsMatch() {
        if (user!!.getPassword() != repeatedPassword) {
            throw UnmatchedPasswordsException()
        }
    }

    private fun createAccount() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("display_name", user!!.getDisplayName())
            .add("avatar_url", user!!.getAvatarURL())
            .add("email", user!!.getEmail())
            .add("password", user!!.getPassword())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/register")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DesignUtils.showSnackbar(
                    window.decorView.rootView,
                    "Could not connect to server!",
                    this@RegisterActivity
                )
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body!!.string() == "200") {
                    runOnUiThread {
                        AlertDialog.Builder(this@RegisterActivity)
                            .setTitle("Success")
                            .setMessage("Account created successfully!")
                            .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                                run {
                                    val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                    startActivity(loginIntent)
                                }
                            })
                            .show()
                    }
                } else {
                    DesignUtils.showSnackbar(
                        window.decorView.rootView,
                        response.body!!.string(),
                        this@RegisterActivity
                    )
                }
            }

        })
    }

    // Open the web view with the Terms and Conditions
    private fun loadTermsAndConditions() {
        setContentView(webView)
        webView?.loadUrl("https://www.websitepolicies.com/policies/view/FVj4pExJ")
    }
}