package ru.sccraft.arenacontrol

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView

class ConsoleActivity : ADsActivity() {

    private lateinit var сервер: Server
    private lateinit var консоль: TextView
    private lateinit var комманда: EditText
    private lateinit var настройки: SharedPreferences
    private lateinit var прокрутка: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        настройки = PreferenceManager.getDefaultSharedPreferences(this)
        if (savedInstanceState == null) {
            сервер = Server.fromJSON(intent.getStringExtra("server")!!)
        } else {
            сервер = Server.fromJSON(savedInstanceState.getString("server")!!)
        }
        setContentView(R.layout.activity_console)
        setTitle(R.string.title_activity_console)

        прокрутка = findViewById(R.id.console_scrollView)
        комманда = findViewById(R.id.console_cmd)
        val отправить = findViewById<ImageButton>(R.id.console_send)
        консоль = findViewById(R.id.console_textView)
        консоль.text = сервер.консоль
        прокрутить_до_конца()
        комманда.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KEYCODE_ENTER) {
                выполнить_комманду()
            }
            false
        }
        if (сервер.статус.toInt() != 0) {
            комманда.visibility = View.VISIBLE
            отправить.visibility = View.VISIBLE
        } else {
            комманда.visibility = View.GONE
            отправить.visibility = View.GONE
        }
        val adView = findViewById<AdView>(R.id.adView)
        задать_баннер(adView)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //Выполняется после прогрузки рекламы
                прокрутить_до_конца()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

        }
    }

    fun send(view: View) {
        выполнить_комманду()
    }

    private fun выполнить_комманду() {
        val command = комманда.text.toString()
        сервер.выполнить_комманду(command)
        val очистить_поле_ввода = настройки.getBoolean("settings_clear_command", true)
        if (очистить_поле_ввода) комманда.setText("")
        обновить()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_console, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_update -> {
                обновить()
                return true
            }
            R.id.action_banlist -> {
                сервер.выполнить_комманду("banlist")
                обновить()
                return true
            }
            R.id.action_whitelist -> {
                сервер.выполнить_комманду("whitelist list")
                обновить()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    internal fun обновить() {
        val intent = Intent(this@ConsoleActivity, UpdateActivity::class.java)
        intent.putExtra("server", сервер.toJSON())
        startActivityForResult(intent, 1)
    }

    private fun прокрутить_до_конца() {
        if (настройки.getBoolean("settings_auto_scroll_console", true))
            прокрутка.post {
                прокрутка.fullScroll(View.FOCUS_DOWN) //Прокручиваем вывод консоли до конца.
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("server", сервер!!.toJSON())
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == 0) {
                сервер = Server.fromJSON(data!!.getStringExtra("server")!!)
                консоль.text = сервер.консоль
                прокрутить_до_конца()
            }
        }
    }
}
