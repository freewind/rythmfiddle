package models;

import com.greenlaw110.rythm.Rythm;
import com.greenlaw110.rythm.RythmEngine;
import com.greenlaw110.rythm.extension.ICodeType;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.resource.*;
import com.greenlaw110.rythm.utils.S;
import play.cache.Cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/03/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class InMemoryResourceLoader implements ITemplateResourceLoader {

    public final String sessionId;

    public InMemoryResourceLoader(String sessionId) {
        this.sessionId = sessionId;
    }

    public static void save(String path, String sessionId, CodeFile file) {
        Cache.set(key(path, sessionId), file);
    }
    
    public static CodeFile load(String path, String sessionId) {
        return (CodeFile)Cache.get(key(path, sessionId));
    }

    private static String key(String path, String sessionId) {
        return sessionId + "://" + path;
    }

    @Override
    public CodeFile load(String path) {
        return load(path, sessionId);
    }

    public TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerClass) {
        return tryLoadTemplate(tmplName, engine, callerClass, true);
    }

    private TemplateClass tryLoadTemplate(String tmplName, RythmEngine engine, TemplateClass callerClass, boolean processTagName) {
        if (null == engine) engine = Rythm.engine();
        if (engine.templateRegistered(tmplName)) return null;
        String rythmSuffix = engine.conf().resourceNameSuffix();
        final List<String> suffixes = new ArrayList(Arrays.asList(new String[]{
                ".html",
                ".json",
                ".js",
                ".css",
                ".csv",
                ".xml",
                ".txt",
                ""
        }));
        ICodeType codeType = TemplateResourceBase.getTypeOfPath(engine, tmplName);
        if (ICodeType.DefImpl.RAW == codeType) {
            // use caller's code type
            codeType = callerClass.codeType;
        }
        final String tagNameOrigin = tmplName;
        if (processTagName) {
            boolean tagNameProcessed = false;
            while (!tagNameProcessed) {
                // process tagName to remove suffixes
                // 1. check without rythm-suffix
                for (String s : suffixes) {
                    if (tmplName.endsWith(s)) {
                        tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                        break;
                    }
                }
                if (S.notEmpty(rythmSuffix)) {
                    // 2. check with rythm-suffix
                    for (String s : suffixes) {
                        s = s + rythmSuffix;
                        if (tmplName.endsWith(s)) {
                            tmplName = tmplName.substring(0, tmplName.lastIndexOf(s));
                            break;
                        }
                    }
                }
                tagNameProcessed = true;
            }
        }
        tmplName = tmplName.replace('.', '/');
        String sfx = codeType.resourceNameSuffix();
        if (S.notEmpty(sfx) && !suffixes.get(0).equals(sfx)) {
            suffixes.remove(sfx);
            suffixes.add(0, sfx);
        }
        CodeFile tagFile;
        List<String> roots = new ArrayList<String>();
        roots.add("");

        // call tag with import path
        if (null != callerClass.importPaths) {
            for (String s : callerClass.importPaths) {
                roots.add(s.replace('.', '/'));
            }
        }

        final String tagName0 = tmplName;
        // call tag using relative path
        String currentPath = callerClass.getKey().toString();
        int pos = currentPath.lastIndexOf("/");
        if (-1 == pos) {
            pos = currentPath.lastIndexOf(File.separator);
        }
        if (-1 != pos) {
            currentPath = currentPath.substring(0, pos);
            if (currentPath.startsWith("/") || currentPath.startsWith(File.separator))
                currentPath = currentPath.substring(1);
            roots.add(currentPath);
        }

        for (String r : roots) {
            tmplName = r + '/' + tagName0;
            for (String suffix : suffixes) {
                String name = tmplName + suffix + rythmSuffix;

                tagFile = load(name);
                ITemplateResource tr = tagFile;
                if (null != tr) {
                    try {
                        TemplateClass tc = engine.classes().getByTemplate(tr.getKey());
                        if (null == tc) {
                            tc = new TemplateClass(tr, engine);
                        }
                        try {
                            tc.asTemplate(); // register the template
                            return tc;
//                            ITemplate t = tc.asTemplate();
//                            if (null != t) {
//                                String fullName = getFullTagName(tc, engine);
//                                tc.setFullName(fullName);
//                                engine.registerTemplate(fullName, t);
//                                return tc;
//                            }
                        } catch (Exception e) {
                            return tc;
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        }
        return processTagName ? tryLoadTemplate(tagNameOrigin, engine, callerClass, false) : null;
    }
    
    @Override
    public String getFullName(TemplateClass tc, RythmEngine engine) {
        return FileTemplateResource.getFullTagName(tc, engine);
    }

    @Override
    public void scan(String root, TemplateResourceManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
