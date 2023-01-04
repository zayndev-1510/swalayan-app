package com.it015.selfcashier.model

data class KeranjangModel(
    var id_keranjang:Int,
    var id_pengguna:Int,
    var id_barang:Int,
    var jumlah:Int,
    var harga:Double,
    var nomor_keranjang:String,
    var tgl:String,
    var status:Int,
    var nama_barang:String
)