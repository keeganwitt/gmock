package org.gmock.internal.signature

import org.gmock.Matcher

class ParameterSignature {

    def arguments

    ParameterSignature(arguments) {
        this.arguments = arguments.toList()
    }

    String toString() {
        arguments*.inspect().join(", ")
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object signature) {
        if (signature == null || !(signature instanceof ParameterSignature)) return false
        return true
    }

    boolean equals(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        return arguments == signature.arguments
    }

    boolean match(Object signature) {
        if (!equalsWithoutArguments(signature)) return false
        if (arguments.size() != signature.arguments.size()) return false
        return [arguments, signature.arguments].transpose().every { arg1, arg2 ->
            if (isMatcher(arg1)) return arg1.matches(arg2)
            else if (isMatcher(arg2)) return arg2.matches(arg1)
            else return arg1 == arg2
        }
    }

    private boolean isMatcher(object) {
        if (object == null) return false

        // as the Hamcrest library is optional, it may be not present at runtime
        // so we cannot use "object instanceof org.hamcrest.Matcher" directly
        return object instanceof Matcher || isHamcrestMatcherClass(object.class)
    }

    private boolean isHamcrestMatcherClass(Class clazz) {
        if (clazz == null) return false
        if (clazz.name == "org.hamcrest.Matcher") return true
        return isHamcrestMatcherClass(clazz.superclass) || clazz.interfaces.any { isHamcrestMatcherClass(it) }
    }

    int hashCode() {
        arguments.hashCode()
    }

}
