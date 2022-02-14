package com.example.writableapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.writableapp.Utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var password: String ?= null
    private var email: String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Check whether the user is connected to the internet => display an activity with some appropriate text in this case
        checkIsOnline()

        redirect_register_link.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
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
}