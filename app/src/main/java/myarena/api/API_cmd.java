package myarena.api;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class API_cmd extends API_запрос {

    String cmd;

    API_cmd(String cmd, String token) {
        super("consolecmd", token);
        this.cmd = cmd;
    }

    @Override
    String toHTTPs() {
        String cmd = this.cmd;
        try {
            cmd = URLEncoder.encode(this.cmd,"UTF8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return baseURL + "query=" + query + "&cmd=" + cmd + "&token=" +  token;
    }
}
