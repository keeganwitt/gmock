package org.gmock

class ProxyCollection {

    def proxies = []

    def expectConstructor(aClass, args, mock){
        def interceptor = proxies.find{ it.theClass == aClass }
        if (!interceptor){
            interceptor =  StaticMock.getInstance(aClass)
            proxies << interceptor
        }
        interceptor.expectConstructor(args, mock)
    }

    def startProxy(){
        proxies*.startProxy()
    }

    def stopProxy(){
        proxies*.stopProxy()
    }

    def verify(){
        proxies*.verify()
    }


}