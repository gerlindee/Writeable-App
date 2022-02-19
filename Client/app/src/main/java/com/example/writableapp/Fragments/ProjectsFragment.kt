package com.example.writableapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.writableapp.ChangeUserProfileActivity
import com.example.writableapp.CreateProjectActivity
import com.example.writableapp.Model.Project
import com.example.writableapp.R
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_projects.*

class ProjectsFragment : Fragment() {

    private var userProjects = ArrayList<Project>()
    private var userProjectsView: RecyclerView ?= null
    private var noProjectsLayout: LinearLayout ?= null
    private var createProjectButton: MaterialButton ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeLayoutElements()

        setupProjects()

        button_create_project.setOnClickListener {
            val mainMenuIntent = requireActivity().intent
            val createProjectIntent = Intent(context, CreateProjectActivity::class.java)
            createProjectIntent.putExtra("USER_ID", mainMenuIntent.extras!!.get("USER_ID").toString())
            createProjectIntent.putExtra("USER_EMAIL", mainMenuIntent.extras!!.get("USER_EMAIL").toString())
            createProjectIntent.putExtra("USER_AVATAR_URL", mainMenuIntent.extras!!.get("USER_AVATAR_URL").toString())
            createProjectIntent.putExtra("USER_UID", mainMenuIntent.extras!!.get("USER_UID").toString())

            startActivity(createProjectIntent)
        }
    }

    private fun initializeLayoutElements() {
        userProjectsView = view?.findViewById(R.id.recycler_view_projects)
        noProjectsLayout = view?.findViewById(R.id.layout_no_projects)
        createProjectButton = view?.findViewById(R.id.button_create_project)
    }

    private fun setupProjects() {

    }
}