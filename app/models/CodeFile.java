package models;

import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.resource.ITemplateResource;
import com.greenlaw110.rythm.resource.TemplateResourceBase;
import com.greenlaw110.rythm.utils.S;

public class CodeFile implements ITemplateResource {
    public String filename;
    public String source;
    public boolean isMain;
    public String tagName;
    public String sessionId;
    private RythmEngine engine;
    
    @Override
    public String getKey() {
        if (filename.startsWith("/")) return filename;
        else return "/" + filename;
    }

    @Override
    public String getSuggestedClassName() {
        return "C" + getFullTagName(getKey());
    }

    @Override
    public String asTemplateContent() {
        return source;
    }

    @Override
    public void setEngine(RythmEngine engine) {
        this.engine = engine;
    }

    @Override
    public boolean refresh() {
        CodeFile cf = InMemoryResourceLoader.load(getKey(), sessionId);
        if (null != cf && !equals(cf)) {
            source = cf.source;
            return true;
        }
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String tagName() {
        if (null == tagName) {
            this.tagName = getFullTagName(getKey());
        }
        return tagName;
    }

    private static String getFullTagName(String key) {
        if (key.startsWith("/")) key = key.substring(1);
//        int pos = key.lastIndexOf(".");
//        if (-1 != pos) key = key.substring(0, pos);
        return key.replace('/', '.');
    }

    private ICodeType type = null;
    @Override
    public ICodeType codeType() {
        if (null == type) { 
            RythmEngine engine = RythmEngine.get();
            type = TemplateResourceBase.getTypeOfPath(engine, getKey());
        }
        
        return type;
    }
    
    public void save(String sessionId) {
        this.sessionId = sessionId;
        InMemoryResourceLoader.save(getKey(), sessionId, this);
    }

    public boolean equals(Object that) {
        if (this == that) return true;
        if (that instanceof CodeFile) {
            CodeFile cf = (CodeFile)that;
            return cf.getKey().equals(this.getKey()) && S.eq(cf.sessionId, this.sessionId) && cf.source.equals(this.source);
        } else {
            return false;
        }
    }
}
