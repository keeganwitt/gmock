package org.gmock.internal.signature

class ParameterSignature {

    def arguments

    ParameterSignature(arguments) {
        this.arguments = arguments.toList()
    }

    String toString() {
        arguments*.inspect().join(", ")
    }

    boolean equals(Object signature) {
        if (signature == null || !(signature instanceof ParameterSignature)) return false
        if (arguments.size() != signature.arguments.size()) return false
        if (arguments.any { isMatcher(it) }) {
            return [arguments, signature.arguments].transpose().every { arg1, arg2 ->
                isMatcher(arg1) ? arg1.matches(arg2) : arg1 == arg2
            }
        } else if (signature.arguments.any { isMatcher(it) }) {
            return signature == this
        } else {
            return arguments == signature.arguments;
        }
    }

    private boolean isMatcher(object) {
        // as the Hamcrest library is optional, it may be not present at runtime
        // so we cannot use "object instanceof org.hamcrest.Matcher" directly
        return isHamcrestMatcherClass(object.class)
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
