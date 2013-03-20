package models;

import com.google.gson.Gson;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.utils.JSONWrapper;
import com.greenlaw110.rythm.utils.S;

import java.io.IOException;

/**
 * User: freewind
 * Date: 13-3-18
 * Time: 下午6:07
 */
public class Code {

    public String id;
    public String desc;
    public String source;
    public String params;
    
    private static RythmEngine engine = new RythmEngine();

    public boolean isNew() {
        return S.empty(id);
    }

    public String render() throws IOException {
        //Map<String, Object> ps = Helper.parseJson(params);
        //return Rythm.sandbox().render(source, ps);
        if (!S.empty(params)) {
            return engine.sandbox().render(source, JSONWrapper.wrap(params));
        } else {
            return engine.sandbox().render(source);
        }
    }

    public String getJavaCode() {
        return engine.getTemplate(source).__getTemplateClass(false).javaSource;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
