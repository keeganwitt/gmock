/*
 * Copyright 2008-2009 Julien Gagnet, Johnny Jian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
            String value;
            try {
                value = loader.load(key);
            } catch (NotFoundException e) {
                value = null;
            }
            cache.put(key, value);
            return value;
        }
    }

    public String getLoaderName() {
        return loader.getName();
    }

}
