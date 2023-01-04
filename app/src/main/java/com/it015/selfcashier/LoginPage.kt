package com.it015.selfcashier

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.google.android.material.textfield.TextInputEditText
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import com.it015.selfcashier.kasir.DashboardKasir
import com.it015.selfcashier.user.DashboardPage
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import kotlin.math.log

const val CODE_PERMISION=101
class LoginPage:AppCompatActivity() {
    private lateinit var btnmasuk:TextView
    private lateinit var btndaftar:TextView
    private lateinit var editusername:TextInputEditText
    private lateinit var editsandi:TextInputEditText
    private lateinit var sharePreferences:SharedPreferences
    var pesan =""
    var URL_API=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        URL_API=getString(R.string.IP_REST_API)
        sharePreferences=getSharedPreferences("data", MODE_PRIVATE)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true



        initComponent()
        eventListener()
    }

    // intalasi component
    private fun initComponent() {
        btnmasuk=findViewById(R.id.btn_masuk)
        btndaftar=findViewById(R.id.btn_daftar_page)
        editusername=findViewById(R.id.username_edit)
        editsandi=findViewById(R.id.sandi_edit)
    }

    private fun checkValidasi():Boolean{
        if(editusername.text.toString().length==0){
            pesan="Masukan Nama Pengguna Masih Kosong"
            return true
        }
        else if(editsandi.text.toString().length==0){
            pesan="Masukan Kata Sandi Yang Masih Kosong"
            return true
        }
        else{
            return false
        }
    }

    private fun requestLoginAkun() {
            val username=editusername.text.toString()
            val sandi=editsandi.text.toString()
            val map=mapOf(
                "username" to username,"sandi" to sandi
            )
            val url=URL_API+"/user/login_akun"

            val dialog=Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.show()
            RequestApi().PostingText(this,map,url,object :CallbackRes{
                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.P)
                override fun oncallbackSuccess(result: String) {
                  val jsonObject=JSONObject(result)
                  val count=jsonObject.getInt("count")
                    val ket_loading=dialog.findViewById<TextView>(R.id.ket_loading)
                    val animation=dialog.findViewById<LottieAnimationView>(R.id.lottie_loading)
                    Handler.createAsync(Looper.getMainLooper()).postDelayed({
                        if(count>0){
                            animation.setAnimation(R.raw.success)
                            animation.playAnimation()
                            animation.loop(false)
                            ket_loading.text="Login Akun Berhasil"
                            val id_pengguna=jsonObject.getString("id_pengguna").substring(0,2)
                            val edit=sharePreferences.edit()
                            edit.putInt("id_pengguna",id_pengguna.toInt())

                            val lvl=jsonObject.getInt("lvl")
                            edit.putInt("lvl",lvl)
                            edit.apply()
                            if(lvl==1){
                                Handler.createAsync(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(applicationContext, DashboardKasir::class.java))
                                    finish()
                                },2000)
                            }else if(lvl==2){
                                Handler.createAsync(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(applicationContext, DashboardPage::class.java))
                                    finish()
                                },2000)

                            }
                        }
                        else{
                            animation.setAnimation(R.raw.failed)
                            animation.playAnimation()
                            animation.loop(false)
                            ket_loading.text="Login Akun Gagal"
                        }
                    },3000)
                }

                override fun oncallbackError(volleyError: VolleyError) {
                    var errorMsg=""
                    if(volleyError is NoConnectionError){
                        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo?
                        activeNetwork = cm.activeNetworkInfo
                        errorMsg = if (activeNetwork != null && activeNetwork.isConnectedOrConnecting) {
                            "Server is not connected to the internet. Please try again"
                        } else {
                            "Your device is not connected to internet.please try again with active internet connection"
                        }
                    }else if (volleyError is NetworkError || volleyError.cause is ConnectException) {
                        errorMsg = "Your device is not connected to internet.please try again with active internet connection"
                    } else if (volleyError.cause is MalformedURLException) {
                        errorMsg = "That was a bad request please try again…"
                    } else if (volleyError is ParseError || volleyError.cause is IllegalStateException || volleyError.cause is JSONException || volleyError.cause is XmlPullParserException) {
                        errorMsg = "There was an error parsing data…"
                    } else if (volleyError.cause is OutOfMemoryError) {
                        errorMsg = "Device out of memory"
                    } else if (volleyError is AuthFailureError) {
                        errorMsg = "Failed to authenticate user at the server, please contact support"
                    } else if (volleyError is ServerError || volleyError.cause is ServerError) {
                        errorMsg = "Internal server error occurred please try again...."
                    } else if (volleyError is TimeoutError || volleyError.cause is SocketTimeoutException || volleyError.cause is ConnectTimeoutException || volleyError.cause is SocketException || (volleyError.cause!!.message != null && volleyError.cause!!.message!!.contains(
                            "Your connection has timed out, please try again"
                        ))
                    ) {
                        errorMsg = "Your connection has timed out, please try again"
                    }
                    Toast.makeText(applicationContext,errorMsg,Toast.LENGTH_LONG).show()
                }

            })
    }

    // instalasi kejadian di semua objek
    private fun eventListener(){
        btnmasuk.setOnClickListener{
            if(checkValidasi()){
                Toast.makeText(this,pesan,Toast.LENGTH_LONG).show()
            }else{
                requestLoginAkun()
            }
        }

        btndaftar.setOnClickListener {
            startActivity(Intent(this,DaftarAkun::class.java))
            finish()
        }
    }


}