package ru.sccraft.arenacontrol

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

/**
 * Создан пользователем alexandr 10.02.18 16:20, работающем в комманде ScCraft.
 */

class ServerAdapter(context: Context, private val сервер: Array<Server>) : BaseAdapter() {

    private val lInflater: LayoutInflater

    init {
        lInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return сервер.size
    }

    override fun getItem(i: Int): Any {
        return сервер[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        // используем созданные, но не используемые view
        var вью: View? = view
        if (вью == null) {
            вью = lInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false)
        }
        val сервер = получить_сервер(i)
        val название = вью!!.findViewById<TextView>(android.R.id.text1)
        val ID = вью.findViewById<TextView>(android.R.id.text2)
        название.text = сервер.имя_сервера
        ID.text = сервер.id.toString()
        return вью
    }

    private fun получить_сервер(позиция: Int): Server {
        return getItem(позиция) as Server
    }
}
