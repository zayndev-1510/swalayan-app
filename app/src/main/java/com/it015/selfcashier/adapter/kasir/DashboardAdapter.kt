package com.it015.selfcashier.adapter.kasir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R
import com.it015.selfcashier.`interface`.KasirDashboardInterface
import com.it015.selfcashier.holder.kasir.TransaksiHolderKasir
import com.it015.selfcashier.model.kasir.TransaksiKasirModel
import java.text.DecimalFormat

class DashboardAdapter(var list: MutableList<TransaksiKasirModel>,val interfaceDashboard:KasirDashboardInterface):RecyclerView.Adapter<TransaksiHolderKasir>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiHolderKasir {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.result_transaksi_kasir,parent,false)
        return TransaksiHolderKasir(view)
    }

    override fun onBindViewHolder(holder: TransaksiHolderKasir, position: Int) {
        val transaksiModel=list.get(position)
        val DF = DecimalFormat("#,###,###")
        holder.row_nomor_transaksi.text=transaksiModel.nomor_transaksi
        val format_rupiah="Rp."+DF.format(transaksiModel.total)
        holder.row_total.text=format_rupiah
        holder.row_month.text=transaksiModel.month.substring(0,3)
        holder.row_date.text=transaksiModel.date
        holder.linear_layout_transaksi.setOnClickListener{
            interfaceDashboard.gototDetailTransaksiKasir(transaksiModel)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}