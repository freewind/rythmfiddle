package common;

import models.Code;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import play.Play;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: freewind
 * Date: 13-3-21
 * Time: 上午10:22
 */
public class CodeManager {


    public static final File CODE_ROOT = Play.getFile("data");

    private static Map<String, Code> map;

    public synchronized static void reload() throws IOException {
        map = new ConcurrentHashMap<String, Code>();
        for (String filename : CODE_ROOT.list()) {
            if (filename.endsWith(".json")) {
                String id = StringUtils.substringBeforeLast(filename, ".json");
                Code code = loadCode(id);
                map.put(code.id, code);
            }
        }
    }

    public static Map<String, Code> getAll() {
        return map;
    }

    public static Code findById(String id) {
        return map.get(id);
    }

    private static Code loadCode(String id) throws IOException {
        File file = getCodeFile(id);
        String content = FileUtils.readFileToString(file, "UTF-8");
        return Helper.parse2code(content);
    }

    private static File getCodeFile(String id) {
        return new File(CODE_ROOT, id + ".json");
    }

    public static void saveOrUpdate(Code code) throws IOException {
        String targetId = code.isNew() ? Helper.nextUUID() : code.id;
        code.id = targetId;
        File target = new File(CODE_ROOT, targetId + ".json");
        FileUtils.writeStringToFile(target, code.toJson());
        reload();
    }

    public static void deleteById(String id) throws IOException {
        File file = getCodeFile(id);
        file.delete();
        reload();
    }

}
