package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import myarena.api.API_cmd;
import myarena.api.API_console;
import myarena.api.API_info;
import myarena.api.API_res;
import myarena.api.API_запрос;
import myarena.api.API_ответ;

/**
 * Создан пользователем alexandr 01.06.2017 11:56, работающем в комманде ScCraft.
 *
 * Основной класс программы. Используется для работы с API
 */

public class Server {

    private static final String baseURL = "https://www.myarena.ru/api.php?";
    private static final String LOG_TAG = "Server";
    private String токен;
    int id;
    byte статус; // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
    String имя_сервера;
    String ip;
    String[] игроки;
    int игроки_на_сервере;
    int игроки_всего;
    int лимит_игроков;
    String локация;
    String тип;
    String игра;
    String игра_версия;
    String плагины;
    String консоль;

    String дата_окончания_аренды;
    String дней_до_окончания_аренды;

    byte процессор_в_процентах;
    int озу_использовано;
    int озу_всего;
    byte озу_в_процентах;
    int диск_использовано;
    int диск_всего;
    int диск_в_процентах;

    String комманда_день;
    String комманда_ночь;
    String комманда_задать_время;
    String комманда_добавить_время;
    String комманда_погода;
    boolean геймМод_1_13 = true;
    String комманда_релоад;

    public Server(String токен) {
        this.токен = токен;
        очистить_комманды();
    }

    String получить_токен() {
        return "" + токен;
    }

    int получить_id() {
        return id;
    }

    String получить_имя() {
        return "" + имя_сервера;
    }

    String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static Server fromJSON(String JSON) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(JSON, Server.class);
    }

    void очистить_комманды() {
        this.комманда_день = "time set day";
        this.комманда_ночь = "time set night";
        this.комманда_задать_время = "time set %time%";
        this.комманда_добавить_время = "time add %time%";
        this.комманда_погода = "weather %weather%";
        this.геймМод_1_13 = true;
        this.комманда_релоад = "reload";
    }

    private void updateLocalServerData(API_info api_info) {
        this.id = Integer.parseInt(api_info.getServer_id());
        this.статус = api_info.getOnline();
        this.ip = api_info.getServer_address();
        this.локация = api_info.getServer_location();
        this.тип = api_info.getServer_type();
        this.лимит_игроков = Integer.parseInt(api_info.getServer_maxslots());
        if (!api_info.getData().getS().getName().equals("")) this.имя_сервера = api_info.getData().getS().getName();
        this.дней_до_окончания_аренды = api_info.getServer_daystoblock();
        this.игра = api_info.getServer_name();
        this.игра_версия = api_info.getData().getE().getVersion();
        this.плагины = api_info.getData().getE().getPlugins();

        if (api_info.getData().getP() == null) return;
        игроки = new String[api_info.getData().getP().length];
        for (int i = 0; i < игроки.length; i++) {
            игроки[i] = api_info.getData().getP()[i].getName();
        }
    }

    private void updateLocalServerRes(API_res api_res) {
        this.процессор_в_процентах = api_res.getCpu_proc();
        this.озу_использовано = api_res.getMem_used();
        this.озу_всего = api_res.getMem_quota();
        this.озу_в_процентах = api_res.getMem_proc();
        this.диск_использовано = api_res.getDisk_used();
        this.диск_всего = api_res.getDisk_quota();
        this.диск_в_процентах = api_res.getDisk_proc();
        this.игроки_всего = api_res.getPlayers_max();
        this.игроки_на_сервере = api_res.getPlayers();
    }


    /**
     * @return Обновляет все данные о сервере в приложении. Возращает True при удачном обновлении
     * False = Ошибка
     */
    public boolean update() {
        try {
            API_запрос api_запрос = new API_запрос("status", токен);
            String JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            JSON = JSON.replace("\"e\":[]", "\"e\":{}");
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            API_info api_info = gson.fromJson(JSON, API_info.class);
            updateLocalServerData(api_info);
            Log.i(LOG_TAG, "Основная информация обновлена!");

            api_запрос = new API_запрос("getresources", токен);
            JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            builder = new GsonBuilder();
            gson = builder.create();
            API_res api_res = gson.fromJson(JSON, API_res.class);
            updateLocalServerRes(api_res);
            api_запрос = new API_запрос("getconsole", токен);
            JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            API_console api_console = gson.fromJson(JSON,API_console.class);
            this.консоль = api_console.toString();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void выполнить_комманду(final String комманда) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                API_cmd api_cmd = new API_cmd(комманда ,токен);
                NetGet.getOneLine(api_cmd.toHTTPs());
            }
        });
        t.start();
    }

    /**
     * Включает сервер
     */
    public void включить(final Операция_завершена операция_завершена) {
        @SuppressLint("StaticFieldLeak")
        class Поток extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... voids) {
                API_запрос api_запрос = new API_запрос("start", токен);
                String JSON = NetGet.getOneLine(api_запрос.toHTTPs());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                try {
                    API_ответ ответ = gson.fromJson(JSON, API_ответ.class);
                    return ответ.успех();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    операция_завершена.успешно();
                } else {
                    операция_завершена.ошибка();
                }
            }
        }
        Поток поток = new Поток();
        поток.execute();
    }

    /**
     * Выключает сервер
     */
    public void выключить(final Операция_завершена операция_завершена) {
        @SuppressLint("StaticFieldLeak")
        class Поток extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... voids) {
                API_запрос api_запрос = new API_запрос("stop", токен);
                String JSON = NetGet.getOneLine(api_запрос.toHTTPs());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                try {
                    API_ответ ответ = gson.fromJson(JSON, API_ответ.class);
                    return ответ.успех();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    операция_завершена.успешно();
                } else {
                    операция_завершена.ошибка();
                }
            }
        }
        Поток поток = new Поток();
        поток.execute();
    }

    /**
     * Перезагружает сервер
     */
    public void перезагрузить(final Операция_завершена операция_завершена) {
        class Поток extends AsyncTask<Void, Void, Boolean> {

            @Override
            protected Boolean doInBackground(Void... voids) {
                API_запрос api_запрос = new API_запрос("restart", токен);
                String JSON = NetGet.getOneLine(api_запрос.toHTTPs());

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                try {
                    API_ответ ответ = gson.fromJson(JSON, API_ответ.class);
                    return ответ.успех();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    операция_завершена.успешно();
                } else {
                    операция_завершена.ошибка();
                }
            }
        }
        Поток поток = new Поток();
        поток.execute();
    }

    public interface Операция_завершена {
        void успешно();
        void ошибка();
    }
}
