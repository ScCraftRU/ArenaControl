package ru.sccraft.arenacontrol

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import myarena.api.*

/**
 * Создан пользователем alexandr 01.06.2017 11:56, работающем в комманде ScCraft.
 *
 * Основной класс программы. Используется для работы с API
 */

class Server(private val токен: String) {
    var id: Int = 0
    var статус: Byte = 0 // 0=сервер offline, 1=сервер online, 2=сервер запускается или завис
    var имя_сервера = "Ulcnown"
    var ip = "127.0.0.1:25565"
    var игроки: Array<String>? = null
    var игроки_на_сервере = 0
    var игроки_всего = 0
    var лимит_игроков = 0
    var локация = "Ulcnown"
    var тип = "Ulcnown"
    var игра = "Ulcnown"
    var игра_версия: String? = "Ulcnown"
    var плагины: String? = ""
    var консоль = "ERROR"

    var дата_окончания_аренды = ""
    var дней_до_окончания_аренды = ""

    var процессор_в_процентах: Byte = 0
    var озу_использовано = 0
    var озу_всего = 0
    var озу_в_процентах: Byte = 0
    var диск_использовано = 0
    var диск_всего = 0
    var диск_в_процентах: Byte = 0

    var комманда_день = ""
    var комманда_ночь = ""
    var комманда_задать_время = ""
    var комманда_добавить_время = ""
    var комманда_погода = ""
    var геймМод_1_13 = true
    var комманда_релоад = ""

    init {
        очистить_комманды()
    }

    fun получить_токен(): String {
        return "" + токен
    }

    fun получить_id(): Int {
        return id
    }

    fun получить_имя(): String {
        return "" + имя_сервера
    }

    fun toJSON(): String {
        val gson = Gson()
        return gson.toJson(this)
    }

    fun очистить_комманды() {
        this.комманда_день = "time set day"
        this.комманда_ночь = "time set night"
        this.комманда_задать_время = "time set %time%"
        this.комманда_добавить_время = "time add %time%"
        this.комманда_погода = "weather %weather%"
        this.геймМод_1_13 = true
        this.комманда_релоад = "reload"
    }

    private fun updateLocalServerData(api_info: API_info) {
        this.id = Integer.parseInt(api_info.server_id)
        this.статус = api_info.online
        this.ip = api_info.server_address
        this.локация = api_info.server_location
        this.тип = api_info.server_type
        this.лимит_игроков = Integer.parseInt(api_info.server_maxslots)
        if (api_info.data!!.s!!.name != "") this.имя_сервера = api_info.data!!.s!!.name
        this.дней_до_окончания_аренды = api_info.server_daystoblock
        this.игра = api_info.server_name
        this.игра_версия = api_info.data!!.e!!.version
        this.плагины = api_info.data!!.e!!.plugins

        if (api_info.data!!.p == null) return
        val mSize = api_info.data!!.p!!.size
        игроки = Array<String>(mSize, {""})
        for (i in игроки!!.indices) {
            игроки!![i] = api_info.data!!.p!![i].name
        }
    }

    private fun updateLocalServerRes(api_res: API_res) {
        this.процессор_в_процентах = api_res.cpu_proc
        this.озу_использовано = api_res.mem_used
        this.озу_всего = api_res.mem_quota
        this.озу_в_процентах = api_res.mem_proc
        this.диск_использовано = api_res.disk_used
        this.диск_всего = api_res.disk_quota
        this.диск_в_процентах = api_res.disk_proc
        this.игроки_всего = api_res.players_max
        this.игроки_на_сервере = api_res.players
    }


    /**
     * @return Обновляет все данные о сервере в приложении. Возращает True при удачном обновлении
     * False = Ошибка
     */
    fun update(): Boolean {
        try {
            var api_запрос = API_запрос("status", токен)
            var JSON = NetGet.getOneLine(api_запрос.toHTTPs())
            Log.i(LOG_TAG, "Ответ сервера MyArena: $JSON")
            val api_info = API_info.fromJSON(JSON)
            updateLocalServerData(api_info)
            Log.i(LOG_TAG, "Основная информация обновлена!")

            api_запрос = API_запрос("getresources", токен)
            JSON = NetGet.getOneLine(api_запрос.toHTTPs())
            Log.i(LOG_TAG, "Ответ сервера MyArena: $JSON")
            val api_res = API_res.fromJSON(JSON)
            updateLocalServerRes(api_res)
            api_запрос = API_запрос("getconsole", токен)
            JSON = NetGet.getOneLine(api_запрос.toHTTPs())
            val api_console = API_console.fromJSON(JSON)
            this.консоль = api_console.toString()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun выполнить_комманду(комманда: String) {
        val t = Thread(Runnable {
            val api_cmd = API_cmd(комманда, токен)
            NetGet.getOneLine(api_cmd.toHTTPs())
        })
        t.start()
    }

    /**
     * Включает сервер
     */
    fun включить(операция_завершена: Операция_завершена) {
        @SuppressLint("StaticFieldLeak")
        class Поток : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg voids: Void): Boolean? {
                val api_запрос = API_запрос("start", токен)
                val JSON = NetGet.getOneLine(api_запрос.toHTTPs())

                val builder = GsonBuilder()
                val gson = builder.create()
                try {
                    val ответ = gson.fromJson(JSON, API_ответ::class.java)
                    return ответ.успех()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    return false
                }

            }

            override fun onPostExecute(aBoolean: Boolean?) {
                super.onPostExecute(aBoolean)
                if (aBoolean!!) {
                    операция_завершена.успешно()
                } else {
                    операция_завершена.ошибка()
                }
            }
        }

        val поток = Поток()
        поток.execute()
    }

    /**
     * Выключает сервер
     */
    fun выключить(операция_завершена: Операция_завершена) {
        @SuppressLint("StaticFieldLeak")
        class Поток : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg voids: Void): Boolean? {
                val api_запрос = API_запрос("stop", токен)
                val JSON = NetGet.getOneLine(api_запрос.toHTTPs())

                val builder = GsonBuilder()
                val gson = builder.create()
                try {
                    val ответ = gson.fromJson(JSON, API_ответ::class.java)
                    return ответ.успех()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    return false
                }

            }

            override fun onPostExecute(aBoolean: Boolean?) {
                super.onPostExecute(aBoolean)
                if (aBoolean!!) {
                    операция_завершена.успешно()
                } else {
                    операция_завершена.ошибка()
                }
            }
        }

        val поток = Поток()
        поток.execute()
    }

    /**
     * Перезагружает сервер
     */
    fun перезагрузить(операция_завершена: Операция_завершена) {
        class Поток : AsyncTask<Void, Void, Boolean>() {

            override fun doInBackground(vararg voids: Void): Boolean? {
                val api_запрос = API_запрос("restart", токен)
                val JSON = NetGet.getOneLine(api_запрос.toHTTPs())

                val builder = GsonBuilder()
                val gson = builder.create()
                try {
                    val ответ = gson.fromJson(JSON, API_ответ::class.java)
                    return ответ.успех()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                    return false
                }

            }

            override fun onPostExecute(aBoolean: Boolean?) {
                super.onPostExecute(aBoolean)
                if (aBoolean!!) {
                    операция_завершена.успешно()
                } else {
                    операция_завершена.ошибка()
                }
            }
        }

        val поток = Поток()
        поток.execute()
    }

    interface Операция_завершена {
        fun успешно()
        fun ошибка()
    }

    companion object {

        private val LOG_TAG = "Server"

        fun fromJSON(JSON: String): Server {
            val builder = GsonBuilder()
            val gson = builder.create()
            return gson.fromJson(JSON, Server::class.java)
        }
    }
}
