package myarena.api

import com.google.gson.GsonBuilder

/**
 * Базовый класс ответа MyArena.ru API
 */
open class API_ответ {

    var status = "NOT_SET"
    var message = "No answer from server or you are not use this correctly!"

    fun успех(): Boolean {
        return status == "OK"
    }

    override fun toString(): String {
        return message
    }

    companion object {
        fun fromJSON(JSON: String): API_ответ {
            val builder = GsonBuilder()
            val gson = builder.create()
            return gson.fromJson(JSON, API_ответ::class.java)
        }
    }
}
