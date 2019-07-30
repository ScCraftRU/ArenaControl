package ru.sccraft.arenacontrol

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

class UpdateActivity : AppCompatActivity() {

    private var сервер: Server? = null
    private var fe: Fe? = null
    private var поток: Поток? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        setTitle(R.string.pleaseWait)
        fe = Fe(this)
        сервер = Server.fromJSON(intent.getStringExtra("server")!!)
        поток = lastCustomNonConfigurationInstance as? Поток
        if (поток == null) {
            поток = Поток(this)
        } else {
            поток!!.link(this)
        }
    }

    @SuppressLint("StaticFieldLeak")
    private inner class Поток internal constructor(internal var активность: UpdateActivity) : AsyncTask<Server, Void, Boolean>() {

        init {
            execute(сервер)
        }

        internal fun link(activity: UpdateActivity) {
            активность = activity
        }

        override fun doInBackground(vararg сервер1: Server): Boolean? {
            val сервер = сервер1[0]
            val b = сервер.update()
            активность.fe!!.saveFile(активность.сервер!!.получить_токен() + ".json", активность.сервер!!.toJSON())
            return b
        }

        override fun onPostExecute(aBoolean: Boolean?) {
            super.onPostExecute(aBoolean)
            if (aBoolean != null)
                if ((!aBoolean)) {
                    Toast.makeText(активность.applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
                }
            val intent = Intent()
            intent.putExtra("server", активность.сервер!!.toJSON())
            setResult(0, intent)
            finish()
        }
    }

    override fun onRetainCustomNonConfigurationInstance(): Any? {
        return поток
    }

    override fun onBackPressed() {}
}
