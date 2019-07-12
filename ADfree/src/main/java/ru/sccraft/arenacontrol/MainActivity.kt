package ru.sccraft.arenacontrol

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val _PlayStore_URL: String
        get() = "market://details?id=$packageName"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fe = Fe(this)
        fe.saveFile("arenacontrol-ads", "1") //Отключение рекламы
        fe.saveFile("adid", "1") //Отключение соглашения на использование рекламного интендификатора

        setContentView(R.layout.activity_main) //Предлагаем обновить приложение с сохранением данных. (Подписи приложений совпадают!!!)
    }

    fun googlePlay(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(_PlayStore_URL)
        startActivity(intent)

    }

    fun gitHub(view: View) {
        val ссылка_на_релизы_GitHub = Uri.parse(getString(R.string.gitHub_url))
        val открыть_в_браузере = Intent(Intent.ACTION_VIEW, ссылка_на_релизы_GitHub)
        startActivity(открыть_в_браузере)
    }

    /**
     * Открывает страницу форума 4PDA.ru
     * @param view передаётся из XML
     */
    fun pda4(view: View) {
        val ссылка_на_релизы_GitHub = Uri.parse(getString(R.string.pda4))
        val открыть_в_браузере = Intent(Intent.ACTION_VIEW, ссылка_на_релизы_GitHub)
        startActivity(открыть_в_браузере)
    }
}
