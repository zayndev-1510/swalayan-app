package com.it015.selfcashier.model.kasir

data class DetailTransaksiModelKasir (
    var produk:String,
    var jumlah:Int,
    var harga:Double,
    var total:Double,
    var id_produk:Int,
    var status:String,
    var stok:Int
)