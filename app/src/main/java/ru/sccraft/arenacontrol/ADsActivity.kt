package ru.sccraft.arenacontrol

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView

/**
 * Created by alexandr on 16.08.17.
 * Activity с рекламным баннером AdMob
 */

@SuppressLint("Registered")
open class ADsActivity : AppCompatActivity() {
    private lateinit var adView: AdView
    protected lateinit var fe: Fe

    protected fun задать_баннер(баннер: AdView) {
        this.adView = баннер
        adView.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fe = Fe(this)
    }
}
