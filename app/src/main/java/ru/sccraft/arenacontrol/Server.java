package ru.sccraft.arenacontrol;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by alexandr on 01.06.17.
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

    public Server(String токен) {
        this.токен = токен;
        this.комманда_день = "time set day";
        this.комманда_ночь = "time set night";
    }

    public String getToken() {
        return "" + токен;
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.toJson(this);
    }

    public static Server fromJSON(String JSON) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(JSON, Server.class);
    }

    private void updateLocalServerData(API_info api_info) {
        this.id = Integer.parseInt(api_info.server_id);
        this.статус = api_info.online;
        this.ip = api_info.server_address;
        this.локация = api_info.server_location;
        this.тип = api_info.server_type;
        this.лимит_игроков = Integer.parseInt(api_info.server_maxslots);
        this.имя_сервера = api_info.data.s.name;
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

    private class API_запрос {
        String query;
        String token;

        API_запрос() {
            this.token = токен;
        }

        String toHTTPs() {
            return baseURL + "query=" + query + "&token=" +  token;
        }
    }

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

    private class API_ответ {
        String status;
        String message;

        boolean успех() {
            return status.equals("OK");
        }
    }

    private class API_info extends API_ответ {
        byte online; // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
        String server_id;
        String server_name;
        String server_address;
        String server_maxslots;
        String server_location;
        String server_type;
        String server_dateblock;
        String server_daystoblock;
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
                int password;
            }

            class E {
                String gametype;
                String game_id;
                String version;
                String plugins;
                String map;
                String hostport;
                String hostip;
            }

            class Player {
                String name;
            }
        }
    }

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

    public boolean update() {
        try {
            API_запрос api_запрос = new API_запрос();
            api_запрос.query = "status";
            String JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            try {
                API_info api_info = gson.fromJson(JSON, API_info.class);
                updateLocalServerData(api_info);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                this.статус = 0;
                this.игроки = new String[0];
            }
            Log.i(LOG_TAG, "Основная информация обновлена!");

            api_запрос = new API_запрос();
            api_запрос.query = "getresources";
            JSON = NetGet.getOneLine(api_запрос.toHTTPs());
            Log.i(LOG_TAG, "Ответ сервера MyArena: " + JSON);
            builder = new GsonBuilder();
            gson = builder.create();
            API_res api_res = gson.fromJson(JSON, API_res.class);
            updateLocalServerRes(api_res);
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

    public void включить() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "start";
                NetGet.getOneLine(api_запрос.toHTTPs());
            }
        });
        t.start();
    }

    public void выключить() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "stop";
                NetGet.getOneLine(api_запрос.toHTTPs());
            }
        });
        t.start();
    }

    public void перезагрузить() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                API_запрос api_запрос = new API_запрос();
                api_запрос.query = "restart";
                NetGet.getOneLine(api_запрос.toHTTPs());
            }
        });
        t.start();
    }
}
