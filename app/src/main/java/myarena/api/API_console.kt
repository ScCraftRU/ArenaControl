package myarena.api

/**
 * Ответ MyArena.ru API на запрос о консоли сервера (Неофициальный метод - getconsole)
 */
class API_console : API_ответ() {

    private val console_log = "ERROR: Empty output"

    override fun toString(): String {
        return if (успех()) {
            console_log
        } else {
            message
        }
    }
}
