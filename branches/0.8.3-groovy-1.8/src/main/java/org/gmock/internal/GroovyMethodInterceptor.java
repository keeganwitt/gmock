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

import groovy.lang.MetaClass;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class GroovyMethodInterceptor implements MethodInterceptor {

    private MetaClass mpmc;

    public GroovyMethodInterceptor(MetaClass mpmc) {
        this.mpmc = mpmc;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String name = method.getName();
        if ("invokeMethod".equals(name)) {
            return mpmc.invokeMethod(obj, (String) args[0], args[1]);
        } else if ("getProperty".equals(name)) {
            return mpmc.getProperty(obj, (String) args[0]);
        } else if ("setProperty".equals(name)) {
            mpmc.setProperty(obj, (String) args[0], args[1]);
            return null;
        } else if ("getMetaClass".equals(name)) {
            return mpmc;
        } else { // ignore "setMetaClass" method
            return null;
        }
    }

}
