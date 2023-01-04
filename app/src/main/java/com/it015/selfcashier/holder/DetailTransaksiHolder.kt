package com.it015.selfcashier.holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R

class DetailTransaksiHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val row_detail_transaksi_produk=itemView.findViewById<TextView>(R.id.row_detail_transaksi_produk)
    val row_detail_transaksi_harga=itemView.findViewById<TextView>(R.id.row_detail_transaksi_harga)
    val row_detail_transaksi_total=itemView.findViewById<TextView>(R.id.row_detail_transaksi_total)

}