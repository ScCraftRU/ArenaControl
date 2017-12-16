package ru.sccraft.arenacontrol;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

public class ConsoleActivity extends ADsActivity {

    Server сервер;
    TextView консоль;
    EditText комманда;
    ImageButton отправить;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        setContentView(R.layout.activity_console);
        setTitle(R.string.title_activity_console);
        комманда = findViewById(R.id.console_cmd);
        отправить = findViewById(R.id.console_send);
        консоль = findViewById(R.id.console_textView);
        консоль.setText(сервер.консоль);
        AdView adView = findViewById(R.id.adView);
        задать_баннер(adView);
    }

    public void send(View view) {
        String command = комманда.getText().toString();
        сервер.выполнить_комманду(command);
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
        Поток поток = new Поток();
        поток.execute();
    }

    class Поток extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return сервер.update();
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                консоль.setText(сервер.консоль);
            }
        }
    }
}
