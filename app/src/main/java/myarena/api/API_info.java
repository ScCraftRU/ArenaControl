package myarena.api;

/**
 * Ответ на запрос querty=status&token=ВАШ_ТОКЕН
 * получение информации о сервере
 */
public class API_info extends API_ответ {

    public byte online; // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
    public String server_id; //ID сервера на хостинге
    public String server_name; //Название сервера
    public String server_address; //IP и порт сервера
    public String server_maxslots; //Максимальное возможное количество подключённых игроков
    public String server_location; //Расположение сервера
    public String server_type; //Тип сервера (Обычно публичный)
    public String server_dateblock; //Сервер оплачен до...
    public String server_daystoblock; //Дней до окончания аренды
    public Data data;


    public class Data {
        public B b;
        public S s;
        public E e;
        public Player[] p;
        public Object[] t;

        public class B {
            public String type;
            public String ip;
            public String c_port;
            public String q_port;
            public String s_port;
            public int status;
        }

        public class S {
            public String game;
            public String name;
            public String map;
            public int players; //количество игроков
            public int playersmax;
            public Object password;
        }

        public class E {
            public String gametype;
            public String game_id;
            public String version; //Версия сервера
            public String plugins; //Список плагинов
            public String map;
            public String hostport;
            public String hostip;
        }

        public class Player {
            public String name;
        }
    }
}
