package org.gmock.internal.signature

class ConstructorSignature {

    Class aClass
    def arguments

    ConstructorSignature(aClass, arguments) {
        this.aClass = aClass
        this.arguments = new ParameterSignature(arguments)
    }

    String toString() {
        "new $aClass.simpleName($arguments)"
    }

    def validate(){
    }

    private boolean equalsWithoutArguments(Object constructor) {
        if (constructor == null || getClass() != constructor.getClass()) return false
        if (aClass != constructor.aClass) return false
        return true
    }

    boolean equals(Object constructor) {
        if (!equalsWithoutArguments(constructor)) return false
        if (arguments != constructor.arguments) return false
        return true
    }

    boolean match(Object constructor) {
        if (!equalsWithoutArguments(constructor)) return false
        if (!arguments.match(constructor.arguments)) return false
        return true
    }

    int hashCode() {
        aClass.hashCode() * 31  + arguments.hashCode()
    }

}
