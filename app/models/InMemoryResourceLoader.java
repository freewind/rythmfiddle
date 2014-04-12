package models;

import org.rythmengine.resource.ResourceLoaderBase;
import play.cache.Cache;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 23/03/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class InMemoryResourceLoader extends ResourceLoaderBase {

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

    protected static String key(String path, String sessionId) {
        return sessionId + "://" + path;
    }

    @Override
    public CodeFile load(String path) {
        return load(path, sessionId);
    }

    @Override
    public String getResourceLoaderRoot() {
        return "";
    }
}
