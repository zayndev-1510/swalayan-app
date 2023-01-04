package com.it015.selfcashier

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException

class DaftarAkun:AppCompatActivity() {
    private lateinit var editnama:EditText
    private lateinit var editalamat:EditText
    private lateinit var edituser:EditText
    private lateinit var editsandi:EditText
    private lateinit var btn_daftar:TextView
    private lateinit var editnomorhp:EditText
    private lateinit var editemail:EditText
    var API_DAFTAR_AKUN_USER=""

    var pesan=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_page)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
        API_DAFTAR_AKUN_USER=getString(R.string.IP_REST_API)+"/user/cek_akun/"
        initComponent()
        initEvent()
    }

    private fun initComponent() {
        editnama=findViewById(R.id.nama_edit)
        editalamat=findViewById(R.id.alamat_edit)
        edituser=findViewById(R.id.username_edit)
        editsandi=findViewById(R.id.sandi_edit)
        editnomorhp=findViewById(R.id.nomor_hp_edit)
        editemail=findViewById(R.id.email_edit)
        btn_daftar=findViewById(R.id.btn_daftar)
    }

    private fun initEvent() {
        btn_daftar.setOnClickListener {
            if(checkValidation()){
                Toast.makeText(this,pesan,Toast.LENGTH_LONG).show()
            }else{
                requestDaftarAkun()
            }
        }
    }

    private fun requestDaftarAkun() {
        val nama_lengkap=editnama.text.toString()
        val alamat_rumah=editalamat.text.toString()
        val username=edituser.text.toString()
        val sandi=editsandi.text.toString()
        val nomor_hp=editnomorhp.text.toString()
        val email=editemail.text.toString()
        val map=mapOf(
            "nama" to nama_lengkap,"alamat" to alamat_rumah,
            "username" to username,"sandi" to sandi,
            "nomor_hp" to nomor_hp,"email" to email,"foto" to "nofoto.png"
        )
        val dialog=Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        RequestApi().PostingText(this,map,API_DAFTAR_AKUN_USER,object :CallbackRes{
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
                        ket_loading.text="Pendaftaran Akun Berhasil"
                        Handler.createAsync(Looper.getMainLooper()).postDelayed({
                            startActivity(Intent(applicationContext,LoginPage::class.java))
                            finish()
                        },2000)
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
                var errorMsg = ""
                if(volleyError is NoConnectionError){
                    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    var activeNetwork: NetworkInfo? = null
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

    private fun checkValidation():Boolean{
        if(editnama.text.toString().length==0) {
            pesan="masukan nama lengkap yang kosong"
            return true
        }else if(editalamat.text.toString().length==0){
            pesan="masukan alamat rumah yang kosong"
            return true
        }else if(edituser.text.toString().length==0){
            pesan="masukan nama pengguna yang kosong"
            return true
        }else if(editsandi.text.toString().length==0){
            pesan="masukan sandi yang kosong"
            return true
        }else if(editnomorhp.text.length==0){
            pesan="masukan nomor handphone yang kosong"
            return true
        }
        else if(editemail.text.length==0){
            pesan="masukan alamat email yang kosong"
            return true
        }else{
            return false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,LoginPage::class.java))
        finish()
    }
}