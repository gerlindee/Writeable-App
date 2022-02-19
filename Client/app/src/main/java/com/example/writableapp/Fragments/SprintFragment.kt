package com.example.writableapp.Fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.writableapp.R
import com.example.writableapp.Utils.Constants
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_sprint.*
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit

class SprintFragment : Fragment() {

    private var sprintTimeText: TextView? = null
    private var sprintProjectTitleLayout: TextInputLayout? = null
    private var sprintProjectTitle: TextInputEditText? = null
    private var sprintProjectWordCountLayout: TextInputLayout? = null
    private var sprintProjectWordCount: TextInputEditText? = null
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLayoutElements()

        sprint_start_button.setOnClickListener {
            setTimer()
            sprint_start_button.isEnabled = false
            sprint_start_button.visibility = View.GONE
            sprintProjectTitleLayout!!.visibility = View.GONE
            sprintProjectWordCountLayout!!.visibility = View.GONE
            sprint_update_word_count_button.visibility = View.GONE

            sprint_end_button.isEnabled = true
            sprint_end_button.visibility = View.VISIBLE
        }

        sprint_end_button.setOnClickListener {
            timer!!.cancel()
            timer!!.onFinish()
        }

        sprint_update_word_count_button.setOnClickListener {
            updateWordCount()
        }
    }

    private fun setupLayoutElements() {
        sprintTimeText = view?.findViewById(R.id.sprint_time)
        sprintProjectTitleLayout = view?.findViewById(R.id.sprint_project_title_layout)
        sprintProjectTitle = view?.findViewById(R.id.sprint_project_title)
        sprintProjectWordCountLayout = view?.findViewById(R.id.sprint_project_word_count_layout)
        sprintProjectWordCount = view?.findViewById(R.id.sprint_project_word_count)
    }

    private fun setTimer() {
        timer = object : CountDownTimer(1200000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val formattedSecondsLeft = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
                    ),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                    )
                )
                sprintTimeText!!.text = formattedSecondsLeft
            }

            override fun onFinish() {
                sprint_end_button.isEnabled = false
                sprint_end_button.visibility = View.GONE

                sprint_start_button.isEnabled = true
                sprint_start_button.visibility = View.VISIBLE

                sprintTimeText!!.text = "00:00"

                sprintProjectTitleLayout!!.visibility = View.VISIBLE
                sprintProjectWordCountLayout!!.visibility = View.VISIBLE
                sprint_update_word_count_button.visibility = View.VISIBLE
            }
        }
        timer!!.start()
    }

    private fun updateWordCount() {
        val okHttpClient = OkHttpClient()

        val requestBody = FormBody.Builder()
            .add("user_uid", requireActivity().intent.extras!!.get("USER_UID").toString())
            .add("project_title", sprintProjectTitle!!.text.toString())
            .add("project_word_count", sprintProjectWordCount!!.text.toString())
            .build()

        val httpRequest = Request.Builder().url(Constants.serverURL + "/updateWordCount")
            .post(requestBody)
            .build()

        okHttpClient.newCall(httpRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Could not connect to server!", Toast.LENGTH_LONG)
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Word count updated!", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}