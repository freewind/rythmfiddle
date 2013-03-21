package models;

import com.google.gson.Gson;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;
import common.MyTemplateResourceLoader;

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

    public static RythmEngine rythm;

    public String id;
    public String desc;
    public String params;
    public List<CodeFile> files;

    public static void initRythmEngine() {
        Map<String, Object> conf = new HashMap();
        conf.put("resource.loader", new MyTemplateResourceLoader());
        conf.put("sandbox.timeout", 100000000);
        rythm = new RythmEngine(conf);
        // rythm.resourceManager().scan(null);
    }

    public boolean isNew() {
        return S.empty(id);
    }

    public String render() throws IOException {
        CodeFile main = getMainCodeFile();
        // FIXME? Is it correct to use a file? (which will make rythm use FileTemplateResource)
        File file = new File(main.filename);
        Map<String, Object> context = new HashMap();
        context.put("code", this);
        if (S.notEmpty(params)) {
            return rythm.sandbox(context).render(file, JSONWrapper.wrap(params));
        } else {
            return rythm.sandbox(context).render(file);
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

