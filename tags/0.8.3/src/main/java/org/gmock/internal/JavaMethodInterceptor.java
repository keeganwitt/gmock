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

public class JavaMethodInterceptor implements MethodInterceptor {

    private InternalMockController gmc;

    private MetaClass mpmc;

    private Object mockName;

    public JavaMethodInterceptor(InternalMockController gmc, MetaClass mpmc, Object mockName) {
        this.gmc = gmc;
        this.mpmc = mpmc;
        this.mockName = mockName;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if ("finalize".equals(method.getName()) && args.length == 0) {
            return null;
        }

        if (!gmc.getReplay() || gmc.getInternal()) {
            if ("toString".equals(method.getName()) && args.length == 0) {
                return mockName.toString();
            } else if ("hashCode".equals(method.getName()) && args.length == 0) {
                return System.identityHashCode(obj);
            } else if ("equals".equals(method.getName()) && args.length == 1) {
                return obj == args[0];
            }
        }

        if (gmc.getInternal()) {
            return proxy.invokeSuper(obj, args);
        } else {
            return mpmc.invokeMethod(obj, method.getName(), args);
        }
    }

}
