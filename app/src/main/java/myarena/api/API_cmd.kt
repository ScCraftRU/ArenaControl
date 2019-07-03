package myarena.api

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**
 * Запрос для отправки консольных комманд на сервер
 */
class API_cmd(cmd: String, token: String) : API_запрос("consolecmd", token) {

    internal var cmd = "say Administrator using the unofficial API incorrettly!"

    init {
        this.cmd = cmd
    }

    override fun toHTTPs(): String {
        var cmd = this.cmd
        try {
            cmd = URLEncoder.encode(this.cmd, "UTF8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return "${baseURL}query=$query&cmd=$cmd&token=$token"
    }
}
