package models;

import com.greenlaw110.rythm.extension.II18nMessageResolver;
import com.greenlaw110.rythm.template.ITemplate;
import com.greenlaw110.rythm.utils.I18N;
import play.cache.Cache;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 7/04/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class InMemoryMessageResolver extends InMemoryResourceLoader implements II18nMessageResolver {

    public InMemoryMessageResolver(String sessionId) {
        super(sessionId);
    }
    
    public static final String CACHE_KEY = "__FIDDLE_CODE__";
    
    public static void save(Code code, String sessionId) {
        Cache.set(key(CACHE_KEY, sessionId), code);
    }
    
    private String _msg(ITemplate template, Locale locale, CodeFile cf, String key, Object... args) {
        String res = cf.asTemplateContent();
        Properties p = new Properties();
        try {
            p.load(new StringReader(res));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String s = p.getProperty(key);
        if (null == s) return null;
        int argLen = args.length;
        if (argLen > 0) {
            MessageFormat fmt = new MessageFormat(s, locale);
            Object[] argsResolved = new Object[argLen];
            for (int i = 0; i < argLen; ++i) {
                Object arg = args[i];
                if (arg instanceof String) {
                    arg = getMessage(template, (String) arg);
                }
                argsResolved[i] = arg;
            }
            return fmt.format(argsResolved);
        } else {
            return s;
        }
    }

    @Override
    public String getMessage(ITemplate template, String key, Object... args) {
        boolean useFormat = args.length > 0;
        Locale locale = null;
        if (useFormat) {
            // check if the first arg is locale
            Object arg0 = args[0];
            if (arg0 instanceof Locale) {
                locale = (Locale)arg0;
                Object[] args0 = new Object[args.length - 1];
                System.arraycopy(args, 1, args0, 0, args.length - 1);
                args = args0;
            }
        }
        if (null == locale) locale = I18N.locale(template);

        Code code = Cache.get(key(CACHE_KEY, sessionId), Code.class);
        for (CodeFile cf: code.files) {
            if (cf.filename.startsWith("messages.")) {
                String s = locale.toString();
                while(true) {
                    String retVal = null, fn = "messages." + s;
                    if (cf.filename.startsWith(fn)) {
                        retVal = _msg(template, locale, cf, key, args);
                        if (null != retVal) return retVal;
                    }
                    int pos = fn.lastIndexOf('_');
                    if (pos == -1) break;
                    // get off locale variant and try
                    fn = fn.substring(0, pos);
                    if (cf.filename.startsWith(fn)) {
                        retVal = _msg(template, locale, cf, key, args);
                        if (null != retVal) return retVal;
                    }
                    pos = fn.lastIndexOf('_');
                    if (pos == -1) break;
                    // get rid of locale country code and try again
                    fn = fn.substring(0, pos);
                    if (cf.filename.startsWith(fn)) {
                        retVal = _msg(template, locale, cf, key, args);
                        if (null != retVal) return retVal;
                    }
                    break;
                }
            }
        }
        return key;
    }
}
