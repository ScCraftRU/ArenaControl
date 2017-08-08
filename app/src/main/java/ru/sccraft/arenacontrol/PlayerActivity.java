package ru.sccraft.arenacontrol;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.LinkedList;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {

    private Server сервер;
    private String игрок;
    private EditText сообщение;
    AdView adview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        сервер = Server.fromJSON(getIntent().getStringExtra("server"));
        игрок = getIntent().getStringExtra("name");
        setTitle(игрок);
        сообщение = (EditText) findViewById(R.id.player_message);
        adview = (AdView) findViewById(R.id.adView);
        показать_рекламу();
    }

    private void показать_рекламу() {
        Fe fe = new Fe(this);
        adview.setVisibility(View.GONE);
        String AD_DATA = fe.getFile("arenacontrol-ads");
        if (AD_DATA.contains("1")) return; //Для повышения вероятности работы покупки. Раньше использовался equals.
        if (получить_email().equals("sasha01945@gmail.com")) return;
        adview.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adview.loadAd(adRequest);
    }

    public String получить_email() {
        AccountManager manager = AccountManager.get(this);
        SharedPreferences myPreference= PreferenceManager.getDefaultSharedPreferences(this);
        Boolean fl = myPreference.getBoolean("disableADsByEmail", false);
        if (!fl) return "a@b.c";
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            запросить_разрешение();
            return "a@b.c";
        }
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            return email;
        }
        return "a@b.c";
    }

    private void запросить_разрешение() {
        ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.GET_ACCOUNTS}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                показать_рекламу();
                Toast.makeText(getApplicationContext(), "You are logined as " + получить_email(), Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    public void gameMode_survival(View view) {
        сервер.выполнить_комманду("gamemode 0 " + игрок);
    }

    public void gameMode_creative(View view) {
        сервер.выполнить_комманду("gamemode 1 " + игрок);
    }

    public void gameMode_gameMode_adventure(View view) {
        сервер.выполнить_комманду("gamemode 2 " + игрок);
    }

    public void gameMode_observer(View view) {
        сервер.выполнить_комманду("gamemode 3 " + игрок);
    }
}
