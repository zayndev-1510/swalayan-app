package com.it015.selfcashier.kasir

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.it015.selfcashier.R
import com.it015.selfcashier.adapter.kasir.TransaksiKasirAdapter
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import com.it015.selfcashier.model.DetailTransaksiModel
import com.it015.selfcashier.user.PERMISSION_CODE_CAMERA
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParserException
import java.net.ConnectException
import java.net.MalformedURLException
import java.net.SocketException
import java.net.SocketTimeoutException

class TransaksiPage:AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var linear_bottom: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var nomorkeranjangtxt:TextView
    private lateinit var tgltxt:TextView
    private lateinit var jumlaproduktxt:TextView

    private lateinit var rcy_detail_transaksi:RecyclerView

    private lateinit var list: MutableList<DetailTransaksiModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.transaksi_kasir_page)
        window.statusBarColor= ContextCompat.getColor(this,R.color.transaksikasir)

        initComponent()
        initEvent()
        loadDataTransaksi("6RYRX7L5ESLP7IJHE9B0")

    }

    private fun initComponent() {
        scannerView=findViewById(R.id.scanner_view)
        nomorkeranjangtxt=findViewById(R.id.row_nomor_keranjang)
        tgltxt=findViewById(R.id.row_tgl_transaksi)
        jumlaproduktxt=findViewById(R.id.row_totalproduk)
        codeScanner=CodeScanner(this,scannerView)
        linearLayout=findViewById(R.id.container_detail_keranjang)
        linear_bottom=findViewById(R.id.menu_bottom)
        recyclerView=findViewById(R.id.recyler_keranjang)
        recyclerView.hasFixedSize()
        recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

    }

    private fun initEvent() {
        setUpPermisionCamera()
    }

    private fun setUpPermisionCamera() {
        val permission:Int= ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
            PERMISSION_CODE_CAMERA
        )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            PERMISSION_CODE_CAMERA ->{
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"You Need to Permisiion camera access Barcode Scanner",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun loadDataTransaksi(nomor_transaki:String){
        list= arrayListOf()
        val url=getString(R.string.IP_REST_API)+"/kasir/dataTransaksiUser/"+nomor_transaki
        RequestApi().Getting(this,url, object : CallbackRes {
            @SuppressLint("NotifyDataSetChanged")
            override fun oncallbackSuccess(result: String) {
                val jsonObject=JSONObject(result)
                val count=jsonObject.getInt("count")
                if(count>0){

                    val data=jsonObject.getString("data").toString()
                    val res=JSONObject(data)
                    val nomor_keranjang=res.getString("nomor_keranjang")
                    val tgl=res.getString("format")
                    val jumlahproduk=jsonObject.getInt("jumlah_total").toString()+" Jumlah"
                    jumlaproduktxt.text=jumlahproduk
                    nomorkeranjangtxt.text=nomor_keranjang
                    tgltxt.text=tgl

                    val array=JSONArray(res.getString("produk"))
                    (0 until array.length()).forEach(){
                        val row=array.getJSONObject(it)
                        val total_produk=row.getDouble("jumlah")*row.getDouble("harga")
                        val detailTransaksiModel= DetailTransaksiModel(
                            row.getString("nama_barang"),row.getInt("jumlah"),
                            row.getDouble("harga"),total_produk
                        )
                        list.add(detailTransaksiModel)
                    }
                    val detailTransaksiKasirAdapter=TransaksiKasirAdapter(list)
                    recyclerView.adapter=detailTransaksiKasirAdapter
                    detailTransaksiKasirAdapter.notifyDataSetChanged()
                }
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


    private fun openScanner() {

        scannerView.visibility= View.VISIBLE
        linearLayout.visibility= View.GONE
        linear_bottom.visibility= View.GONE
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
                scannerView.visibility= View.GONE
                linearLayout.visibility= View.VISIBLE
                linear_bottom.visibility= View.VISIBLE
                val res=it.text

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

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }


    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


}