package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

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
        MobileAds.initialize(this, getString(R.string.admob_appid));
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
