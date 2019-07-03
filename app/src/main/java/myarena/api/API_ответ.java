package myarena.api;

/**
 * Базовый класс ответа MyArena.ru API
 */
public class API_ответ {

    public String status;
    public String message;

    public boolean успех() {
        return status.equals("OK");
    }

    @Override
    public String toString() {
        return message;
    }
}
