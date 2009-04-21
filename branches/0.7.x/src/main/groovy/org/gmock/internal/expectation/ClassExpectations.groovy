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
package org.gmock.internal.expectation

import org.gmock.internal.metaclass.ClassProxyMetaClass

class ClassExpectations {

    def proxies = [:]
    def controller
    def staticValidators = []

    ClassExpectations(controller) {
        this.controller = controller
    }

    def addStaticValidator(staticValidator) {
        staticValidators << staticValidator
    }

    def addConstructorExpectation(aClass, expectation){
        ClassProxyMetaClass proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxyMetaClass.getInstance(aClass, controller)
            proxies.put(aClass, proxy)
        }
        proxy.addConstructorExpectation(expectation)
    }

    def addStaticExpectation(aClass, expectation){
        ClassProxyMetaClass proxy = proxies.get(aClass)
        if (!proxy){
            proxy = ClassProxyMetaClass.getInstance(aClass, controller)
            proxies.put(aClass, proxy)
        }
        proxy.addStaticExpectation(expectation)
    }

    def startProxy() {
        proxies.values()*.startProxy()
    }

    def stopProxy() {
        proxies.values()*.stopProxy()
    }

    def validate(){
        proxies.values()*.validate()
        staticValidators*.call()
    }

    def verify() {
        proxies.values()*.verify()
    }

    def reset() {
        proxies.values()*.reset()
        staticValidators = []
    }

}
