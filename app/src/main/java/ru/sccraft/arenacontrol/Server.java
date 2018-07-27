package ru.sccraft.arenacontrol;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
    }

    private void updateLocalServerData(API_info api_info) {
        this.id = Integer.parseInt(api_info.server_id);
        this.статус = api_info.online;
        this.ip = api_info.server_address;
        this.локация = api_info.server_location;
        this.тип = api_info.server_type;
        this.лимит_игроков = Integer.parseInt(api_info.server_maxslots);
        if (!api_info.data.s.name.equals("")) this.имя_сервера = api_info.data.s.name;
        this.дней_до_окончания_аренды = api_info.server_daystoblock;
        this.игра = api_info.server_name;
        this.игра_версия = api_info.data.e.version;
        this.плагины = api_info.data.e.plugins;

        if (api_info.data.p == null) return;
        игроки = new String[api_info.data.p.length];
        for (int i = 0; i < игроки.length; i++) {
            игроки[i] = api_info.data.p[i].name;
        }
    }

    private void updateLocalServerRes(API_res api_res) {
        this.процессор_в_процентах = api_res.cpu_proc;
        this.озу_использовано = api_res.mem_used;
        this.озу_всего = api_res.mem_quota;
        this.озу_в_процентах = api_res.mem_proc;
        this.диск_использовано = api_res.disk_used;
        this.диск_всего = api_res.disk_quota;
        this.диск_в_процентах = api_res.disk_proc;
        this.игроки_всего = api_res.players_max;
        this.игроки_на_сервере = api_res.players;
    }

    /**
     * Базовый класс запроса к MyArena.ru API
     */
    private class API_запрос {
        String query;
        String token;

        API_запрос() {
            this.token = токен;
        }

        /**
         * @return Возвращает GET-запрос, который нужно отправить на сервер MyArena.ru
         */
        String toHTTPs() {
            return baseURL + "query=" + query + "&token=" +  token;
        }
    }

    /**
     * Запрос для отправки консольных комманд на сервер
     */
    private class API_cmd extends API_запрос {
        String cmd;

        API_cmd() {
            this.query = "consolecmd";
        }

        @Override
        String toHTTPs() {
            String cmd = this.cmd;
            try {
                cmd = URLEncoder.encode(this.cmd,"UTF8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return baseURL + "query=" + query + "&cmd=" + cmd + "&token=" +  token;
        }
    }

    /**
     * Базовый класс ответа MyArena.ru API
     */
    private class API_ответ {
        String status;
        String message;

        boolean успех() {
            return status.equals("OK");
        }
    }

    /**
     * Ответ на запрос querty=status&token=ВАШ_ТОКЕН
     */
    private class API_info extends API_ответ {
        byte online; // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
        String server_id; //ID сервера на хостинге
        String server_name; //Название сервера
        String server_address; //IP и порт сервера
        String server_maxslots; //Максимальное возможное количество подключённых игроков
        String server_location; //Расположение сервера
        String server_type; //Тип сервера (Обычно публичный)
        String server_dateblock; //Сервер оплачен до...
        String server_daystoblock; //Дней до окончания аренды
        Data data;


        class Data {
            B b;
            S s;
            E e;
            Player[] p;
            Object[] t;

            class B {
                String type;
                String ip;
                String c_port;
                String q_port;
                String s_port;
                int status;
            }

            class S {
                String game;
                String name;
                String map;
                int players; //количество игроков
                int playersmax;
                Object password;
            }

            class E {
                String gametype;
                String game_id;
                String version; //Версия сервера
                String plugins; //Список плагинов
                String map;
                String hostport;
                String hostip;
            }

            class Player {
                String name;
            }
        }
    }

    /**
     * Ответ MyArena.ru API на запрос об использовании ресурсов
     */
    private class API_res extends API_ответ {
        byte cpu_proc;
        int mem_used;
        int mem_quota;
        byte mem_proc;
        int players;
        int players_max;
        byte players_proc;
        int disk_used;
        int disk_quota;
        byte disk_proc;
    }

    /**
     * Ответ MyArena.ru API на запрос о консоли сервера (Неофициальный метод - getconsole)
     */
    private class API_console extends API_ответ {
        String console_log;
    }

    /**
     * @return Обновляет все данные о сервере в приложении. Возращает True при удачном обновлении
     * False = Ошибка
     */
    public boolean update() {
        try {
            API_запрос api_запрос = new API_запрос();
            api_запрос.query = "status";
            String JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            JSON = JSON.replace("\"e\":[]", "\"e\":{}");
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            API_info api_info = gson.fromJson(JSON, API_info.class);
            updateLocalServerData(api_info);
            Log.i(LOG_TAG, "Основная информация обновлена!");

            api_запрос = new API_запрос();
            api_запрос.query = "getresources";
            JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            builder = new GsonBuilder();
            gson = builder.create();
            API_res api_res = gson.fromJson(JSON, API_res.class);
            updateLocalServerRes(api_res);
            api_запрос = new API_запрос();
            api_запрос.query = "getconsole";
            JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            API_console api_console = gson.fromJson(JSON,API_console.class);
            if (api_console.console_log != null) {
                this.консоль = api_console.console_log;
            }
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
                API_cmd api_cmd = new API_cmd();
                api_cmd.cmd = комманда;
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
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "start";
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
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "stop";
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
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "restart";
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
