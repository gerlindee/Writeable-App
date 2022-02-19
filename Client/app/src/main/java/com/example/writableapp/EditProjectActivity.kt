package com.example.writableapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.writableapp.Model.Project
import com.example.writableapp.Model.User
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.DesignUtils
import com.example.writableapp.Utils.ImageLoaderUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_change_user_profile.*
import kotlinx.android.synthetic.main.activity_edit_project.*
import okhttp3.*
import java.io.IOException

class EditProjectActivity : AppCompatActivity() {

    private var newImage: ImageView ?= null
    private var newTitle: TextInputEditText ?= null
    private var newDescription: EditText ?= null
    private var newWordCount: TextInputEditText ?= null
    private var newComplete: TextInputEditText ?= null
    private var saveChangesButton: MaterialButton? = null

    private var currentProject: Project? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_project)

        setupLayoutElements()

        setOriginalUserData()

        edit_project_button.setOnClickListener {
            updateProjectData()
        }
    }

    private fun setupLayoutElements() {
        newImage = findViewById(R.id.edit_project_avatar)
        newTitle = findViewById(R.id.edit_project_title)
        newDescription = findViewById(R.id.edit_project_description)
        newWordCount = findViewById(R.id.edit_project_word_count)
        newComplete = findViewById(R.id.edit_project_complete)

        saveChangesButton = findViewById(R.id.edit_project_button)
    }

    private fun setOriginalUserData() {
        currentProject = Project(
            pid = intent.extras!!.get("PROJECT_PID").toString(),
            uid = intent.extras!!.get("PROJECT_UID").toString(),
            title = intent.extras!!.get("PROJECT_TITLE").toString(),
            description = intent.extras!!.get("PROJECT_DESCRIPTION").toString(),
            imageURL = intent.extras!!.get("PROJECT_IMAGEURL").toString(),
            wordCount = intent.extras!!.getString("PROJECT_WORDCOUNT").toString(),
            completed = intent.extras!!.getString("PROJECT_COMPLETED").toString()
        )

        ImageLoaderUtils(this).loadImage(currentProject!!.getImageURL(), newImage!!)
        newTitle!!.setText(currentProject!!.getTitle())
        newDescription!!.setText(currentProject!!.getDescription())
        newWordCount!!.setText(currentProject!!.getWordCount())
        if (currentProject!!.getCompleted() == "True") {
            newComplete!!.setText("Completed")
        } else {
            newComplete!!.setText("In Progress")
        }
    }

    private fun updateProjectData() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("pid", currentProject!!.getPID())
            .add("new_title", newTitle!!.text.toString())
            .add("new_description", newDescription!!.text.toString())
            .add("new_word_count", newWordCount!!.text.toString())
            .add("new_completed", newComplete!!.text.toString())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/updateProject")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DesignUtils.showSnackbar(
                    window.decorView.rootView,
                    "Could not connect to server!",
                    this@EditProjectActivity
                )
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    AlertDialog.Builder(this@EditProjectActivity)
                        .setTitle("Success")
                        .setMessage("Project updated successfully!")
                        .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                            val mainMenuIntent = Intent(this@EditProjectActivity, MainMenuActivity::class.java)
                            mainMenuIntent.putExtra("USER_UID", intent.extras!!.get("USER_UID").toString())
                            mainMenuIntent.putExtra("USER_DISPLAY_NAME", intent.extras!!.get("USER_DISPLAY_NAME").toString())
                            mainMenuIntent.putExtra("USER_AVATAR_URL", intent.extras!!.get("USER_AVATAR_URL").toString())
                            mainMenuIntent.putExtra("USER_EMAIL", intent.extras!!.get("USER_EMAIL").toString())
                            startActivity(mainMenuIntent)
                        })
                        .show()
                }
            }
        })
    }
}