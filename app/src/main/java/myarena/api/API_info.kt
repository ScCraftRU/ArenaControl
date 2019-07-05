package myarena.api

/**
 * Ответ на запрос querty=status&token=ВАШ_ТОКЕН
 * получение информации о сервере
 */
class API_info : API_ответ() {

    var online: Byte = 0 // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
    var server_id = "0" //ID сервера на хостинге
    var server_name = "ERROR: Server offline or this API used incorrectly!" //Название сервера
    var server_address = "127.0.0.1:25565" //IP и порт сервера
    var server_maxslots = "0" //Максимальное возможное количество подключённых игроков
    var server_location = "ERROR: API used Incorrectly" //Расположение сервера
    var server_type = "Неизвестно" //Тип сервера (Обычно публичный)
    var server_dateblock = "" //Сервер оплачен до...
    var server_daystoblock = "0" //Дней до окончания аренды
    var data: Data? = null


    inner class Data {
        var b: B? = null
        var s: S? = null
        var e: E? = null
        var p: Array<Player>? = null
        var t: Array<Any>? = null

        inner class B {
            var type = "Ulcnown"
            var ip = "127.0.0.1" //IP сервера без порта
            var c_port: String? = null
            var q_port: String? = null
            var s_port: String? = null
            var status: Int = 0
        }

        inner class S {
            var game: String = ""
            var name = ""
            var map = "Ulcnown" //Карта (Название мира)
            var players = 0 //количество игроков
            var playersmax = 0
            var password: Any? = null //Не использовать!!!
        }

        inner class E {
            var gametype = "Ulcnown"
            var game_id = "Ulcnown" //Название игры
            var version: String? = null //Версия сервера
            var plugins: String? = null //Список плагинов
            var map = "Ulcnown" //Карта
            var hostport = "25565" //Порт игрового сервера сервера
            var hostip = "127.0.0.1" //IP без порта
        }

        inner class Player {
            var name = "Ulcnown" //Ник игрока на сервере
            var score =  0 //Счёт игрока (Не для Minecraft)
        }
    }
}
