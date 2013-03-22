package models;

import com.google.gson.Gson;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;
import play.mvc.Scope;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static common.Helper.eq;

/**
 * User: freewind
 * Date: 13-3-18
 * Time: 下午6:07
 */
public class Code {

    public String id;
    public String desc;
    public String params;
    public List<CodeFile> files;
    public static RythmEngine engine; static {
//        Map<String, Object> conf = new HashMap();
//        conf.put("resource.loader", new MyTemplateResourceLoader(this));
//        engine = new RythmEngine(conf);
    }
    
    public boolean isNew() {
        return S.empty(id);
    }

    public String render() throws IOException {
        engine.resourceManager().scan(null);
        CodeFile main = getMainCodeFile();
        File file = new File(id + "." + main.filename);
        Map<String, Object> ctx = new HashMap<String, Object>();
        ctx.put("sessionid", Scope.Session.current().getId());
        if (S.notEmpty(params)) {
            return engine.sandbox(ctx).render(file, JSONWrapper.wrap(params));
        } else {
            return engine.sandbox(ctx).render(file);
        }
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

    public CodeFile getMainCodeFile() {
        for (CodeFile file : files) {
            if (file.isMain) {
                return file;
            }
        }
        return files.get(0);
    }

    public CodeFile findCodeFile(String path) {
        // find by fullname first
        for (CodeFile file : files) {
            if (eq(file.filename, path)) {
                return file;
            }
        }
        // find by prefix
        for (CodeFile file : files) {
            if (file.filename.startsWith(path + ".")) {
                return file;
            }
        }
        return null;
    }
}

