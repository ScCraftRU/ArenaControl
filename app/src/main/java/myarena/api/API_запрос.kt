package myarena.api

/**
 * Базовый класс запроса к MyArena.ru API
 */
open class API_запрос(query: String, token: String) {
    internal var query = ""
    internal var token = ""

    init {
        this.query = query
        this.token = token
    }

    /**
     * @return Возвращает GET-запрос, который нужно отправить на сервер MyArena.ru
     */
    open fun toHTTPs(): String {
        return "${baseURL}query=$query&token=$token"
    }
}
