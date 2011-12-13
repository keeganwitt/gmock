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
package org.gmock.internal.metaclass;

import groovy.lang.Closure;
import groovy.lang.GroovySystem;
import groovy.lang.MetaMethod;
import groovy.lang.ProxyMetaClass;
import org.codehaus.groovy.runtime.InvokerHelper;
import static org.codehaus.groovy.runtime.MetaClassHelper.convertToTypeArray;
import org.gmock.GMock;
import org.gmock.internal.InternalMockController;

import java.beans.IntrospectionException;

public class MockDelegateMetaClass extends ProxyMetaClass {

    private Object delegate;
    private InternalMockController controller;

    public MockDelegateMetaClass(Class clazz, Object delegate, InternalMockController controller) throws IntrospectionException {
        super(GroovySystem.getMetaClassRegistry(), clazz, GroovySystem.getMetaClassRegistry().getMetaClass(clazz));
        this.delegate = delegate;
        this.controller = controller;
    }

    public Object invokeMethod(Object object, String methodName, Object[] arguments) {
        return invokeMethod(theClass, object, methodName, arguments, false, false);
    }

    public Object invokeMethod(Class sender, Object receiver, String methodName, Object[] arguments, boolean isCallToSuper, boolean fromInsideClass) {
        return pickMethod(methodName, convertToTypeArray(arguments)).invoke(receiver, arguments);
    }

    public Object getProperty(Object object, String property) {
        return getProperty(theClass, object, property, false, false);
    }

    public Object getProperty(Class sender, Object receiver, String property, boolean isCallToSuper, boolean fromInsideClass) {
        return InvokerHelper.getProperty(delegate, property);
    }

    public void setProperty(Object object, String property, Object newValue) {
        setProperty(theClass, object, property, newValue, false, false);
    }

    public void setProperty(Class sender, Object receiver, String property, Object value, boolean isCallToSuper, boolean fromInsideClass) {
        InvokerHelper.setProperty(delegate, property, value);
    }

    public MetaMethod pickMethod(String methodName, Class[] arguments) {
        Object newDelegate = null;
        if (arguments.length == 1 && arguments[0] != null && Closure.class.isAssignableFrom(arguments[0])) {
            if ("match".equals(methodName)) {
                newDelegate = GMock.class;
            } else if ("ordered".equals(methodName) || "unordered".equals(methodName)) {
                newDelegate = controller;
            }
        }
        return new DelegateMetaMethod(this, methodName, arguments, newDelegate != null ? newDelegate : delegate);
    }

}
