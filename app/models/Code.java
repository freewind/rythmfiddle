package models;

import com.google.gson.Gson;
import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.template.TemplateBase;
import common.Helper;

import java.io.IOException;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

    public boolean isNew() {
        return isBlank(id);
    }

    public String render() throws IOException {
        Map<String, Object> ps = Helper.parseJson(params);
        return Rythm.renderStr(source, ps);
    }

    public String getJavaCode() {
        return ((TemplateBase) RythmEngine.get().getTemplate(source)).__getTemplateClass(false).javaSource;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
