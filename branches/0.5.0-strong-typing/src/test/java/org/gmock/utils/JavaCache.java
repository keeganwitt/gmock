package org.gmock.utils;

import java.util.Map;
import java.util.HashMap;

public class JavaCache {

    private JavaLoader loader;
    private Map<String, String> cache = new HashMap<String,String>();

    public JavaCache(JavaLoader loader) {
        this.loader = loader;
    }

    public String load(String key){
        return load(key, loader);
    }

    public String load(String key, ILoader loader) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            String value = loader.load(key);
            cache.put(key, value);
            return value;
        }
    }

}
