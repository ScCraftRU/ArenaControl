package myarena.api

/**
 * Ответ MyArena.ru API на запрос об использовании ресурсов
 */
class API_res : API_ответ() {

    var cpu_proc: Byte = 0
    var mem_used = 0
    var mem_quota = 0
    var mem_proc: Byte = 0
    var players = 0
    var players_max = 0
    var players_proc: Byte = 0
    var disk_used = 0
    var disk_quota = 0
    var disk_proc: Byte = 0
}
