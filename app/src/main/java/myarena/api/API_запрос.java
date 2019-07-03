package myarena.api;

/**
 * Базовый класс запроса к MyArena.ru API
 */
public class API_запрос {
    String query;
    String token;
    protected final String baseURL = "https://www.myarena.ru/api.php?";

    public API_запрос(String query, String token) {
        this.query = query;
        this.token = token;
    }

    /**
     * @return Возвращает GET-запрос, который нужно отправить на сервер MyArena.ru
     */
    public String toHTTPs() {
        return baseURL + "query=" + query + "&token=" +  token;
    }
}
