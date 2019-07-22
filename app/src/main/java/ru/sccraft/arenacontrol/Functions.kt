package ru.sccraft.arenacontrol

import java.net.URL

fun getOneLineHTTPs(webadress: String): String {
    try {
        return URL(webadress).readText()
    } catch (e: Exception) {
        e.printStackTrace()
        return "{\"status\":\"ERROR\",\"message\":\"Can't send HTTPs GET request. Не удалось отправить HTTPs GET запрос\"}"
    }
}