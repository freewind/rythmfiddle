package models;

import org.rythmengine.RythmEngine;
import org.rythmengine.extension.ICodeType;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.TemplateResourceBase;
import org.rythmengine.utils.S;

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
            //RythmEngine engine = RythmEngine.get();
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
