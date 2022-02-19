package com.example.writableapp.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.writableapp.Adapters.ProjectsAdapter
import com.example.writableapp.ChangeUserProfileActivity
import com.example.writableapp.CreateProjectActivity
import com.example.writableapp.Model.Project
import com.example.writableapp.R
import com.example.writableapp.Utils.Constants
import com.google.android.material.button.MaterialButton
import kotlinx.android.synthetic.main.fragment_projects.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

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
            createProjectIntent.putExtra("USER_UID", mainMenuIntent.extras!!.get("USER_UID").toString())
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
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("uid", requireActivity().intent.extras!!.get("USER_UID").toString())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/getProjects")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Could not connect to server!", Toast.LENGTH_LONG).show()
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body!!.string()
                if (responseBody == "501") {
                    requireActivity().runOnUiThread {
                        noProjectsLayout!!.visibility = View.VISIBLE
                        userProjectsView!!.visibility = View.GONE
                    }
                } else {
                    noProjectsLayout!!.visibility = View.GONE
                    val jsonObject = JSONObject(responseBody)
                    requireActivity().runOnUiThread {
                        userProjectsView!!.visibility = View.VISIBLE
                        val jsonArray = JSONArray(jsonObject.getString("response"))
                        for (idx in 0 until jsonArray.length()) {
                            val jsonArrayElement = jsonArray.getJSONObject(idx)
                            val project = Project(
                                pid = jsonArrayElement.getString("PID"),
                                uid = jsonArrayElement.getString("UID"),
                                title = jsonArrayElement.getString("Title"),
                                imageURL = jsonArrayElement.getString("Image_URL"),
                                description = jsonArrayElement.getString("Description"),
                                wordCount = jsonArrayElement.getString("Word_Count"),
                                completed = jsonArrayElement.getString("Completed")
                            )

                            userProjects.add(project)
                            userProjectsView!!.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = ProjectsAdapter(context, userProjects)
                                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                            }
                            userProjectsView!!.adapter?.notifyItemInserted(userProjects.size + 1)
                            userProjectsView!!.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })
    }
}