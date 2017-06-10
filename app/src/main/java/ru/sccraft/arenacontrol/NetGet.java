package ru.sccraft.arenacontrol;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by alexandr on 02.06.17.
 */

public class NetGet {
    private static final String LOG_TAG = "NetGet";

    public static String getOneLine(String webAdress) {
        URL url;
        HttpsURLConnection conn = null;
        BufferedReader rd;
        String line;
        String result = "";
        try {
            url = new URL(webAdress);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            int responseCode = conn.getResponseCode();
            Log.i(LOG_TAG, "Код ответа сервера (RESPONSE CODE) = " + responseCode);
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result += line;
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.disconnect();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
