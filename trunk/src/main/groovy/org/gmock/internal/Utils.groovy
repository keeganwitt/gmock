package org.gmock.internal

class Utils {

    static abreviateClassName(String className){
        def index = className.lastIndexOf(".")
        if (index >= 0){
            return className.substring(index + 1)
        } else {
            return className
        }

    }

}