package ru.sccraft.arenacontrol;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;

import static android.view.KeyEvent.KEYCODE_ENTER;

public class ConsoleActivity extends ADsActivity {

    private Server сервер;
    private TextView консоль;
    private EditText комманда;
    private SharedPreferences настройки;
    private ScrollView прокрутка;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        настройки = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            сервер = Server.Companion.fromJSON(getIntent().getStringExtra("server"));
        } else {
            сервер = Server.Companion.fromJSON(savedInstanceState.getString("server"));
        }
        setContentView(R.layout.activity_console);
        setTitle(R.string.title_activity_console);

        прокрутка = findViewById(R.id.console_scrollView);
        комманда = findViewById(R.id.console_cmd);
        ImageButton отправить = findViewById(R.id.console_send);
        консоль = findViewById(R.id.console_textView);
        консоль.setText(сервер.getконсоль());
        прокрутить_до_конца();
        комманда.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KEYCODE_ENTER) {
                    выполнить_комманду();
                }
                return false;
            }
        });
        if (сервер.getстатус() != 0) {
            комманда.setVisibility(View.VISIBLE);
            отправить.setVisibility(View.VISIBLE);
        } else {
            комманда.setVisibility(View.GONE);
            отправить.setVisibility(View.GONE);
        }
        AdView adView = findViewById(R.id.adView);
        задать_баннер(adView);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                //Выполняется после прогрузки рекламы
                прокрутить_до_конца();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

        });
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
                обновить();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void обновить() {
        Intent intent = new Intent(ConsoleActivity.this, UpdateActivity.class);
        intent.putExtra("server", сервер.toJSON());
        startActivityForResult(intent, 1);
    }

    private void прокрутить_до_конца() {
        if (настройки.getBoolean("settings_auto_scroll_console", true))
            прокрутка.post(new Runnable() {
                @Override
                public void run() {
                    прокрутка.fullScroll(View.FOCUS_DOWN); //Прокручиваем вывод консоли до конца.
                }
            });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("server", сервер.toJSON());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 0) {
                сервер = Server.Companion.fromJSON(data.getStringExtra("server"));
                консоль.setText(сервер.getконсоль());
                прокрутить_до_конца();
            }
        }
    }
}
