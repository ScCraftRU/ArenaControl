package ru.sccraft.arenacontrol

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * Created by alexandr on 16.08.17.
 * Activity с рекламным баннером AdMob
 */

@SuppressLint("Registered")
open class ADsActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    protected lateinit var fe: Fe

    private fun показать_рекламу() {
        adView.visibility = View.GONE
        val AD_DATA = fe.getFile("arenacontrol-ads")
        if (AD_DATA.contains("1")) return  //Для повышения вероятности работы покупки. Раньше использовался equals.
        MobileAds.initialize(this, getString(R.string.admob_appid))
        adView.visibility = View.VISIBLE
        val adRequest = AdRequest.Builder().setRequestAgent("android_studio:ad_template").build()
        adView.loadAd(adRequest)
    }

    protected fun задать_баннер(баннер: AdView) {
        this.adView = баннер
        показать_рекламу()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fe = Fe(this)
    }
}
