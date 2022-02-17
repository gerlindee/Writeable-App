package com.example.writableapp.Utils

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ImageLoaderUtils(private var context: Context) {

    private val imageLoader: ImageLoader = ImageLoader.getInstance()

    fun loadImageIntoCircleView(url: String, circleImageView: CircleImageView) {
        Picasso.with(context).load(url).into(circleImageView)
    }

    fun loadImage(url: String, imageView: ImageView) {
        loadImageWithPicasso(url, imageView)
    }

    private fun loadImageWithPicasso(url: String, imageView: ImageView) {
        Picasso.with(context).load(url).into(imageView)
    }

    private fun loadImageWithUIL(url: String, imageView: ImageView) {
        val config = ImageLoaderConfiguration.Builder(context).build()
        imageLoader.init(config)

        imageLoader.loadImage(url, object : ImageLoadingListener {
            override fun onLoadingStarted(imageUri: String?, view: View?) {}

            override fun onLoadingFailed(imageUri: String?, view: View?, failReason: FailReason?) {}

            override fun onLoadingComplete(imageUri: String?, view: View?, loadedImage: Bitmap?) {
                imageView.setImageBitmap(loadedImage)
            }

            override fun onLoadingCancelled(imageUri: String?, view: View?) {}
        })
    }
}