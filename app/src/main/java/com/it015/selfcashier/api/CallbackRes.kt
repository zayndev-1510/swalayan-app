package com.it015.selfcashier.api

import com.android.volley.VolleyError

interface CallbackRes {

    fun oncallbackSuccess(result:String)
    fun oncallbackError(volleyError: VolleyError)
}