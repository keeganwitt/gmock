package org.gmock.utils

class Loader {

    def store = [:]

    def load = { key ->
        return store[key]
    }

    def put(key, value){
        store[key] = value
    }

    static one(){
        return 1
    }

}