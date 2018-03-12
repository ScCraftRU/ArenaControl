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
        adView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    protected final void задать_баннер(AdView баннер) {
        this.adView = баннер;
        показать_рекламу();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fe = new Fe(this);
    }
}
