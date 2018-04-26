package ru.sccraft.arenacontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    Server[] сервер;
    String[] file;
    Fe fe;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fe = new Fe(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv = findViewById(R.id.listView_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AddServerActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        file = fileList();
        if (file.length == 0) {
            //нет серверов
            setTitle(R.string.noServers);
            String[] ошибка = new String[]{getString(R.string.noServers)};
            ArrayAdapter<String> адаптер = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ошибка);
            lv.setAdapter(адаптер);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, AddServerActivity.class);
                    startActivity(intent);
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    return false;
                }
            });
        } else {
            setTitle(R.string.app_name);
            {
                ArrayList<Server> pre = new ArrayList<>();
                for (String файл : file) {
                    if (!(файл.contains(".json"))) continue;
                    pre.add(Server.fromJSON(fe.getFile(файл)));
                }
                сервер = pre.toArray(new Server[pre.size()]);
            }
            SharedPreferences настройки = PreferenceManager.getDefaultSharedPreferences(this);
            String сортировать_по = настройки.getString("settings_sort_servers_by", "");
            String[] настройки_сортировки = getResources().getStringArray(R.array.settings_sort_servers_by);
            if (сортировать_по.equals(настройки_сортировки[0])) {
                Arrays.sort(сервер, new Comparator<Server>() {
                    @Override
                    public int compare(Server server, Server t1) {
                        return server.получить_токен().compareTo(t1.получить_токен());
                    }
                });
            } else if (сортировать_по.equals(настройки_сортировки[1])) {
                Arrays.sort(сервер, new Comparator<Server>() {
                    @Override
                    public int compare(Server server, Server t1) {
                        return ("" + server.получить_id()).compareTo(("" + t1.получить_id()));
                    }
                });
            } else if (сортировать_по.equals(настройки_сортировки[2])) {
                Arrays.sort(сервер, new Comparator<Server>() {
                    @Override
                    public int compare(Server server, Server t1) {
                        return server.получить_имя().compareTo(t1.получить_имя());
                    }
                });
            }
            ServerAdapter адаптер = new ServerAdapter(this, сервер);
            lv.setAdapter(адаптер);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Server s = сервер[position];
                    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                    intent.putExtra("server", s.toJSON());
                    startActivityForResult(intent, 1);
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String s = сервер[position].toJSON();
                    Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                    intent.putExtra("server", s);
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Intent intent1 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent1);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 0) {
                String сервер = data.getStringExtra("server");
                Intent intent = new Intent(MainActivity.this, ServerActivity.class);
                intent.putExtra("server", сервер);
                startActivity(intent);
            }
        }
    }
}
