package myarena.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Запрос для отправки консольных комманд на сервер
 */
public class API_cmd extends API_запрос {

    String cmd;

    public API_cmd(String cmd, String token) {
        super("consolecmd", token);
        this.cmd = cmd;
    }

    @Override
    public String toHTTPs() {
        String cmd = this.cmd;
        try {
            cmd = URLEncoder.encode(this.cmd,"UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return baseURL + "query=" + query + "&cmd=" + cmd + "&token=" +  token;
    }
}
