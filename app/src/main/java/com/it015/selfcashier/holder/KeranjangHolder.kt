package com.it015.selfcashier.holder

import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R

class KeranjangHolder(itemview:View):RecyclerView.ViewHolder(itemview) {
    val nama_barang=itemview.findViewById<TextView>(R.id.row_nama_barang_cart)
    val harga_barang=itemview.findViewById<TextView>(R.id.row_harga_cart)
    val jumlah_input=itemview.findViewById<EditText>(R.id.jml_input)
    val btn_tambah_jumlah=itemview.findViewById<TextView>(R.id.btn_tambah_jumlah)
    val btn_tambah_kurang=itemview.findViewById<TextView>(R.id.btn_kurang_jumlah)

}