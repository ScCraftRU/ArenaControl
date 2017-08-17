package ru.sccraft.arenacontrol;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by alexandr on 16.08.17.
 * Activity с рекламным баннером AdMob
 */

public class ADsActivity extends AppCompatActivity {
    private AdView adView;
    Fe fe;

    private void показать_рекламу() {
        adView.setVisibility(View.GONE);
        String AD_DATA = fe.getFile("arenacontrol-ads");
        if (AD_DATA.contains("1")) return; //Для повышения вероятности работы покупки. Раньше использовался equals.
        if (получить_email().equals("sasha01945@gmail.com")) return;
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
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
        AlertDialog.Builder ad;
        ad = new AlertDialog.Builder(ADsActivity.this);
        ad.setTitle(R.string.devMode_enabled);  // заголовок
        ad.setMessage(R.string.devMode_reqest); // сообщение
        ad.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                ActivityCompat.requestPermissions(ADsActivity.this, new String[] {android.Manifest.permission.GET_ACCOUNTS}, 1);
            }
        });
        ad.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            }
        });
        ad.show();
    }

    void задать_баннер(AdView баннер) {
        this.adView = баннер;
        показать_рекламу();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fe = new Fe(this);
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
}
