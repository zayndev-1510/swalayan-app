package com.it015.selfcashier.model.kasir

import org.json.JSONArray

class TransaksiKasirModel(
    var nomor_transaksi:String,
    var tgl:String,
    var total:Double,
    var month:String,
    var year:String,
    var date:String,
    var produk: JSONArray

) {
}