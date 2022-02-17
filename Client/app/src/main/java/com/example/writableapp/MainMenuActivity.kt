package com.example.writableapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.viewpager2.widget.ViewPager2
import com.example.writableapp.Adapters.MainMenuViewPagerAdapter
import com.example.writableapp.Utils.ImageLoaderUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {

    private val tabTitles = ArrayList<Int>()

    private var menuViewPager: ViewPager2 ?= null

    private var profilePictureImageView: CircleImageView ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        setupActivity()
    }

    private fun setupActivity() {
        profilePictureImageView = findViewById(R.id.avatar_display)

        setTitlesForTabs()

        setUserAvatar(intent.extras!!["USER_AVATAR_URL"].toString())

        menuViewPager = findViewById(R.id.main_menu_viewpager)
        menuViewPager!!.adapter = MainMenuViewPagerAdapter(this)

        val menuTabLayout = findViewById<TabLayout>(R.id.main_menu_tabs_layout)
        TabLayoutMediator(menuTabLayout, menuViewPager!!) { tab, position ->
            tab.icon = AppCompatResources.getDrawable(this, tabTitles[position])
            menuViewPager!!.setCurrentItem(tab.position, true)
        }.attach()

        sign_out_button.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setTitlesForTabs() {
        tabTitles.add(R.drawable.sprint_tab)
        tabTitles.add(R.drawable.works_tab)
        tabTitles.add(R.drawable.settings_tab)
    }

    private fun setUserAvatar(avatar_url: String) {
        ImageLoaderUtils(this).loadImage(avatar_url, profilePictureImageView!!)
    }

    override fun onBackPressed() {
        // this is empty so that it does not by default go back to LoginActivity while still connected to a session
    }
}