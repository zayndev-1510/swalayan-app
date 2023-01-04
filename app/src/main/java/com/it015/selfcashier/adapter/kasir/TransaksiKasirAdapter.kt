package com.it015.selfcashier.adapter.kasir

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.holder.kasir.DetailTransaksiKasirHolder
import com.it015.selfcashier.model.DetailTransaksiModel
import com.it015.selfcashier.model.kasir.TransaksiKasirModel
import java.text.DecimalFormat

class TransaksiKasirAdapter (var list: MutableList<DetailTransaksiModel>):
    RecyclerView.Adapter<DetailTransaksiKasirHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTransaksiKasirHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.result_detail_transaksi,parent,false)
        return DetailTransaksiKasirHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailTransaksiKasirHolder, position: Int) {
        val produk=list.get(position)
        val df= DecimalFormat("#,###,###")
        holder.row_detail_transaksi_produk.text=produk.produk
        holder.row_detail_transaksi_harga.text="${produk.jumlah} * Rp. ${df.format(produk.harga)}"
        holder.row_detail_transaksi_total.text="Rp. ${df.format(/* number = */ produk.total)}"
    }

    override fun getItemCount(): Int {
       return list.size
    }
}