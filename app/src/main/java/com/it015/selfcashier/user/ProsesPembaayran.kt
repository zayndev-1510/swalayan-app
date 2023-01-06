package com.it015.selfcashier.user

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.it015.selfcashier.R

class ProsesPembaayran:AppCompatActivity() {
    private lateinit var btn_selesai: TextView
    private lateinit var imagebarcode: ImageView

    var bitmap: Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pembayaran_page)
        btn_selesai=findViewById(R.id.btn_selesai)
        imagebarcode=findViewById(R.id.imagebarcode)

        val intent= intent
        val nomor_transaksi=intent.getStringExtra("nomor_transaksi").toString()
        Toast.makeText(this,nomor_transaksi,Toast.LENGTH_LONG).show()
        val word=nomor_transaksi
        val manager=getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displaly=manager.defaultDisplay
        val point= Point()
        displaly.getSize(point)
        val width=point.x
        val height=point.y
        var dimen=if(width<height) width else height
        dimen=dimen *3/4
        val qrgEncoder= QRGEncoder(word, null, QRGContents.Type.TEXT, dimen)
        qrgEncoder.setColorBlack(Color.WHITE);
        qrgEncoder.setColorWhite(Color.BLACK);
        try {
            bitmap=qrgEncoder.getBitmap()
            imagebarcode.setImageBitmap(bitmap)
        }
        catch (e:Exception){
            Log.d("tag",e.toString())
        }

        btn_selesai.setOnClickListener {
            val intent= Intent(this, DashboardPage::class.java)
            startActivity(intent)
            finish()
        }
    }
}