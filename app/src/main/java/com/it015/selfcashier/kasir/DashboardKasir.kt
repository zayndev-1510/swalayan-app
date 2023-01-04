package com.it015.selfcashier.kasir

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.VolleyError
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.KasirDashboardInterface
import com.it015.selfcashier.adapter.kasir.DashboardAdapter
import com.it015.selfcashier.api.CallbackRes
import com.it015.selfcashier.api.RequestApi
import com.it015.selfcashier.model.kasir.TransaksiKasirModel

import com.it015.selfcashier.user.DetailTransaksi
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat

class DashboardKasir:AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var txt_total_transaksi:TextView
    private lateinit var txt_nama_kasir:TextView
    private lateinit var btn_scanner:ImageView
    private lateinit var list: MutableList<TransaksiKasirModel>
    private lateinit var imageprofil:ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btn_profil:TextView

    var id_pengguna=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_kasir_page)
        window.statusBarColor= ContextCompat.getColor(this,R.color.dashboardkasir)
        initComponent()
        initEvent()


    }

    private fun initComponent() {
        btn_profil=findViewById(R.id.btn_akun)
        txt_nama_kasir=findViewById(R.id.txt_nama_kasir)
        recyclerView=findViewById(R.id.recycler_transaksi_kasir)
        relativeLayout=findViewById(R.id.relatif_transaksi_kasir)
        txt_total_transaksi=findViewById(R.id.total_transaksi_kasir)
        btn_scanner=findViewById(R.id.btn_scanner_page)
        imageprofil=findViewById(R.id.profile_image)
        recyclerView.hasFixedSize()
        sharedPreferences=getSharedPreferences("data", MODE_PRIVATE)
        id_pengguna=sharedPreferences.getInt("id_pengguna",0)
        recyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    }
    fun loadDataTransaksi(){
        list= arrayListOf()
        val url=getString(R.string.IP_REST_API)+"/kasir/dataTransaksiToday/17"

        RequestApi().Getting(this,url, object : CallbackRes {
            var total=0.0
            var jumlah_total_produk=0
            @SuppressLint("NotifyDataSetChanged")
            override fun oncallbackSuccess(result: String) {
                val jsonObject= JSONObject(result)
                val count=jsonObject.getInt("count")
                if(count>0){
                    val getData= JSONObject(result).getString("data")
                    jumlah_total_produk = JSONObject(result).getInt("jumlah_total")
                    val jsonArray= JSONArray(getData)
                    (0 until jsonArray.length()).forEach({
                        val objek=jsonArray.getJSONObject(it)
                        total=total+objek.getDouble("total")
                        val transaksiModel= TransaksiKasirModel(
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

                    val dashboardAdapter=DashboardAdapter(list,object: KasirDashboardInterface {
                        override fun gototDetailTransaksiKasir(transaksiKasirModel: TransaksiKasirModel) {
                            val list_json= mapOf(
                                "total" to transaksiKasirModel.total,"jumlah_total" to jumlah_total_produk,
                                "date" to transaksiKasirModel.date,"month" to transaksiKasirModel.month,
                                "year" to transaksiKasirModel.year,"nomor_transaksi" to transaksiKasirModel.nomor_transaksi,
                                "produk" to transaksiKasirModel.produk
                            )
                            val jsondata= JSONObject()
                            jsondata.put("data",list_json)
                            val intent= Intent(applicationContext, DetailTransaksi::class.java)
                            intent.putExtra("detail_transaksi",jsondata.toString())
                            startActivity(intent)
                            DashboardKasir().finish()
                        }
                    })
                    recyclerView.visibility= View.VISIBLE
                    relativeLayout.visibility= View.GONE
                    recyclerView.adapter=dashboardAdapter
                    dashboardAdapter.notifyDataSetChanged()
                }
                else{
                    recyclerView.visibility= View.GONE
                    relativeLayout.visibility= View.VISIBLE
                }
                txt_total_transaksi.text=format_rupiah

            }

            override fun oncallbackError(volleyError: VolleyError) {
                Log.d("error",volleyError.toString())
            }

        })
    }
    fun loadProfilKasir(){
        list= arrayListOf()
        val url=getString(R.string.IP_REST_API)+"/kasir/profilkasir"
        val map= mapOf("id_pengguna" to id_pengguna.toString())
        RequestApi().PostingText(this,map,url,object : CallbackRes {
            @SuppressLint("NotifyDataSetChanged")
            override fun oncallbackSuccess(result: String) {
                val jsonObject= JSONObject(result)
                val count=jsonObject.getInt("val")
                if(count>0){
                    val data=jsonObject.get("data")
                    val row=JSONObject(data.toString())
                    txt_nama_kasir.text=row.getString("nama_kasir")
                    val url=getString(R.string.URL_IMAGE)+"akun/"+row.getString("foto")
                    Picasso.get().load(url).into(imageprofil)
                }
            }

            override fun oncallbackError(volleyError: VolleyError) {
                Log.d("error",volleyError.toString())
            }

        })
    }
    private fun initEvent(){

        loadDataTransaksi()
        loadProfilKasir()

        btn_scanner.setOnClickListener({
            startActivity(Intent(this,TransaksiPage::class.java))
            finish()
        })

        btn_profil.setOnClickListener({
            startActivity(Intent(this,ProfilPageKasir::class.java))
            finish()
        })

    }
}