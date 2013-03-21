package controllers;

import com.greenlaw110.rythm.utils.S;
import common.CodeManager;
import common.Helper;
import models.Code;
import models.CodeFile;
import org.apache.commons.io.FileUtils;
import play.Play;
import play.mvc.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void angularIndex() {
        renderBinary(Play.getFile("angular-seed/app/index.html"));
    }

    public static void run(String body) throws IOException {
        Code code = Helper.parse2code(body);

        try {
            CodeFile mainCodeFile = code.getMainCodeFile();
            if (isNotBlank(mainCodeFile.source)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("renderedCode", code.render());
                renderJSON(map);
            } else {
                renderJSON("{}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> map = new HashMap<String, String>();
            map.put("error", e.toString());
            renderJSON(map);
        }
    }

    public static void load(String id) throws IOException {
        renderJSON(CodeManager.findById(id));
    }

    public static void delete(String body) throws IOException {
        String id = (String) Helper.parseJson(body).get("id");
        if (S.notEmpty(id)) {
            CodeManager.deleteById(id);
        }
        ok();
    }

    public static void save(String body) throws IOException {
        checkLogin();
        Code code = Helper.parse2code(body);

        CodeManager.saveOrUpdate(code);
        renderJSON(code);
    }

    public static void list() throws IOException {
        List<Map> codes = new ArrayList();
        for (Code code : CodeManager.getAll().values()) {
            Map<String, String> map = new HashMap();
            map.put("id", code.id);
            map.put("desc", code.desc);
            codes.add(map);
        }
        renderJSON(codes);
    }


    // just use the simplest solution for login
    public static void login(String body) throws IOException {
        Map<String, Object> map = Helper.parseJson(body);
        String username = (String) map.get("username");
        String password = (String) map.get("password");

        Map<String, String> accounts = getAccounts();
        if (S.notEmpty(username) && S.eq(accounts.get(username), password)) {
            session.put("username", username);
            renderJSON("{}");
        }
        renderJSON("{\"error\": \"Invalid username or password\"}");
    }

    private static void checkLogin() throws IOException {
        boolean ok = getAccounts().containsKey(session.get("username"));
        if (!ok) {
            forbidden("Please login first");
        }
    }

    private static Map<String, String> getAccounts() throws IOException {
        Map<String, String> map = new HashMap();
        List<String> lines = FileUtils.readLines(Play.getFile("conf/account.txt"));
        for (String line : lines) {
            String[] items = line.split("=");
            map.put(items[0], items[1]);
        }
        return map;
    }

    public static void current() {
        renderText(session.get("username"));
    }

}