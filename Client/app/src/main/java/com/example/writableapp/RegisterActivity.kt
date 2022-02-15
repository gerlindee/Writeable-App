package com.example.writableapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    // Web view for the Terms and Conditions
    private var webView: WebView?= null

    // User data from the screen
    private var password: String? = null
    private var repeatedPassword: String? = null
    private var email: String? = null
    private var displayName: String? = null
    private var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Create instance of the web view
        webView = WebView(this)

        // Set the Terms and Conditions part of the string to an URL
        val termsAndConditionsMessage = "I have read and therefore agree with the " + "<u>" + "Terms and Conditions" + "</u>" + "."
        text_terms_service.text = HtmlCompat.fromHtml(termsAndConditionsMessage, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // The register button is only enabled when the user has checked the Terms and Conditions box s
        checkbox_terms_service.setOnCheckedChangeListener { _, isChecked ->
            register_button.isEnabled = isChecked
        }

        register_button.setOnClickListener {
            // TODO
        }

        text_terms_service.setOnClickListener {
            loadTermsAndConditions()
        }

        register_avatar_civ.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 0)
        }

        register_avatar_remove.setOnClickListener {
            register_avatar_remove.visibility = View.GONE
            selectedPhotoUri = null
            register_avatar_civ.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.default_icon, null))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            register_avatar_remove.visibility = View.VISIBLE
            setSelectedAvatar(data)
        }
    }

    @SuppressLint("NewApi")
    private fun setSelectedAvatar(data: Intent) {
        selectedPhotoUri = data.data
        val source = ImageDecoder.createSource(this.contentResolver, selectedPhotoUri!!)
        val bitmap = ImageDecoder.decodeBitmap(source)
        register_avatar_civ.setImageBitmap(bitmap)
    }

    // Open the web view with the Terms and Conditions
    private fun loadTermsAndConditions() {
        setContentView(webView)
        webView?.loadUrl("https://www.websitepolicies.com/policies/view/FVj4pExJ")
    }
}