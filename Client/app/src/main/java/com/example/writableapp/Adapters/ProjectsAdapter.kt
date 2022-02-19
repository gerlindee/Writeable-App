package com.example.writableapp.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.writableapp.EditProjectActivity
import com.example.writableapp.Fragments.ProjectsFragment
import com.example.writableapp.MainMenuActivity
import com.example.writableapp.Model.Project
import com.example.writableapp.Model.User
import com.example.writableapp.R
import com.example.writableapp.Utils.Constants
import com.example.writableapp.Utils.ImageLoaderUtils
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

class ProjectsAdapter(
    private val currentUser: User,
    private val mainContext: Context?,
    private val projectsList: ArrayList<Project>): RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProjectViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return projectsList.size
    }

    override fun onBindViewHolder(
        holder: ProjectViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val project = projectsList[position]
        holder.bind(mainContext!!, project)

        holder.editButton!!.setOnClickListener {
            val editProjectIntent = Intent(mainContext, EditProjectActivity::class.java)
            editProjectIntent.putExtra("PROJECT_PID", project.getPID())
            editProjectIntent.putExtra("PROJECT_UID", project.getUID())
            editProjectIntent.putExtra("PROJECT_TITLE", project.getTitle())
            editProjectIntent.putExtra("PROJECT_DESCRIPTION", project.getDescription())
            editProjectIntent.putExtra("PROJECT_IMAGEURL", project.getImageURL())
            editProjectIntent.putExtra("PROJECT_WORDCOUNT", project.getWordCount())
            editProjectIntent.putExtra("PROJECT_COMPLETED", project.getCompleted())

            editProjectIntent.putExtra("USER_UID", currentUser.getUID())
            editProjectIntent.putExtra("USER_DISPLAY_NAME", currentUser.getDisplayName())
            editProjectIntent.putExtra("USER_AVATAR_URL", currentUser.getAvatarURL())
            editProjectIntent.putExtra("USER_EMAIL", currentUser.getEmail())
            mainContext.startActivity(editProjectIntent)
        }

        holder.deleteButton!!.setOnClickListener {
            val okHttpClient = OkHttpClient()

            val requestBody = FormBody.Builder()
                .add("pid", project.getPID())
                .build()

            val httpRequest = Request.Builder().url(Constants.serverURL + "/deleteProject")
                .post(requestBody)
                .build()

            okHttpClient.newCall(httpRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (mainContext is MainMenuActivity) {
                        (mainContext as MainMenuActivity).runOnUiThread {
                            Toast.makeText(mainContext, "Could not connect to server!", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    if (mainContext is MainMenuActivity) {
                        (mainContext as MainMenuActivity).runOnUiThread {
                            projectsList.removeAt(position)
                            notifyDataSetChanged()
                        }
                    }
                }
            })
        }
    }

    class ProjectViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.view_project_card, parent, false)) {

        var projectIcon: ImageView ?= null
        var projectTitle: TextView ?= null
        var projectWordCount: TextView ?= null
        var projectDescription: TextView ?= null
        var projectCompleted: TextView ?= null

        var editButton: ImageView ?= null
        var deleteButton: ImageView ?= null

        init {
            projectIcon = itemView.findViewById(R.id.project_icon)
            projectTitle = itemView.findViewById(R.id.project_title)
            projectWordCount = itemView.findViewById(R.id.project_wordcount)
            projectDescription = itemView.findViewById(R.id.project_description)
            projectCompleted = itemView.findViewById(R.id.project_completed)

            editButton = itemView.findViewById(R.id.project_edit_button)
            deleteButton = itemView.findViewById(R.id.project_delete_button)
        }

        fun bind(mainContext: Context, project: Project) {
            ImageLoaderUtils(mainContext).loadImage(project.getImageURL(), projectIcon!!)

            projectTitle!!.text = project.getTitle()
            projectWordCount!!.text = project.getWordCount()
            projectDescription!!.text = project.getDescription()
            if (project.getCompleted() == "True") {
                projectCompleted!!.visibility = View.VISIBLE
                projectCompleted!!.text = "Complete"
            } else {
                projectCompleted!!.visibility = View.VISIBLE
                projectCompleted!!.text = "In Progress"
            }
        }
    }
}