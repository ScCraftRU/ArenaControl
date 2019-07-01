package myarena.api;

public class API_ответ {

    public String status;
    public String message;

    boolean успех() {
        return status.equals("OK");
    }
}
