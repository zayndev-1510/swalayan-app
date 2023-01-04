package com.it015.selfcashier.model

import org.json.JSONArray

data class TransaksiModel(
    var nomor_transaksi:String,
    var tgl:String,
    var total:Double,
    var month:String,
    var year:String,
    var date:String,
    var produk:JSONArray
) {
}