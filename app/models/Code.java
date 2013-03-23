package models;

import com.google.gson.Gson;
import com.greenlaw110.play.SessionCache;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;
import play.mvc.Scope;

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

    public boolean isNew() {
        return S.empty(id);
    }

    private static final Object lock = new Object();

    private static final Map<String, RythmEngine> engines = new HashMap<String, RythmEngine>();
    
    private RythmEngine engine() {
        String sessId = Scope.Session.current().getId();
        synchronized (lock) {
            RythmEngine e = engines.get(sessId);
            if (null == e) {
                Map<String, Object> conf = new HashMap<String, Object>();
                conf.put("resource.loader", new InMemoryResourceLoader(sessId));
                conf.put("default.code_type", ICodeType.DefImpl.HTML);
                conf.put("engine.mode", Rythm.Mode.dev);
                e = new RythmEngine(conf);
                engines.put(sessId, e);
            }
            return e;
        }
    }

    public String render() throws IOException {
        CodeFile main = getMainCodeFile();
        // FIXME? Is it correct to use a file? (which will make rythm use FileTemplateResource)
        Map<String, Object> context = new HashMap();
        context.put("session-id", Scope.Session.current().getId());
        RythmEngine rythm = engine();
        if (S.notEmpty(params)) {
            return rythm.sandbox(context).render(main.getKey(), JSONWrapper.wrap(params));
        } else {
            return rythm.sandbox(context).render(main.getKey());
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
    
    public void save(String sessionId) {
        for (CodeFile file: files) {
            file.save(sessionId);
        }
    }
}

