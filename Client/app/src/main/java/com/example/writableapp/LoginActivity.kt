package com.example.writableapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.writableapp.Exceptions.IException
import com.example.writableapp.Model.User
import com.example.writableapp.Utils.DesignUtils
import com.example.writableapp.Utils.NetworkUtils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private var password: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check whether the user is connected to the internet => display an activity with some appropriate text in this case
        checkIsOnline()

        redirect_register_link.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        login_button.setOnClickListener {
            try {
                bindLayoutData()
                loginUser()
            } catch (ex: IException) {
                ex.displayMessageWithSnackbar(window.decorView.rootView, this)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkIsOnline()
    }

    private fun checkIsOnline() {
        if (NetworkUtils.getConnectivityStatus(this) == NetworkUtils.TYPE_NO_CONNECTION) {
            startActivity(Intent(this, NoInternetConnectionActivity::class.java))
        }
    }

    private fun bindLayoutData() {
        email = login_email.text.toString()
        password = login_password.text.toString()
    }

    private fun loginUser() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("email", email!!)
            .add("password", password!!)
            .build()

        val httpRequest = Request.Builder().url("http://a1c0-34-91-147-153.ngrok.io/login")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DesignUtils.showSnackbar(
                    window.decorView.rootView,
                    "Could not connect to server!",
                    this@LoginActivity
                )
            }

            override fun onResponse(call: Call, response: Response) {
                when (val responseBody = response.body!!.string()) {
                    "501" -> {
                        DesignUtils.showSnackbar(
                            window.decorView.rootView,
                            "The e-mail address that you entered doesn't match any existing account!",
                            this@LoginActivity
                        )
                    }
                    "502" -> {
                        DesignUtils.showSnackbar(
                            window.decorView.rootView,
                            "The password you entered is incorrect! Please try again.",
                            this@LoginActivity
                        )
                    }
                    else -> {
                        runOnUiThread {
                            val user = Gson().fromJson(responseBody, User::class.java)
                            val mainMenuIntent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                            mainMenuIntent.putExtra("USER_UID", user.getUID())
                            mainMenuIntent.putExtra("USER_DISPLAY_NAME", user.getDisplayName())
                            mainMenuIntent.putExtra("AVATAR_URL", user.getAvatarURL())
                            mainMenuIntent.putExtra("EMAIL", user.getEmail())
                            startActivity(mainMenuIntent)
                        }
                    }
                }
            }

        })
    }

    override fun onBackPressed() {
        // this is empty so that it does not by default go back to LoginActivity from another activity
    }
}