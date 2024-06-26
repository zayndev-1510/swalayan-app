package com.it015.selfcashier.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.it015.selfcashier.LoginPage
import com.it015.selfcashier.MainActivity
import com.it015.selfcashier.R
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

class ProfilPage : AppCompatActivity() {

    private lateinit var btn_back:LinearLayout
    private lateinit var btn_keluar:TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var profilname: TextView
    private lateinit var profilemail: TextView
    private lateinit var profilalamat:TextView
    private lateinit var profiluser: TextView
    private lateinit var profilnomor: TextView

    var id_pengguna=0
    private var IP_ADRESS=""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil_page)


        IP_ADRESS=getString(R.string.IP_REST_API)
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE)


        if(sharedPreferences.contains("id_pengguna")){
            id_pengguna=sharedPreferences.getInt("id_pengguna",0)
        }else{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btn_back=findViewById(R.id.btn_back_dashboard)
        profilname=findViewById(R.id.profil_name)
        profilemail=findViewById(R.id.profil_email)
        profilalamat=findViewById(R.id.profil_alamat)
        profiluser=findViewById(R.id.profil_username)
        profilnomor=findViewById(R.id.profil_nomor_hp)
        btn_keluar=findViewById(R.id.btn_keluar)
        requestProfil()

        btn_back.setOnClickListener{
            startActivity(Intent(this, DashboardPage::class.java))
            finish()
        }

        btn_keluar.setOnClickListener{
            val editor=sharedPreferences.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(this,LoginPage::class.java))
            finish()
        }
    }

    private fun requestProfil() {
        val url=IP_ADRESS+"/user/akun_pengguna/$id_pengguna"
        RequestApi().Getting(this,url,object:CallbackRes{
            override fun oncallbackSuccess(result: String) {
             val json=JSONObject(result)
             val count=json.getInt("count")
             if(count>0){
                 val res=JSONObject(json.getString("data").toString())
                 val nama=res.get("nama")
                 val email=res.get("email")
                 val nomor_hp=res.get("nomor_hp")
                 val alamat=res.get("alamat")
                 val username=res.get("username")
                 profilname.text=nama.toString()
                 profilemail.text=email.toString()
                 profilnomor.text=nomor_hp.toString()
                 profilalamat.text=alamat.toString()
                 profiluser.text=username.toString()
             }
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

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardPage::class.java))
        finish()
    }
}