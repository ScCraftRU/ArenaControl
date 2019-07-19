package ru.sccraft.arenacontrol

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class CommandEditActivity : AppCompatActivity() {

    private var сервер: Server? = null
    lateinit var время_день: EditText
    lateinit var время_ночь: EditText
    lateinit var время_задать: EditText
    lateinit var время_добавить: EditText
    lateinit var погода: EditText
    lateinit var перезагрузить_плагины: EditText
    lateinit var использовать_новые_комманды_переключения_режима_игры: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_command_edit)
        setTitle(R.string.title_activity_server_command)
        сервер = Server.fromJSON(intent.getStringExtra("server")!!)

        время_день = findViewById(R.id.editText_command_day)
        время_ночь = findViewById(R.id.editText_command_night)
        время_задать = findViewById(R.id.editText_command_timeSet)
        время_добавить = findViewById(R.id.editText_command_timeAdd)
        погода = findViewById(R.id.editText_weather)
        использовать_новые_комманды_переключения_режима_игры = findViewById(R.id.newGameMode)
        перезагрузить_плагины = findViewById(R.id.editText_reload_command)

        время_день.setText(сервер!!.комманда_день)
        время_ночь.setText(сервер!!.комманда_ночь)
        время_задать.setText(сервер!!.комманда_задать_время)
        время_добавить.setText(сервер!!.комманда_добавить_время)
        погода.setText(сервер!!.комманда_погода)
        использовать_новые_комманды_переключения_режима_игры.isChecked = сервер!!.геймМод_1_13
        перезагрузить_плагины.setText(сервер!!.комманда_релоад)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_server_command, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            R.id.action_save -> {
                сохранить()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun сохранить() {
        val fe = Fe(this)
        сервер!!.комманда_день = время_день.text.toString()
        сервер!!.комманда_ночь = время_ночь.text.toString()
        сервер!!.комманда_задать_время = время_задать.text.toString()
        сервер!!.комманда_добавить_время = время_добавить.text.toString()
        сервер!!.комманда_погода = погода.text.toString()
        сервер!!.геймМод_1_13 = использовать_новые_комманды_переключения_режима_игры.isChecked
        сервер!!.комманда_релоад = перезагрузить_плагины.text.toString()
        fe.saveFile(сервер!!.получить_токен() + ".json", сервер!!.toJSON())
        finish()
    }
}
