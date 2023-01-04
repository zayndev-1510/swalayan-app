package com.it015.selfcashier

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    private lateinit var btn_login_page:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true



        btn_login_page=findViewById(R.id.btn_login_page)
        btn_login_page.setOnClickListener {
            startActivity(Intent(this,LoginPage::class.java))
            finish()
        }

    }
}