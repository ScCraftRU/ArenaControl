package myarena.api;

public class API_info extends API_ответ {

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
