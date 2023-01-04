package com.it015.selfcashier.user

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.it015.selfcashier.LoginPage
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.InterfaceDashboard
import com.it015.selfcashier.adapter.TransaksiAdapter
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import com.it015.selfcashier.model.TransaksiModel
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.text.DecimalFormat
import java.util.*

class DashboardPage:AppCompatActivity() {
    private lateinit var list: MutableList<TransaksiModel>
    private lateinit var transaksiAdapter: TransaksiAdapter
    private lateinit var recycler_transaksi:RecyclerView
    private lateinit var btn_scanner_page:ImageView
    private lateinit var txt_tgl:TextView
    private lateinit var txt_total_month:TextView
    private lateinit var txt_jumlah_total_produk:TextView
    private lateinit var txt_nama_dashboard: TextView
    private lateinit var txt_waktu_dashboard: TextView
    private lateinit var relatif_tranasksi:RelativeLayout
    private lateinit var btn_akun: TextView
    private var date: Int? =null
    private var API_DATA_TRANSAKSI_BY_MONTH=""
    private lateinit var sharedPreferences:SharedPreferences
    val nama_bulan= listOf(
        "Januari","Februai","Maret","April","Mei","Juni","Juli",
        "Agustus","September","Oktober","November","Desember"
    )

    var id_pengguna=0
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_page)

        recycler_transaksi=findViewById(R.id.recycler_transaksi)
        relatif_tranasksi=findViewById(R.id.relatif_transaksi)
        btn_scanner_page=findViewById(R.id.btn_scanner_page)
        txt_nama_dashboard=findViewById(R.id.txt_nama_dashboard)
        txt_waktu_dashboard=findViewById(R.id.txt_waktu_dashboard)

        btn_akun=findViewById(R.id.btn_akun)
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE)
        if(sharedPreferences.contains("id_pengguna")){
            id_pengguna=sharedPreferences.getInt("id_pengguna",0)
        }else{
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }
        recycler_transaksi.hasFixedSize()
        recycler_transaksi.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        initComponent()
        initData()

    }
    private fun initComponent(){

        txt_tgl=findViewById(R.id.txt_tgl)
        txt_total_month=findViewById(R.id.total_month)
        txt_jumlah_total_produk=findViewById(R.id.txt_jumlah_total_produk)

        val c= Calendar.getInstance()

        val year=c.get(Calendar.YEAR)
        val month=c.get(Calendar.MONTH)

        date=c.get(Calendar.DATE)

        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                c.set(Calendar.YEAR, year)
                c.set(Calendar.MONTH, monthOfYear)
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateTgl(c)
            }
        }
        txt_tgl.setOnClickListener {
                DatePickerDialog(this@DashboardPage,dateSetListener,
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show()
        }

        val edit=sharedPreferences.edit()
        edit.remove("status_keranjang")
        edit.apply()
        API_DATA_TRANSAKSI_BY_MONTH= getString(R.string.IP_REST_API)+"/user/data_transaksi_user_month/$year/${month+1}/$id_pengguna"
        val format_tgl="$date ${nama_bulan[month]} $year"
        txt_tgl.text=format_tgl

        btn_scanner_page.setOnClickListener{
            startActivity(Intent(this, KeranjangPage::class.java))
            finish()
        }

        btn_akun.setOnClickListener {
            startActivity(Intent(this, ProfilPage::class.java))
            finish()
        }

    }

    private fun updateTgl(c: Calendar) {
        val tahun=c.get(Calendar.YEAR)
        val bulan=c.get(Calendar.MONTH)
        val date=c.get(Calendar.DATE)
        val format_tgl="$date ${nama_bulan[bulan]} $tahun"
        txt_tgl.text=format_tgl
        API_DATA_TRANSAKSI_BY_MONTH= getString(R.string.IP_REST_API)+"/user/data_transaksi_user_month/$tahun/${bulan+1}/$id_pengguna"
        requestDataTransaksi()
    }

    private fun initData() {
        list= arrayListOf()
        requestDataTransaksi()
        requestProfil()
    }
    private fun requestProfil() {
        val url=getString(R.string.IP_REST_API)+"/user/akun_pengguna/$id_pengguna"
        RequestApi().Getting(this,url,object:CallbackRes{
            override fun oncallbackSuccess(result: String) {
                val json=JSONObject(result)
                val count=json.getInt("count")
                if(count>0){
                    val res=JSONObject(json.getString("data").toString())
                    val nama=res.get("nama")
                    val jam=Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    txt_nama_dashboard.text=nama.toString()
                    var pesan_waktu=""
                    if(jam in 1..9){
                        pesan_waktu="Selamat Pagi"
                    }else if(jam in 10..15){
                        pesan_waktu="Selamat Siang"
                    }else if(jam in 16..17){
                        pesan_waktu="Selamat Sore"
                    }else if(jam in 18..24){
                        pesan_waktu="Selamat Malam"
                    }
                    txt_waktu_dashboard.text=pesan_waktu
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


    private fun requestDataTransaksi() {
        var total=0.0
        var jumlah_total_produk=0
        list.clear()
        RequestApi().Getting(this,API_DATA_TRANSAKSI_BY_MONTH,object:CallbackRes{
            @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
            override fun oncallbackSuccess(result: String) {
                val count=JSONObject(result).getInt("count")
                if(count>0){
                    val getData=JSONObject(result).getString("data")
                    jumlah_total_produk=JSONObject(result).getInt("jumlah_total")
                    val jsonArray=JSONArray(getData)
                    (0 until jsonArray.length()).forEach({
                        val objek=jsonArray.getJSONObject(it)
                        total=total+objek.getDouble("total")
                        val transaksiModel=TransaksiModel(
                            objek.getString("nomor_transaksi"),
                            objek.getString("tgl"),
                            objek.getDouble("total"),
                            objek.getString("month"),
                            objek.getString("tahun"),
                            objek.getString("date"),
                            objek.getJSONArray("produk")
                        )
                        list.add(transaksiModel)
                    })
                }
                else{
                    list.clear()
                }
                val df = DecimalFormat("#,###,###")
                val format_rupiah="Rp. ${df.format(total)}"
                if(list.size>0){
                    transaksiAdapter= TransaksiAdapter(list,object:InterfaceDashboard{
                        override fun gotoDetailTransaksi(transaksiModel: TransaksiModel) {
                            val list_json= mapOf(
                                "total" to transaksiModel.total,"jumlah_total" to jumlah_total_produk,
                                "date" to transaksiModel.date,"month" to transaksiModel.month,
                                "year" to transaksiModel.year,"nomor_transaksi" to transaksiModel.nomor_transaksi,
                                "produk" to transaksiModel.produk
                            )
                            val jsonObject=JSONObject()
                            jsonObject.put("data",list_json)
                            val intent=Intent(applicationContext, DetailTransaksi::class.java)
                            intent.putExtra("detail_transaksi",jsonObject.toString())
                            startActivity(intent)
                            finish()
                        }
                    })
                    recycler_transaksi.visibility= View.VISIBLE
                    relatif_tranasksi.visibility=View.GONE
                    recycler_transaksi.adapter=transaksiAdapter
                    transaksiAdapter.notifyDataSetChanged()
                }
                else{
                    recycler_transaksi.visibility= View.GONE
                    relatif_tranasksi.visibility=View.VISIBLE
                }
                txt_total_month.text=format_rupiah
                txt_jumlah_total_produk.text= "$jumlah_total_produk Produk"

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
}