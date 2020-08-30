package ru.sccraft.arenacontrol

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by alexandr on 16.08.17.
 * Бывший Activity с рекламным баннеером. Сейчас не отвечает за рекламу
 */

@SuppressLint("Registered")
open class ADsActivity : AppCompatActivity() {
    protected lateinit var fe: Fe


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fe = Fe(this)
    }
}
