package com.example.writableapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.writableapp.Model.Project
import com.example.writableapp.R
import com.example.writableapp.Utils.ImageLoaderUtils

class ProjectsAdapter(
    private val mainContext: Context?,
    private val projectsList: ArrayList<Project>): RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ProjectViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return projectsList.size
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projectsList[position]
        holder.bind(mainContext!!, project)
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
                projectCompleted!!.visibility = View.GONE
                projectCompleted!!.text = ""
            }
        }
    }
}