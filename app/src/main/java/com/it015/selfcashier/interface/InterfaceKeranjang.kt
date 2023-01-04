package com.it015.selfcashier.`interface`

import com.it015.selfcashier.model.KeranjangModel

interface InterfaceKeranjang {

    fun requestTambah(keranjangModel: MutableList<KeranjangModel>)
    fun requestKurang(keranjangModel: MutableList<KeranjangModel>)

}