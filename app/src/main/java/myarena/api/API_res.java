package myarena.api;

/**
 * Ответ MyArena.ru API на запрос об использовании ресурсов
 */
public class API_res extends API_ответ {

    public byte cpu_proc;
    public int mem_used;
    public int mem_quota;
    public byte mem_proc;
    public int players;
    public int players_max;
    public byte players_proc;
    public int disk_used;
    public int disk_quota;
    public byte disk_proc;
}
