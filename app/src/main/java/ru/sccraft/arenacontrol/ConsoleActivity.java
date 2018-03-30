package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class ConsoleActivity extends ADsActivity {

    private Server сервер;
    private TextView консоль;
    private EditText комманда;
    private Поток поток;
    private SharedPreferences настройки;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        настройки = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        } else {
            сервер = Server.fromJSON(savedInstanceState.getString("server"));
        }
        поток = (Поток) getLastCustomNonConfigurationInstance();
        if (поток == null) {
            поток = new Поток(this);
        } else {
            поток.link(this);
        }
        setContentView(R.layout.activity_console);
        setTitle(R.string.title_activity_console);
        комманда = findViewById(R.id.console_cmd);
        консоль = findViewById(R.id.console_textView);
        консоль.setText(сервер.консоль);
        комманда.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    выполнить_комманду();
                }
                return false;
            }
        });
        AdView adView = findViewById(R.id.adView);
        задать_баннер(adView);
    }

    public void send(View view) {
        выполнить_комманду();
    }

    private void выполнить_комманду() {
        String command = комманда.getText().toString();
        сервер.выполнить_комманду(command);
        boolean очистить_поле_ввода = настройки.getBoolean("settings_clear_command", true);
        if (очистить_поле_ввода) комманда.setText("");
        обновить();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_console, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_update:
                обновить();
                return true;
            case R.id.action_banlist:
                сервер.выполнить_комманду("banlist");
                обновить();
                return true;
            case R.id.action_whitelist:
                сервер.выполнить_комманду("whitelist list");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void обновить() {
        try{
            поток.execute();
        } catch (Exception e) {
            e.printStackTrace();
            поток = new Поток(this);
            поток.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Поток extends AsyncTask<Void, Void, Boolean> {

        ConsoleActivity активность;

        Поток(ConsoleActivity activity) {
            this.активность = activity;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return активность.сервер.update();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                активность.консоль.setText(активность.сервер.консоль);
            }
        }

        void link(ConsoleActivity activity) {
            активность = activity;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("server", сервер.toJSON());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return поток;
    }
}
