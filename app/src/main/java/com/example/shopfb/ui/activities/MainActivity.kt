package com.example.shopfb.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.shopfb.databinding.ActivityMainBinding
import com.example.shopfb.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val sharedPreferences = getSharedPreferences(
            Constants.LOGGED_IN_USERNAME,
            Context.MODE_PRIVATE
        )
        val userName: String = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        binding?.tvHello?.text = userName
        binding?.btnLogout?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent: Intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}