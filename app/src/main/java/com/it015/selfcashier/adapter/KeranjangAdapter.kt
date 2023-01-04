package com.it015.selfcashier.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.InterfaceKeranjang
import com.it015.selfcashier.holder.KeranjangHolder
import com.it015.selfcashier.model.KeranjangModel
import java.text.DecimalFormat

class KeranjangAdapter(var list: MutableList<KeranjangModel>,var interfaceKeranjang: InterfaceKeranjang):RecyclerView.Adapter<KeranjangHolder>() {
    var num=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeranjangHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.result_keranjang,parent,false)
        return KeranjangHolder(view)
    }


    override fun onBindViewHolder(holder: KeranjangHolder, position: Int) {
        val keranjangResponse=list.get(position)
        holder.nama_barang.text=keranjangResponse.nama_barang
        val ket_harga="${keranjangResponse.jumlah} x ${keranjangResponse.harga}"
        holder.harga_barang.text=ket_harga
        val total_harga=keranjangResponse.jumlah*keranjangResponse.harga
        val DF = DecimalFormat("#,###,###")
        val format_rupiah="Rp."+DF.format(total_harga)
        holder.jumlah_input.setText(keranjangResponse.jumlah.toString())
        //event objek

        holder.btn_tambah_jumlah.setOnClickListener{
            requestTambahJumlah(holder,keranjangResponse)
            interfaceKeranjang.requestTambah(list)

        }

        holder.btn_tambah_kurang.setOnClickListener {
            requestKurangJumlah(holder,keranjangResponse)
            interfaceKeranjang.requestKurang(list)

        }

    }

    private fun requestKurangJumlah(holder: KeranjangHolder, keranjangResponse: KeranjangModel) {
        keranjangResponse.jumlah--
        val jml=keranjangResponse.jumlah
        if(jml<0){
            holder.jumlah_input.setText(0.toString())
            keranjangResponse.jumlah=0
        }else{
            holder.jumlah_input.setText(keranjangResponse.jumlah.toString())
        }

    }

    private fun requestTambahJumlah(holder: KeranjangHolder, keranjangResponse: KeranjangModel) {
        keranjangResponse.jumlah++
        holder.jumlah_input.setText(keranjangResponse.jumlah.toString())
    }
    override fun getItemCount(): Int {
       return list.size
    }
}
