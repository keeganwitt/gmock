package org.gmock.internal

import java.lang.reflect.Method
import net.sf.cglib.proxy.CallbackFilter

class GroovyObjectMethodFilter implements CallbackFilter {

    static GroovyObjectMethodFilter instance = new GroovyObjectMethodFilter()

    private GroovyObjectMethodFilter() {}

    int accept(Method method) {
        GroovyObject.metaClass.pickMethod(method.name, method.parameterTypes) ? 0 : 1
    }

}
