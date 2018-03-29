package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by alexandr on 16.08.17.
 * Activity с рекламным баннером AdMob
 */

@SuppressLint("Registered")
public class ADsActivity extends AppCompatActivity {
    private AdView adView;
    protected Fe fe;

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
