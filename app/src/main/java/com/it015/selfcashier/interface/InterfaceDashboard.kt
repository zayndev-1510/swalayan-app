package com.it015.selfcashier.`interface`

import com.it015.selfcashier.model.TransaksiModel
import com.it015.selfcashier.model.kasir.TransaksiKasirModel

interface InterfaceDashboard {
    fun gotoDetailTransaksi(transaksiModel: TransaksiModel)

}