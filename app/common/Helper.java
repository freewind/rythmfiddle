package common;

import com.google.gson.Gson;
import models.Code;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * User: freewind
 * Date: 13-3-18
 * Time: 下午6:13
 */
public class Helper {

    public static boolean eq(Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }

    public static String nextUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Map<String, Object> parseJson(String jsonStr) {
        if (isBlank(jsonStr)) {
            return new HashMap();
        }
        return (Map<String, Object>) new Gson().fromJson(jsonStr, Map.class);
    }

    public static Code parse2code(String jsonStr) {
        return new Gson().fromJson(jsonStr, Code.class);
    }
}
