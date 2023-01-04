package com.it015.selfcashier.user

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.adapter.DetailTransaksiAdapter
import com.it015.selfcashier.model.DetailTransaksiModel
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat

class DetailTransaksi:AppCompatActivity() {
    private lateinit var txt_total_produk:TextView
    private lateinit var txt_nomor_transaksi:TextView
    private lateinit var txt_tgl_transaksi:TextView
    private lateinit var txt_jumlah_produk:TextView
    private lateinit var rcy_detail_transaksi:RecyclerView
    private lateinit var list:MutableList<DetailTransaksiModel>
    private lateinit var detailTransaksiAdapter: DetailTransaksiAdapter
    private lateinit var btn_back_dashboard:LinearLayout
    @SuppressLint("SetTextI18n","NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_transaksi)

        window.statusBarColor= ContextCompat.getColor(this,R.color.dashboardkasir)
        txt_total_produk=findViewById(R.id.txt_detail_transaksi_total)
        txt_nomor_transaksi=findViewById(R.id.txt_detail_nomor_transaksi)
        txt_tgl_transaksi=findViewById(R.id.txt_detail_transaksi_tgl)
        txt_jumlah_produk=findViewById(R.id.txt_detail_transaksi_jumlah_total_produk)
        rcy_detail_transaksi=findViewById(R.id.rcy_transaksi)
        btn_back_dashboard=findViewById(R.id.btn_back_dashboard)
        rcy_detail_transaksi.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rcy_detail_transaksi.hasFixedSize()

        val dataintent=intent.getStringExtra("detail_transaksi")
        val jsonObject=JSONObject(dataintent.toString()).get("data").toString()
        val jsondetail=JSONObject(jsonObject)
        val total=jsondetail.getDouble("total")
        val df=DecimalFormat("#,###,###")
        val produk=jsondetail.getString("produk").toString()

        list= arrayListOf()
        txt_total_produk.text="Rp. ${df.format(total)}"
        txt_tgl_transaksi.text="${jsondetail.get("date")} ${jsondetail.get("month")} ${jsondetail.get("year")}"
        var total_jumlah_produk=0
        val jsonArray=JSONArray(produk)
        (0 until jsonArray.length()).forEach(){
            val row=jsonArray.getJSONObject(it)
            total_jumlah_produk=total_jumlah_produk+row.getInt("jumlah")
            val total_produk=row.getDouble("jumlah")*row.getDouble("harga")
            val detailTransaksiModel=DetailTransaksiModel(
                row.getString("nama_barang"),row.getInt("jumlah"),
                row.getDouble("harga"),total_produk
            )
           list.add(detailTransaksiModel)
        }
        detailTransaksiAdapter= DetailTransaksiAdapter(list)
        rcy_detail_transaksi.adapter=detailTransaksiAdapter
        detailTransaksiAdapter.notifyDataSetChanged()
        txt_jumlah_produk.text="${total_jumlah_produk} Produk"

        btn_back_dashboard.setOnClickListener{
            startActivity(Intent(applicationContext, DashboardPage::class.java))
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(applicationContext, DashboardPage::class.java))
        finish()
    }
}