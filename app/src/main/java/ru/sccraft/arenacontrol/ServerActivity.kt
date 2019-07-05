package ru.sccraft.arenacontrol

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.ads.AdView

class ServerActivity : ADsActivity() {

    private var сервер: Server? = null
    internal lateinit var adView: AdView

    /**
     * The [PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /**
     * Объект [ViewPager] отвечает за вкладки.
     */
    private var mViewPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            сервер = Server.fromJSON(intent.getStringExtra("server")!!)
        } else {
            сервер = Server.fromJSON(savedInstanceState.getString("server")!!)
        }
        setContentView(R.layout.activity_server)

        adView = findViewById(R.id.adView)
        if (сервер!!.id != 0) {
            задать_баннер(adView)
        } else {
            adView.visibility = View.GONE //Убираем рекламу при неправельном Token. (Запросы в AdMob при этом не отправляем)
        }
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container)
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tabs)
        tabLayout.setupWithViewPager(mViewPager)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(View.OnClickListener { view ->
            if (сервер!!.id == 0) {
                Snackbar.make(view, R.string.serverActivity_error, Snackbar.LENGTH_LONG).setAction("Remove server") { удалить() }.show()
                return@OnClickListener
            }
            val intent = Intent(this@ServerActivity, ConsoleActivity::class.java)
            intent.putExtra("server", сервер!!.toJSON())
            startActivity(intent)
        })

        title = сервер!!.имя_сервера + " (" + сервер!!.id + ")"
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_server, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        when (id) {
            R.id.action_update -> {
                обновить()
                return true
            }
            R.id.action_edit_commands -> {
                val intent = Intent(this@ServerActivity, CommandEditActivity::class.java)
                intent.putExtra("server", сервер!!.toJSON())
                startActivity(intent)
                return true
            }
            R.id.action_clear -> {
                сервер!!.очистить_комманды()
                fe.saveFile(сервер!!.получить_токен() + ".json", сервер!!.toJSON())
                finish()
                return true
            }
            R.id.action_remove -> {
                удалить()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class ServerInfoFragment : Fragment() {
        private var rootView: View? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_server, container, false)
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView
            return rootView
        }

        override fun onResume() {
            super.onResume()
            val name = rootView!!.findViewById<View>(R.id.server_name) as TextView
            val s = activity as ServerActivity?
            name.text = s!!.сервер!!.имя_сервера
            val id = rootView!!.findViewById<View>(R.id.server_id) as TextView
            id.text = "" + s.сервер!!.id
            val status = rootView!!.findViewById<View>(R.id.serverActivity_status) as TextView
            status.text = rootView!!.resources.getStringArray(R.array.statusArray)[s.сервер!!.статус.toInt()]
            val IP = rootView!!.findViewById<TextView>(R.id.server_ip)
            IP.text = s.сервер!!.ip
            val игра = rootView!!.findViewById<TextView>(R.id.server_game)
            игра.text = s.сервер!!.игра
            val игра_версия = rootView!!.findViewById<TextView>(R.id.server_version)
            игра_версия.text = s.сервер!!.игра_версия
            val плагины = rootView!!.findViewById<TextView>(R.id.server_plugins)
            плагины.text = s.сервер!!.плагины
            val дней_до_окончания_аренды = rootView!!.findViewById<TextView>(R.id.server_days)
            дней_до_окончания_аренды.text = "" + s.сервер!!.дней_до_окончания_аренды
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): ServerInfoFragment {
                val fragment = ServerInfoFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    class ServerPlayersFragment : Fragment() {
        private var rootView: View? = null
        private var s: ServerActivity? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_players, container, false)
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView
            return rootView
        }

        override fun onResume() {
            super.onResume()
            val lv = rootView!!.findViewById<View>(R.id.listView_players) as ListView
            s = activity as ServerActivity?
            val игроки = s!!.сервер!!.игроки ?: return
            val aa = ArrayAdapter(s!!, android.R.layout.simple_list_item_1, игроки)
            lv.adapter = aa
            lv.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
                if (s!!.сервер!!.игроки!![position] == "") return@OnItemLongClickListener true
                s!!.сервер!!.выполнить_комманду("kick " + s!!.сервер!!.игроки!![position] + " " + getString(R.string.player_kickByApp))
                true
            }
            lv.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                if (s!!.сервер!!.игроки!![position] == "") return@OnItemClickListener
                val intent = Intent(s, PlayerActivity::class.java)
                intent.putExtra("server", s!!.сервер!!.toJSON())
                intent.putExtra("name", s!!.сервер!!.игроки!![position])
                startActivity(intent)
            }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): ServerPlayersFragment {
                val fragment = ServerPlayersFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * Показывает нагрузку на сервер.
     * Позволяет включать, выключать и перезагружать сервер.
     * На Bukkit и Spigot серверах поддерживается перезапуск плагинов.
     */
    class ServerResFragment : Fragment() {
        private var rootView: View? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_resources, container, false)
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView
            return rootView
        }

        override fun onResume() {
            super.onResume()
            val s = activity as ServerActivity?
            val игроки = rootView!!.findViewById<ProgressBar>(R.id.res_players_pb)
            игроки.max = s!!.сервер!!.игроки_всего
            игроки.progress = s.сервер!!.игроки_на_сервере
            val игроки1 = rootView!!.findViewById<TextView>(R.id.res_players_title)
            игроки1.text = игроки1.text.toString() + " (" + s.сервер!!.игроки_на_сервере + "/" + s.сервер!!.игроки_всего
            val процессор = rootView!!.findViewById<ProgressBar>(R.id.res_cpu_pb)
            процессор.max = 100
            процессор.progress = s.сервер!!.процессор_в_процентах.toInt()
            val процессор1 = rootView!!.findViewById<TextView>(R.id.res_cpu_title)
            процессор1.text = процессор1.text.toString() + " (" + s.сервер!!.процессор_в_процентах + "%)"
            val озу = rootView!!.findViewById<ProgressBar>(R.id.res_ram_pb)
            озу.max = s.сервер!!.озу_всего
            озу.progress = s.сервер!!.озу_использовано
            val озу1 = rootView!!.findViewById<TextView>(R.id.res_ram_title)
            озу1.text = озу1.text.toString() + " (" + s.сервер!!.озу_в_процентах + "%)"
            val диск = rootView!!.findViewById<ProgressBar>(R.id.res_disk_pb)
            диск.max = s.сервер!!.диск_всего
            диск.progress = s.сервер!!.диск_использовано
            val диск1 = rootView!!.findViewById<TextView>(R.id.res_disk_title)
            диск1.text = диск1.text.toString() + " (" + s.сервер!!.диск_в_процентах + "%)"

            val ожидание_ответа_от_сервера = rootView!!.findViewById<ProgressBar>(R.id.res_pleaseWait)

            //Кнопки включения / выключения / перезагрузки сервера
            val включить = rootView!!.findViewById<Button>(R.id.res_start)
            val выключить = rootView!!.findViewById<Button>(R.id.res_stop)
            val перезагрузить = rootView!!.findViewById<Button>(R.id.res_reboot)
            val перезагрузить_плагины = rootView!!.findViewById<Button>(R.id.res_reload)

            if (s.сервер!!.статус.toInt() == 0) {
                включить.visibility = View.VISIBLE
            } else {
                включить.visibility = View.GONE
            }
            включить.setOnClickListener {
                ожидание_ответа_от_сервера.visibility = View.VISIBLE
                s.сервер!!.включить(object : Server.Операция_завершена {
                    override fun успешно() {
                        s.обновить()
                    }

                    override fun ошибка() {
                        Toast.makeText(rootView!!.context.applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
                    }
                })
                выключить.visibility = View.GONE
                перезагрузить.visibility = View.GONE
                перезагрузить_плагины.visibility = View.GONE
            }
            if (s.сервер!!.статус.toInt() != 0) {
                выключить.visibility = View.VISIBLE
            } else {
                выключить.visibility = View.GONE
            }
            выключить.setOnClickListener {
                ожидание_ответа_от_сервера.visibility = View.VISIBLE
                s.сервер!!.выключить(object : Server.Операция_завершена {
                    override fun успешно() {
                        s.обновить()
                    }

                    override fun ошибка() {
                        Toast.makeText(rootView!!.context.applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
                    }
                })
                включить.visibility = View.GONE
                перезагрузить.visibility = View.GONE
                перезагрузить_плагины.visibility = View.GONE
            }
            if (s.сервер!!.статус.toInt() == 1) {
                перезагрузить.visibility = View.VISIBLE
            } else {
                перезагрузить.visibility = View.GONE
            }
            перезагрузить.setOnClickListener {
                ожидание_ответа_от_сервера.visibility = View.VISIBLE
                s.сервер!!.перезагрузить(object : Server.Операция_завершена {
                    override fun успешно() {
                        s.обновить()
                    }

                    override fun ошибка() {
                        Toast.makeText(rootView!!.context.applicationContext, R.string.error, Toast.LENGTH_SHORT).show()
                    }
                })
                выключить.visibility = View.GONE
                перезагрузить_плагины.visibility = View.GONE
            }
            if (s.сервер!!.статус.toInt() == 1 && s.сервер!!.плагины != "") {
                перезагрузить_плагины.visibility = View.VISIBLE
            } else {
                перезагрузить_плагины.visibility = View.GONE
            }
            перезагрузить_плагины.setOnClickListener { s.сервер!!.выполнить_комманду("reload") }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): ServerResFragment {
                val fragment = ServerResFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    class ServerWorldFragment : Fragment() {
        private var rootView: View? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_world, container, false)
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            this.rootView = rootView
            return rootView
        }

        override fun onResume() {
            super.onResume()
            val s = activity as ServerActivity?
            val день = rootView!!.findViewById<View>(R.id.world_timeDay) as Button
            день.setOnClickListener { s!!.сервер!!.выполнить_комманду(s.сервер!!.комманда_день) }
            val ночь = rootView!!.findViewById<View>(R.id.world_timeNight) as Button
            ночь.setOnClickListener { s!!.сервер!!.выполнить_комманду(s.сервер!!.комманда_ночь) }
            val время = rootView!!.findViewById<EditText>(R.id.world_timeEditText)
            val время_задать = rootView!!.findViewById<Button>(R.id.world_timeSet)
            val время_добавить = rootView!!.findViewById<Button>(R.id.world_timeAdd)
            время_задать.setOnClickListener {
                val комманда = s!!.сервер!!.комманда_задать_время.replace("%time%", время.text.toString())
                s.сервер!!.выполнить_комманду(комманда)
            }
            время_добавить.setOnClickListener {
                val комманда = s!!.сервер!!.комманда_добавить_время.replace("%time%", время.text.toString())
                s.сервер!!.выполнить_комманду(комманда)
            }
            val погода_ясно = rootView!!.findViewById<Button>(R.id.world_weatherClear)
            погода_ясно.setOnClickListener {
                val комманда = s!!.сервер!!.комманда_погода.replace("%weather%", "clear")
                s.сервер!!.выполнить_комманду(комманда)
            }
            val погода_дождь = rootView!!.findViewById<Button>(R.id.world_weatherRain)
            погода_дождь.setOnClickListener {
                val комманда = s!!.сервер!!.комманда_погода.replace("%weather%", "rain")
                s.сервер!!.выполнить_комманду(комманда)
            }
            val белый_список_включить = rootView!!.findViewById<Button>(R.id.world_whitelist_on)
            белый_список_включить.setOnClickListener { s!!.сервер!!.выполнить_комманду("whitelist on") }
            val белый_список_выключить = rootView!!.findViewById<Button>(R.id.world_whitelist_off)
            белый_список_выключить.setOnClickListener { s!!.сервер!!.выполнить_комманду("whitelist off") }
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): ServerWorldFragment {
                val fragment = ServerWorldFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a ServerInfoFragment (defined as a static inner class below).
            if (сервер!!.статус.toInt() == 1) {
                if (сервер!!.игроки_на_сервере > 0) {
                    when (position) {
                        0 -> return ServerInfoFragment.newInstance(position + 1)
                        1 -> return ServerPlayersFragment.newInstance(position + 1)
                        2 -> return ServerResFragment.newInstance(position + 1)
                        3 -> return ServerWorldFragment.newInstance(position + 1)
                    }
                } else {
                    when (position) {
                        0 -> return ServerInfoFragment.newInstance(position + 1)
                        1 -> return ServerResFragment.newInstance(position + 1)
                        2 -> return ServerWorldFragment.newInstance(position + 1)
                    }
                }
            } else {
                when (position) {
                    0 -> return ServerInfoFragment.newInstance(position + 1)
                    1 -> return ServerResFragment.newInstance(position + 1)
                }
            }
            return ServerInfoFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return if (сервер!!.статус.toInt() == 1) {
                if (сервер!!.игроки_на_сервере > 0)
                    4
                else
                    3
            } else {
                2
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            if (сервер!!.статус.toInt() == 1) {
                if (сервер!!.игроки_на_сервере > 0) {
                    when (position) {
                        0 -> return getString(R.string.serverActivity_info)
                        1 -> return getString(R.string.serverActivity_players)
                        2 -> return getString(R.string.serverActivity_resources)
                        3 -> return getString(R.string.serverActivity_world)
                    }
                } else {
                    when (position) {
                        0 -> return getString(R.string.serverActivity_info)
                        1 -> return getString(R.string.serverActivity_resources)
                        2 -> return getString(R.string.serverActivity_world)
                    }
                }
            } else {
                when (position) {
                    0 -> return getString(R.string.serverActivity_info)
                    1 -> return getString(R.string.serverActivity_resources)
                }
            }
            return null
        }
    }

    private fun обновить() {
        val intent = Intent(this@ServerActivity, UpdateActivity::class.java)
        intent.putExtra("server", сервер!!.toJSON())
        startActivityForResult(intent, 1)
    }

    private fun удалить() {
        deleteFile(сервер!!.получить_токен() + ".json")
        Toast.makeText(applicationContext, R.string.serverRemoved, Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("server", сервер!!.toJSON())
        super.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == 0) {
                val сервер = data!!.getStringExtra("server")
                val intent = Intent(this@ServerActivity, ServerActivity::class.java)
                intent.putExtra("server", сервер)
                startActivity(intent)
                finish()
            }
        }
    }
}