package ru.sccraft.arenacontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ConsoleActivity extends AppCompatActivity {

    Server сервер;
    EditText комманда;
    ImageButton отправить;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        setContentView(R.layout.activity_console);
        setTitle(R.string.title_activity_console);
        комманда = (EditText) findViewById(R.id.console_cmd);
        отправить = (ImageButton) findViewById(R.id.console_send);

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public void send(View view) {
        String command = комманда.getText().toString();
        сервер.выполнить_комманду(command);
    }
}
