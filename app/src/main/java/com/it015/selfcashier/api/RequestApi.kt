package com.it015.selfcashier.api

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlin.collections.HashMap

class RequestApi {
    var result:String=""
    fun PostingText(context: Context,map:Map<String,String>,url:String,callbackRes: CallbackRes){
        val request_queue= Volley.newRequestQueue(context)
        val paramater:MutableMap<String,String> =HashMap()
        paramater.putAll(map)
        val stringRequest: StringRequest =object :StringRequest(Method.POST,url, Response.Listener {
            response ->callbackRes.oncallbackSuccess(response.toString())

        }, Response.ErrorListener { volleyError -> // error occurred
           callbackRes.oncallbackError(volleyError)
        }) {

            override fun getParams(): MutableMap<String, String> {
                return paramater
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        request_queue.add(stringRequest)
    }
    fun Getting(context: Context,url:String,callbackRes: CallbackRes){
        val request_queue=Volley.newRequestQueue(context)

        val stringRequest:StringRequest=object :StringRequest(Method.GET,url, Response.Listener {
                response ->
            callbackRes.oncallbackSuccess(response.toString())

        }, Response.ErrorListener { volleyError -> // error occurred
            callbackRes.oncallbackError(volleyError)
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {

                val headers: MutableMap<String, String> = HashMap()
                // Add your Header paramters here
                return headers
            }
        }
        request_queue.add(stringRequest)
    }


}