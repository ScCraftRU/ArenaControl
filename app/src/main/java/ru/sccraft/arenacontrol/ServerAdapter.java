package ru.sccraft.arenacontrol;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Создан пользователем alexandr 10.02.18 16:20, работающем в комманде ScCraft.
 */

public class ServerAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater lInflater;
    Server[] сервер;

    public ServerAdapter(Context context, Server[] масив_серверов) {
        this.context = context;
        сервер = масив_серверов;
    }

    @Override
    public int getCount() {
        return сервер.length;
    }

    @Override
    public Object getItem(int i) {
        return сервер[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // используем созданные, но не используемые view
        View вью = view;
        if (вью == null) {
            вью = lInflater.inflate(android.R.layout.simple_list_item_2, viewGroup, false);
        }
        Server сервер = получить_сервер(i);
        TextView название = вью.findViewById(android.R.id.text1);
        TextView ID = вью.findViewById(android.R.id.text2);
        название.setText(сервер.имя_сервера);
        ID.setText(сервер.id + "");
        return вью;
    }

    private Server получить_сервер(int позиция) {
        return (Server) getItem(позиция);
    }
}
