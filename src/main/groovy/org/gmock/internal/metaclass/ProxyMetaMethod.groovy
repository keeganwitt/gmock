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
package org.gmock.internal.metaclass

import java.lang.reflect.Modifier
import org.codehaus.groovy.reflection.CachedClass
import org.codehaus.groovy.reflection.ReflectionCache

class ProxyMetaMethod extends MetaMethod {

    private MetaClass theMetaClass
    String name
    CachedClass declaringClass

    ProxyMetaMethod(MetaClass metaClass, String name, Class[] parameterTypes) {
        super(parameterTypes)
        this.theMetaClass = metaClass
        this.name = name
        this.declaringClass = ReflectionCache.getCachedClass(metaClass.theClass)
        getParameterTypes()
    }

    int getModifiers() {
        Modifier.PUBLIC
    }

    Class getReturnType() {
        Object
    }

    Object invoke(Object object, Object[] arguments) {
        theMetaClass.invokeMethod(object, name, arguments)
    }

}
