package controllers;

import com.google.gson.Gson;
import com.greenlaw110.rythm.utils.S;
import common.CodeManager;
import common.Helper;
import models.Code;
import models.CodeFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;
import play.mvc.Controller;
import play.mvc.Scope;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void angularIndex() {
        renderBinary(Play.getFile("angular-seed/app/index.html"));
    }

    public static void embedIndex() {
        renderBinary(Play.getFile("angular-seed/app/embed.html"));
    }

    public static void run(String body) throws IOException {
        Code code = Helper.parse2code(body);
        code.save(Scope.Session.current().getId());
        try {
            CodeFile mainCodeFile = code.getMainCodeFile();
            if (S.notEmpty(mainCodeFile.source)) {
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

    /**
     * @param showInMenu if is null, return all, otherwise return matched
     */
    public static void list(Boolean showInMenu) throws IOException {
        List<Map> codes = new ArrayList();
        for (Code code : CodeManager.getAll().values()) {
            Map<String, Object> map = new HashMap();
            map.put("id", code.id);
            map.put("desc", code.desc);
            map.put("showInMenu", code.showInMenu);
            if (showInMenu == null || showInMenu == code.showInMenu) {
                codes.add(map);
            }
        }
        renderJSON(codes);
    }

    public static class Credential {
        public String username;
        public String password;
    }

    // just use the simplest solution for login
    public static void login(String body) throws IOException {
        //Map<String, Object> map = Helper.parseJson(body);
        Credential login = new Gson().fromJson(body, Credential.class);

        Map<String, String> accounts = getAccounts();
        if (S.notEmpty(login.username) && S.eq(accounts.get(login.username), login.password)) {
            session.put("username", login.username);
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

    public static void siteInfo() {
        Map<String, Object> data = new HashMap();
        data.put("rythmWebsite", Play.configuration.get("site.rythmWebsite"));
        data.put("rythmfiddleGithub", Play.configuration.get("site.rythmfiddleGithub"));
        renderJSON(data);
    }

    public static void sampleModelCode() throws IOException {
        List<Map<String, String>> codes = new ArrayList();
        for (File file : Play.getFile("app/demo").listFiles()) {
            String content = FileUtils.readFileToString(file, "UTF-8");
            Map<String, String> map = new HashMap();
            map.put("className", "demo." + StringUtils.substringBefore(file.getName(), "."));
            map.put("source", content);
            codes.add(map);
        }
        renderJSON(codes);
    }

}