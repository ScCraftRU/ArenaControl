package ru.sccraft.arenacontrol

import android.os.Bundle
import android.view.View
import android.widget.EditText

import com.google.android.gms.ads.AdView

class PlayerActivity : ADsActivity() {

    lateinit private var сервер: Server
    lateinit private var игрок: String
    lateinit private var сообщение: EditText
    lateinit var adview: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        сервер = Server.fromJSON(intent.getStringExtra("server")!!) //Здесь гн может быть NULL т.к. сюда можно попасть только из ServerActivity, которая вылетит при nul
        игрок = intent.getStringExtra("name")!!
        title = игрок
        сообщение = findViewById(R.id.player_message)
        adview = findViewById(R.id.adView)
        задать_баннер(adview)
    }

    fun kick(view: View) {
        val сообщение = this.сообщение.text.toString()
        if (сообщение == "") {
            сервер.выполнить_комманду("kick " + игрок + " " + getString(R.string.player_kickByApp))
        } else {
            сервер.выполнить_комманду("kick $игрок $сообщение")
        }
    }

    fun ban(view: View) {
        val сообщение = this.сообщение.text.toString()
        if (сообщение == "") {
            сервер.выполнить_комманду("ban $игрок ${getString(R.string.player_kickByApp)}")
        } else {
            сервер.выполнить_комманду("ban $игрок $сообщение")
        }
    }

    fun op(view: View) {
        сервер.выполнить_комманду("op $игрок")
    }

    fun deOP(view: View) {
        сервер.выполнить_комманду("deop $игрок")
    }

    fun kill(view: View) {
        сервер.выполнить_комманду("kill $игрок")
    }

    fun gameMode_survival(view: View) {
        if (сервер.геймМод_1_13)
            сервер.выполнить_комманду("gamemode survival $игрок")
        else
            сервер.выполнить_комманду("gamemode 0 $игрок")
    }

    fun gameMode_creative(view: View) {
        if (сервер.геймМод_1_13)
            сервер.выполнить_комманду("gamemode creative $игрок")
        else
            сервер.выполнить_комманду("gamemode 1 $игрок")
    }

    fun gameMode_gameMode_adventure(view: View) {
        if (сервер.геймМод_1_13)
            сервер.выполнить_комманду("gamemode adventure $игрок")
        else
            сервер.выполнить_комманду("gamemode 2 $игрок")
    }

    fun gameMode_observer(view: View) {
        if (сервер.геймМод_1_13)
            сервер.выполнить_комманду("gamemode spectator $игрок")
        else
            сервер.выполнить_комманду("gamemode 3 $игрок")
    }

    fun whitelistAdd(view: View) {
        сервер.выполнить_комманду("whitelist add $игрок")
    }

    fun whitelistRemove(view: View) {
        сервер.выполнить_комманду("whitelist remove $игрок")
    }
}
