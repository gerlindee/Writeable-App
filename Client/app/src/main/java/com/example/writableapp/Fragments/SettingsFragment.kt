package com.example.writableapp.Fragments

import android.content.Intent
import com.example.writableapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.writableapp.ChangeUserProfileActivity
import com.example.writableapp.LoginActivity
import kotlinx.android.synthetic.main.fragment_settings.*

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

        settings_log_out.setOnClickListener {
            val loginIntent = Intent(context, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        settings_reset_password.setOnClickListener {

        }

        settings_delete_account.setOnClickListener {

        }

        settings_delete_history.setOnClickListener {

        }
    }

    private fun deleteDatabaseAuth() {

    }

    private fun deletePlayingHistory() {

    }
}