package ru.sccraft.arenacontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class PlayerActivity extends AppCompatActivity {

    private Server сервер;
    private String игрок;
    private EditText сообщение;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        игрок = getIntent().getStringExtra("name");
        setTitle(игрок);
        сообщение = (EditText) findViewById(R.id.player_message);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void kick(View view) {
        String сообщение = this.сообщение.getText().toString();
        if (сообщение.equals("")) {
            сервер.выполнить_комманду("kick " + игрок + " " + getString(R.string.player_kickByApp));
        } else {
            сервер.выполнить_комманду("kick " + игрок + " " + сообщение);
        }
    }

    public void ban(View view) {
        String сообщение = this.сообщение.getText().toString();
        if (сообщение.equals("")) {
            сервер.выполнить_комманду("ban " + игрок + " " + getString(R.string.player_kickByApp));
        } else {
            сервер.выполнить_комманду("ban " + игрок + " " + сообщение);
        }
    }

    public void op(View view) {
        сервер.выполнить_комманду("op " + игрок);
    }

    public void deOP(View view) {
        сервер.выполнить_комманду("deop " + игрок);
    }

    public void kill(View view) {
        сервер.выполнить_комманду("kill " + игрок);
    }
}
