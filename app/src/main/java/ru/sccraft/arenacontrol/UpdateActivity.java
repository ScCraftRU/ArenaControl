package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private Server сервер;
    private Fe fe;
    private Поток поток;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        setTitle(R.string.pleaseWait);
        fe = new Fe(this);
        сервер = Server.Companion.fromJSON(getIntent().getStringExtra("server"));
        if (сервер == null) {
            setResult(-1);
            finish();
        }
        поток = (Поток) getLastCustomNonConfigurationInstance();
        if (поток == null) {
            поток = new Поток(this);
        } else {
            поток.link(this);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class Поток extends AsyncTask<Server, Void, Boolean> {

        UpdateActivity активность;

        Поток (UpdateActivity activity) {
            активность = activity;
            execute(сервер);
        }

        void link(UpdateActivity activity) {
            активность = activity;
        }

        @Override
        protected Boolean doInBackground(Server... сервер1) {
            Server сервер = сервер1[0];
            Boolean b = сервер.update();
            активность.fe.saveFile(активность.сервер.получить_токен() + ".json", активность.сервер.toJSON());
            return b;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (!aBoolean) {
                Toast.makeText(активность.getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent();
            intent.putExtra("server", активность.сервер.toJSON());
            setResult(0, intent);
            finish();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return поток;
    }

    @Override
    public void onBackPressed() {
    }
}
