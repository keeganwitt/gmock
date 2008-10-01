package org.gmock

class ExpectationCollection {

    def proxies = []

    def expectConstructor(aClass, args, mock){
        def interceptor = proxies.find{ it.theClass == aClass }
        if (!interceptor){
            interceptor =  ProxyInterceptor.getInstance(aClass)
            proxies << interceptor
        }
        interceptor.expectConstructor(args, mock)

    }

    def startProxy(){
        proxies.each {it.startProxy()}
    }

    def stopProxy(){
        proxies.each {it.stopProxy()}
    }

    


}