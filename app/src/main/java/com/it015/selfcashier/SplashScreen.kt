package com.it015.selfcashier

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.it015.selfcashier.kasir.DashboardKasir
import com.it015.selfcashier.user.DashboardPage

@SuppressLint("CustomSplashScreen")
class SplashScreen:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_screen)
        actionBar?.hide()
        val sharePreferences=getSharedPreferences("data", MODE_PRIVATE)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.getMainLooper()).postDelayed({
                if(sharePreferences.contains("id_pengguna") && sharePreferences.contains("lvl")){
                    val lvl=sharePreferences.getInt("lvl",0)
                    if(lvl==1){
                        startActivity(Intent(this, DashboardKasir::class.java))
                        finish()
                    }else if(lvl==2){
                        startActivity(Intent(this, DashboardPage::class.java))
                        finish()
                    }

                }else{
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
            },5000)
        }

    }
}