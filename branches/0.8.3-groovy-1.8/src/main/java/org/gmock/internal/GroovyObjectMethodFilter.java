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
package org.gmock.internal;

import groovy.lang.GroovyObject;
import net.sf.cglib.proxy.CallbackFilter;

import java.lang.reflect.Method;

public class GroovyObjectMethodFilter implements CallbackFilter {

    public static final GroovyObjectMethodFilter INSTANCE = new GroovyObjectMethodFilter();

    private GroovyObjectMethodFilter() {
    }

    public int accept(Method method) {
        return hasMethod(GroovyObject.class, method) ? 1 : 0;
    }

    private boolean hasMethod(Class<?> clazz, Method method) {
        try {
            clazz.getMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }

    }

}
