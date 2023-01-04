package com.it015.selfcashier.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.holder.DetailTransaksiHolder
import com.it015.selfcashier.model.DetailTransaksiModel
import java.text.DecimalFormat

class DetailTransaksiAdapter(var list: MutableList<DetailTransaksiModel>):RecyclerView.Adapter<DetailTransaksiHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailTransaksiHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.result_detail_transaksi,parent,false)
        return DetailTransaksiHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DetailTransaksiHolder, position: Int) {
        val produk=list.get(position)
        val df=DecimalFormat("#,###,###")
        holder.row_detail_transaksi_produk.text=produk.produk
        holder.row_detail_transaksi_harga.text="${produk.jumlah} * Rp. ${df.format(produk.harga)}"
        holder.row_detail_transaksi_total.text="Rp. ${df.format(produk.total)}"
    }

    override fun getItemCount(): Int {
        return list.size
    }
}