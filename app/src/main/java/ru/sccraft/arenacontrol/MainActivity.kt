package ru.sccraft.arenacontrol

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {

    internal lateinit var сервер: Array<Server>
    internal lateinit var file: Array<String>
    internal lateinit var fe: Fe
    internal lateinit var lv: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fe = Fe(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        lv = findViewById(R.id.listView_main)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            val intent = Intent(this@MainActivity, AddServerActivity::class.java)
            startActivity(intent)
        }
        run {
        }
    }

    override fun onResume() {
        super.onResume()
        file = fileList()
        if (file.size == 0 || (file.size == 1 && file[0].contains("PersistedInstallation"))) {
            //нет серверов
            setTitle(R.string.noServers)
            val ошибка = arrayOf(getString(R.string.noServers))
            val адаптер = ArrayAdapter(this, android.R.layout.simple_list_item_1, ошибка)
            lv.adapter = адаптер
            lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val intent = Intent(this@MainActivity, AddServerActivity::class.java)
                startActivity(intent)
            }
            lv.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id -> false }
        } else {
            setTitle(R.string.app_name)
            run {
                val pre = ArrayList<Server>()
                for (файл in file) {
                    if (!файл.contains(".json")) continue
                    if (файл.contains("PersistedInstallation")) continue
                    pre.add(Server.fromJSON(fe.getFile(файл)))
                }
                сервер = pre.toTypedArray()
            }
            val настройки = PreferenceManager.getDefaultSharedPreferences(this)
            val сортировать_по = настройки.getString("settings_sort_servers_by", "")
            val настройки_сортировки = resources.getStringArray(R.array.settings_sort_servers_by)
            if (сортировать_по == настройки_сортировки[0]) {
                Arrays.sort(сервер) { server, t1 -> server.получить_токен().compareTo(t1.получить_токен()) }
            } else if (сортировать_по == настройки_сортировки[1]) {
                Arrays.sort(сервер) { server, t1 -> ("" + server.получить_id()).compareTo("" + t1.получить_id()) }
            } else if (сортировать_по == настройки_сортировки[2]) {
                Arrays.sort(сервер) { server, t1 -> server.получить_имя().compareTo(t1.получить_имя()) }
            }
            val адаптер = ServerAdapter(this, сервер)
            lv.adapter = адаптер
            lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                val s = сервер[position]
                val intent = Intent(this@MainActivity, UpdateActivity::class.java)
                intent.putExtra("server", s.toJSON())
                startActivityForResult(intent, 1)
            }
            lv.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                val s = сервер[position].toJSON()
                val intent = Intent(this@MainActivity, ServerActivity::class.java)
                intent.putExtra("server", s)
                startActivity(intent)
                true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            R.id.action_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_settings -> {
                val intent1 = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent1)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == 0) {
                val сервер = data!!.getStringExtra("server")
                val intent = Intent(this@MainActivity, ServerActivity::class.java)
                intent.putExtra("server", сервер)
                startActivity(intent)
            }
        }
    }
}
