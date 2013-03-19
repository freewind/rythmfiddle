package controllers;

import common.Helper;
import models.Code;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.mvc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Helper.eq;
import static org.apache.commons.lang.StringUtils.*;

public class Application extends Controller {

    private static final File DATA = Play.getFile("data");

    static {
        DATA.mkdirs();
    }


    public static void index() {
        render();
    }

    public static void angularIndex() {
        renderBinary(Play.getFile("angular-seed/app/index.html"));
    }

    public static void run(String body) throws IOException {
        Code code = Helper.parse2code(body);

        try {
            if (isNotBlank(code.source)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("htmlCode", code.render());
                map.put("javaCode", code.getJavaCode());
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
        Code code = loadCode(id);
        renderJSON(code);
    }

    public static void delete(String body) {
        String id = (String) Helper.parseJson(body).get("id");
        if (isNotBlank(id)) {
            File file = getCodeFile(id);
            file.delete();
        }
        ok();
    }

    public static void save(String body) throws IOException {
        checkLogin();
        Code code = Helper.parse2code(body);

        String targetId = code.isNew() ? Helper.nextUUID() : code.id;
        code.id = targetId;
        File target = Play.getFile("data/" + targetId + ".json");
        FileUtils.writeStringToFile(target, code.toJson());

        renderJSON(code);
    }

    public static void list() throws IOException {
        List<Map> codes = new ArrayList();
        for (String filename : DATA.list()) {
            if (filename.endsWith(".json")) {
                String id = StringUtils.substringBeforeLast(filename, ".json");
                Code code = loadCode(id);
                Map<String, String> map = new HashMap();
                map.put("id", code.id);
                map.put("desc", code.desc);
                codes.add(map);
            }
        }
        renderJSON(codes);
    }

    private static Code loadCode(String id) throws IOException {
        File file = getCodeFile(id);
        String content = FileUtils.readFileToString(file, "UTF-8");
        return Helper.parse2code(content);
    }

    // just use the simplest solution for login
    public static void login(String body) throws IOException {
        Map<String, Object> map = Helper.parseJson(body);
        String username = (String) map.get("username");
        String password = (String) map.get("password");

        Map<String, String> accounts = getAccounts();
        if (isNotBlank(username) && eq(accounts.get(username), password)) {
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

    private static File getCodeFile(String id) {
        return new File(DATA, id + ".json");
    }

}