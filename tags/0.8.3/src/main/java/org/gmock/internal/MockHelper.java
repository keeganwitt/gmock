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

import groovy.lang.GString;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Factory;

/**
 * A helper class containing the methods which have to be written in Java.
 */
public class MockHelper {

    public static void setCallbacksTo(Factory object, Callback[] callbacks) {
        object.setCallbacks(callbacks);
    }

    public static Class<?> getClassOfObject(Object object) {
        return object.getClass();
    }

    public static Object[] evaluateGStrings(Object[] arguments, InternalMockController controller) {
        final Object[] evaluated = arguments.clone();
        for (int i = 0; i < evaluated.length; ++i) {
            final int current = i;
            if (evaluated[i] instanceof GString) {
                controller.doExternal(new Callable() {

                    public Object call() {
                        evaluated[current] = evaluated[current].toString();
                        return null;
                    }

                });
            }
        }
        return evaluated;
    }

    public static String toString(Object object) {
        return object == null ? "null" : object.toString();
    }

}
