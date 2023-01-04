package com.it015.selfcashier.`interface`

import com.it015.selfcashier.model.kasir.TransaksiKasirModel

interface KasirDashboardInterface {
    fun gototDetailTransaksiKasir(transaksiKasirModel: TransaksiKasirModel)
}