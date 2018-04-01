package ru.sccraft.arenacontrol;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class UpdateActivity extends AppCompatActivity {

    private Server сервер;
    private Fe fe;
    private Поток поток;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        fe = new Fe(this);
        сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        поток = (Поток) getLastCustomNonConfigurationInstance();
        if (поток == null) {
            Поток поток = new Поток(this);
        } else {
            поток.link(this);
        }
    }

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
}
