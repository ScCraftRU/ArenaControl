package ru.sccraft.arenacontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class AddServerActivity extends AppCompatActivity {

    EditText токен, имя;
    Server сервер;
    Fe fe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        setTitle(R.string.title_activity_server_add);
        токен = (EditText) findViewById(R.id.addServer_token);
        имя = (EditText) findViewById(R.id.addServer_name);
        fe = new Fe(this);
    }

    public void save(View view) {
        String токен = this.токен.getText().toString();
        сервер = new Server(токен);
        сервер.имя_сервера = имя.getText().toString();
        fe.saveFile(токен + ".json", сервер.toJSON());
        finish();
    }
}
