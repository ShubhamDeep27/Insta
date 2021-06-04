package com.example.insta

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.insta.fragments.HomeFragment
import com.example.insta.fragments.NotificationsFragment
import com.example.insta.fragments.ProfileFragment
import com.example.insta.fragments.SearchFragment

class MainActivity : AppCompatActivity() {

   internal var selectedFragment:Fragment?=null

    private val NavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                       movefragment(HomeFragment())
                return@OnNavigationItemSelectedListener true

            }
            R.id.nav_search -> {

                movefragment(SearchFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked=false
              startActivity(Intent(this,NewPost::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {

                movefragment(NotificationsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {

                movefragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
        }
        }


        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(NavigationItemSelectedListener)

        movefragment(HomeFragment())
    }
   private  fun movefragment(fragment: Fragment)
   {

       val fragmentTransaction=supportFragmentManager.beginTransaction()
       fragmentTransaction.replace(R.id.fragment,fragment)
       fragmentTransaction.commit()
   }
}
