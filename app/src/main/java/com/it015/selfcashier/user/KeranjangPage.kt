package com.it015.selfcashier.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.budiyev.android.codescanner.*
import com.it015.selfcashier.LoginPage
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.InterfaceKeranjang
import com.it015.selfcashier.adapter.KeranjangAdapter
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import com.it015.selfcashier.model.KeranjangModel
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

const val PERMISSION_CODE_CAMERA=200

class KeranjangPage:AppCompatActivity() {
    private lateinit var recyclerkeranjang:RecyclerView
    private lateinit var totalharga:TextView
    private lateinit var totalproduk:TextView
    private lateinit var tglkeranjang:TextView
    private lateinit var nomorkeranjang:TextView
    private lateinit var list: MutableList<KeranjangModel>
    private lateinit var keranjangAdapter: KeranjangAdapter
    private lateinit var btnScanner:ImageView
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var linear_bottom:LinearLayout
    private lateinit var caption_empty_data:TextView
    private lateinit var btn_proses_pembayaran:TextView
    private lateinit var btn_batal:TextView
    private lateinit var sharedPreferences: SharedPreferences
    var API_GET_DATA_KERANJANG_TODAY=""
    var API_CEK_BARANG_USER=""
    var API_CEK_KERANJANG_USER=""
    var API_ADD_KERANJANG_USER=""
    var API_UPDATE_KERANJANG_USER=""
    var total_keseluruhan=0
    var id_pengguna=0
    var IP_REST_API=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keranjang_page)
        IP_REST_API=getString(R.string.IP_REST_API)
        window.statusBarColor= ContextCompat.getColor(this,R.color.purple_500)
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE)

        if(sharedPreferences.contains("id_pengguna")){
            id_pengguna=sharedPreferences.getInt("id_pengguna",0)
        }else{
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }

        API_GET_DATA_KERANJANG_TODAY=IP_REST_API+"/user/data_transaksi_user_today"
        API_CEK_BARANG_USER=IP_REST_API+"/user/cek_barang_user/"
        API_ADD_KERANJANG_USER=IP_REST_API+"/user/add_keranjang_user"
        API_UPDATE_KERANJANG_USER=IP_REST_API+"/user/update_keranjang_user"

        setUpPermisionCamera()
        initComponent()
        initEvent()
    }

    private fun setUpPermisionCamera() {
        val permission:Int=ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if(permission !=PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
            PERMISSION_CODE_CAMERA
        )
    }

    private fun initComponent(){
        caption_empty_data=findViewById(R.id.caption_empty_data)
        linearLayout=findViewById(R.id.container_detail_keranjang)
        linear_bottom=findViewById(R.id.menu_bottom)
        recyclerkeranjang=findViewById(R.id.recyler_keranjang)
        totalharga=findViewById(R.id.row_total_keranjang)
        totalproduk=findViewById(R.id.row_produk_keranjang)
        tglkeranjang=findViewById(R.id.row_tgl_keranjang)
        nomorkeranjang=findViewById(R.id.row_nomor_keranjang)
        btnScanner=findViewById(R.id.btn_scanner)
        scannerView=findViewById(R.id.scanner_view)
        btn_proses_pembayaran=findViewById(R.id.btn_proses_pembayaran)
        btn_batal=findViewById(R.id.btn_batal)
        list= arrayListOf()
        recyclerkeranjang.hasFixedSize()
        recyclerkeranjang.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        codeScanner=CodeScanner(this,scannerView)
    }
    private fun initData() {

        API_CEK_KERANJANG_USER=IP_REST_API+"/user/data_keranjang_user/1/$id_pengguna/${nomorkeranjang.text}"
        Log.d("data",API_UPDATE_KERANJANG_USER)
        RequestApi().Getting(this,API_CEK_KERANJANG_USER,object:CallbackRes{
            @SuppressLint("SetTextI18n")
            override fun oncallbackSuccess(result: String) {
                val jsonData=JSONObject(result)
                val dataArray=JSONArray(jsonData.getString("data").toString())
                if(dataArray.length()>0){
                    (0 until dataArray.length()).forEach {
                        val jsonObject = dataArray.getJSONObject(it)
                        addDataToList(jsonObject,jsonData)
                    }
                    recyclerkeranjang.visibility=View.VISIBLE
                    caption_empty_data.visibility=View.GONE
                }
                else{
                    totalproduk.text= "${0.toString()} Jumlah"
                    totalharga.text="Rp ${0.toString()}"
                    recyclerkeranjang.visibility=View.GONE
                    caption_empty_data.visibility=View.VISIBLE
                }

            }

            override fun oncallbackError(volleyError: VolleyError) {
                var errorMsg = ""
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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun initEvent(){

        val generate= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getRandomString(10).uppercase()
        } else {
            Toast.makeText(applicationContext,"Your Android Not Requipment",Toast.LENGTH_LONG).show()
        }
        val c=Calendar.getInstance()
        val year=c.get(Calendar.YEAR)
        val month=c.get(Calendar.MONTH)
        if(!sharedPreferences.contains("status_keranjang")){
            openScanner()
        }

        nomorkeranjang.text= "$generate$year${month+1}"

        //load data keranjang user

        btnScanner.setOnClickListener {
            scannerView.visibility=View.VISIBLE
            linearLayout.visibility=View.GONE
            linear_bottom.visibility=View.GONE
            openScanner()
        }

        btn_proses_pembayaran.setOnClickListener {
            requestUpdateKeranjang()
        }

        btn_batal.setOnClickListener {
            val intent=Intent(this, DashboardPage::class.java)
            startActivity(intent)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun requestUpdateKeranjang() {

        val dialog= Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)
        val textView=dialog.findViewById<TextView>(R.id.ket_loading)
        textView.text="Sedang Memproses..."
        dialog.show()

        val jsonArray=JSONArray()
        (0 until list.size).forEach{
            val jsonObject=JSONObject()
            jsonObject.put("jumlah",list[it].jumlah.toString())
            jsonObject.put("nomor_keranjang",list[it].nomor_keranjang)
            jsonObject.put("id_barang",list[it].id_barang)
            jsonObject.put("jumlah",list[it].jumlah)
            jsonObject.put("harga",list[it].harga)
            jsonObject.put("tgl",list[it].tgl)
            jsonObject.put("id_pengguna",list[it].id_pengguna)
            jsonArray.put(jsonObject)
        }
        val nomor_transaksi= if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getRandomString(20)
        } else {
            TODO("VERSION.SDK_INT < N")
        }
        val map=mapOf("data" to jsonArray.toString(),
            "total" to total_keseluruhan.toString(),
            "nomor_transaksi" to nomor_transaksi.uppercase())
        RequestApi().PostingText(this,map,API_UPDATE_KERANJANG_USER,object:CallbackRes{
            @RequiresApi(Build.VERSION_CODES.P)
            override fun oncallbackSuccess(result: String) {
                val res=JSONObject(result).getInt("count")
                Handler.createAsync(Looper.getMainLooper()).postDelayed({
                    if(res>0){
                        val intent=Intent(applicationContext, ProsesPembaayran::class.java)
                        intent.putExtra("nomor_transaksi",nomor_transaksi)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"Update keranjang gagal",Toast.LENGTH_LONG).show()
                    }
                },3000)
            }

            override fun oncallbackError(volleyError: VolleyError) {
                var errorMsg = ""
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


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            PERMISSION_CODE_CAMERA ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You Need to Permisiion camera access Barcode Scanner",Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    // Turunan Fungsi Dari Beberapa Event


    @RequiresApi(Build.VERSION_CODES.N)
    private fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
    private fun openScanner() {

        scannerView.visibility=View.VISIBLE
        linearLayout.visibility=View.GONE
        linear_bottom.visibility=View.GONE
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                scannerView.visibility=View.GONE
                linearLayout.visibility=View.VISIBLE
                linear_bottom.visibility=View.VISIBLE
                val res=it.text
                requestCheckBarang(res)
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {

            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun addprodukKeranjang(jsonObject: JSONObject) {
        val nomorkeranjang_result=nomorkeranjang.text.toString()
        val idbarang=jsonObject.getInt("id")
        val jumlah=1
        val harga=jsonObject.getDouble("harga_jual")
        val map=mapOf(
            "nomor_keranjang" to nomorkeranjang_result,"id_barang" to idbarang.toString(),
            "harga" to harga.toString(),"jumlah" to jumlah.toString(),"id_pengguna" to id_pengguna.toString()
        )

        RequestApi().PostingText(this, map,API_ADD_KERANJANG_USER,object:CallbackRes{
            @SuppressLint("NotifyDataSetChanged")
            override fun oncallbackSuccess(result: String) {
                val res=JSONObject(result).getInt("count")
                if(res>0){
                    Toast.makeText(applicationContext,"produk berhasil ditambahkan kelist keranjang",Toast.LENGTH_LONG).show()
                    val edit=sharedPreferences.edit()
                    edit.putInt("status_keranjang",1)
                    edit.apply()
                    val jsondata=JSONObject(result).getString("data")
                    val df = DecimalFormat("#,###,###")
                    val row=JSONObject(jsondata)
                    list.add(KeranjangModel(row.getInt("id_keranjang"),id_pengguna,idbarang,
                        jumlah,harga,nomorkeranjang_result,row.getString("tgl"),row.getInt("status"),row.getString("nama_barang") ))
                    keranjangAdapter= KeranjangAdapter(list,object:InterfaceKeranjang{
                        @SuppressLint("SetTextI18n")
                        override fun requestTambah(keranjangModel: MutableList<KeranjangModel>) {
                            var total_new=0.0
                            (0 until keranjangModel.size).forEach(){
                                total_new=total_new+(keranjangModel[it].jumlah*keranjangModel[it].harga)
                            }
                            totalharga.text="Rp.${df.format(total_new)}"
                            total_keseluruhan=total_new.toInt()

                        }

                        @SuppressLint("SetTextI18n")
                        override fun requestKurang(keranjangModel: MutableList<KeranjangModel>) {
                            var total_new=0.0
                            (0 until keranjangModel.size).forEach(){
                                total_new=total_new+(keranjangModel[it].jumlah*keranjangModel[it].harga)
                            }
                            totalharga.text="Rp.${df.format(total_new)}"
                            total_keseluruhan=total_new.toInt()
                        }
                    })
                    recyclerkeranjang.adapter=keranjangAdapter
                    keranjangAdapter.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(applicationContext,"produk gagal ditambahkan kelist keranjang",Toast.LENGTH_LONG).show()
                }
            }
            override fun oncallbackError(volleyError: VolleyError) {
                var errorMsg = ""
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

    private fun requestCheckBarang(nomor_barcode: String) {
        val url=API_CEK_BARANG_USER+nomor_barcode
        RequestApi().Getting(this,url,object:CallbackRes{
            override fun oncallbackSuccess(result: String) {
                val res=JSONObject(result).getString("data")
                val array=JSONArray(res.toString())


                if(array.length()>0){
                    val jsonObject=array.getJSONObject(0)
                    addprodukKeranjang(jsonObject)
                }
            }

            override fun oncallbackError(volleyError: VolleyError) {
                var errorMsg = ""
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(volleyError is NoConnectionError){
                        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork: NetworkInfo?
                        activeNetwork = cm.activeNetworkInfo
                        errorMsg = if ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting) {
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

                }


                Toast.makeText(applicationContext,errorMsg,Toast.LENGTH_LONG).show()
            }

        })
    }


    @SuppressLint("SetTextI18n")
    private fun addDataToList(jsonObject: JSONObject, jsonData: JSONObject) {

        val keranjangModel = KeranjangModel(
            jsonObject.getInt("id_keranjang"), jsonObject.getInt("id_pengguna"),
            jsonObject.getInt("id_barang"),jsonObject.getInt("jumlah"),
            jsonObject.getDouble("harga"),jsonObject.getString("nomor_keranjang"),
            jsonObject.getString("tgl"),jsonObject.getInt("status"),jsonObject.getString("nama_barang"))
        list.add(keranjangModel)
        val df = DecimalFormat("#,###,###")
        val x="${list.size}"
        val totalhargajson=jsonData.getDouble("total_harga")
        val nomorkeranjangjson=jsonData.getString("nomor_keranjang")
        totalproduk.text=x
        totalharga.text="Rp. ${df.format(totalhargajson)}"
        nomorkeranjang.text=nomorkeranjangjson
        total_keseluruhan=totalhargajson.toInt()
        keranjangAdapter=KeranjangAdapter(list,object:InterfaceKeranjang{
            override fun requestTambah(keranjangModel: MutableList<KeranjangModel>) {
                var total_new=0.0
                (0 until keranjangModel.size).forEach(){
                   total_new=total_new+(keranjangModel[it].jumlah*keranjangModel[it].harga)
                }
                totalharga.text="Rp.${df.format(total_new)}"
                total_keseluruhan=total_new.toInt()

            }

            override fun requestKurang(keranjangModel: MutableList<KeranjangModel>) {
                var total_new=0.0
                (0 until keranjangModel.size).forEach(){
                    total_new=total_new+(keranjangModel[it].jumlah*keranjangModel[it].harga)
                }
                totalharga.text="Rp.${df.format(total_new)}"
                total_keseluruhan=total_new.toInt()
            }
        })
        recyclerkeranjang.adapter=keranjangAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(sharedPreferences.contains("status_keranjang")){
            startActivity(Intent(this, KeranjangPage::class.java))
            finish()
        }else{
            startActivity(Intent(this, DashboardPage::class.java))
            finish()
        }

    }
}