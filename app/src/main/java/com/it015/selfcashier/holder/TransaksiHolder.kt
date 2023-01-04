package com.it015.selfcashier.holder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.it015.selfcashier.R

class TransaksiHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
    val row_date=itemView.findViewById<TextView>(R.id.row_date)
    val row_month=itemView.findViewById<TextView>(R.id.row_month)
    val row_nomor_transaksi=itemView.findViewById<TextView>(R.id.row_nomor_transaction)
    val row_total=itemView.findViewById<TextView>(R.id.row_total)
    val linear_layout_transaksi=itemView.findViewById<LinearLayout>(R.id.linear_layout_transaksi)
}