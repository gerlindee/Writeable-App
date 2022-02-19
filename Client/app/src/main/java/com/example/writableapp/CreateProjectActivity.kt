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
import androidx.core.content.res.ResourcesCompat
import com.example.writableapp.Exceptions.EmptyFieldsException
import com.example.writableapp.Exceptions.IException
import com.example.writableapp.Model.Project
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.DesignUtils
import kotlinx.android.synthetic.main.activity_create_project.*
import okhttp3.*
import java.io.IOException

class CreateProjectActivity : AppCompatActivity() {

    // Project data from the screen
    private var project: Project ?= null
    private var selectedPhotoUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_project)

        create_project_button.setOnClickListener {
            try {
                bindLayoutData()
                checkFieldsEmpty()
                createProject()
            } catch (ex: IException) {
                ex.displayMessageWithSnackbar(window.decorView.rootView, this)
            }
        }

        create_project_avatar.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 0)
        }

        create_project_avatar_remove.setOnClickListener {
            create_project_avatar_remove.visibility = View.GONE
            selectedPhotoUri = null
            create_project_avatar.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.select_icon_button_square,
                    null
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            create_project_avatar_remove.visibility = View.VISIBLE
            setSelectedAvatar(data)
        }
    }

    @SuppressLint("NewApi")
    private fun setSelectedAvatar(data: Intent) {
        selectedPhotoUri = data.data
        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
        val bitmap = ImageDecoder.decodeBitmap(source)
        create_project_avatar.setImageBitmap(bitmap)
    }

    private fun bindLayoutData() {
        project = Project(
            pid = "",
            uid = intent.extras!!.get("USER_UID").toString(),
            title = create_project_title.text.toString(),
            imageURL = "/content/drive/MyDrive/Colab/book_cover_1.jpg",
            description = create_project_description.text.toString(),
            wordCount = "0" ,// initial value for the word count is 0
            completed = "False"
        )
    }

    private fun checkFieldsEmpty() {
        if (project!!.getTitle().isEmpty() || project!!.getDescription().isEmpty()) {
            throw EmptyFieldsException()
        }
    }

    private fun createProject() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("uid", project!!.getUID())
            .add("title", project!!.getTitle())
            .add("image_url", project!!.getImageURL())
            .add("description", project!!.getDescription())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/createProject")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                DesignUtils.showSnackbar(
                    window.decorView.rootView,
                    "Could not connect to server!",
                    this@CreateProjectActivity
                )
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.body!!.string() == "200") {
                    runOnUiThread {
                        AlertDialog.Builder(this@CreateProjectActivity)
                            .setTitle("Success")
                            .setMessage("Project created successfully!")
                            .setPositiveButton("Confirm", DialogInterface.OnClickListener { _, _ ->
                                run {
                                    val mainMenuIntent = Intent(this@CreateProjectActivity, MainMenuActivity::class.java)
                                    mainMenuIntent.putExtra("USER_UID", intent.extras!!.get("USER_UID").toString())
                                    mainMenuIntent.putExtra("USER_DISPLAY_NAME", intent.extras!!.get("USER_DISPLAY_NAME").toString())
                                    mainMenuIntent.putExtra("USER_AVATAR_URL", intent.extras!!.get("USER_AVATAR_URL").toString())
                                    mainMenuIntent.putExtra("USER_EMAIL", intent.extras!!.get("USER_EMAIL").toString())
                                    startActivity(mainMenuIntent)
                                }
                            })
                            .show()
                    }
                } else {
                    DesignUtils.showSnackbar(
                        window.decorView.rootView,
                        "Could not connect to server!",
                        this@CreateProjectActivity
                    )
                }
            }
        })
    }
}