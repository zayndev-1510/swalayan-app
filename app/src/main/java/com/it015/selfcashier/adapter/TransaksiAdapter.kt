package com.it015.selfcashier.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.InterfaceDashboard
import com.it015.selfcashier.holder.TransaksiHolder
import com.it015.selfcashier.model.TransaksiModel
import java.text.DecimalFormat

class TransaksiAdapter(var list: MutableList<TransaksiModel>,var interfaceDashboard: InterfaceDashboard):RecyclerView.Adapter<TransaksiHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.result_transaksi,parent,false)
        return TransaksiHolder(view)
    }

    override fun onBindViewHolder(holder: TransaksiHolder, position: Int) {
       val transaksiModel=list.get(position)
        val DF = DecimalFormat("#,###,###")
        holder.row_nomor_transaksi.text=transaksiModel.nomor_transaksi
        val format_rupiah="Rp."+DF.format(transaksiModel.total)
        holder.row_total.text=format_rupiah
        holder.row_month.text=transaksiModel.month.substring(0,3)
        holder.row_date.text=transaksiModel.date

        holder.linear_layout_transaksi.setOnClickListener{
            interfaceDashboard.gotoDetailTransaksi(transaksiModel)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }
}