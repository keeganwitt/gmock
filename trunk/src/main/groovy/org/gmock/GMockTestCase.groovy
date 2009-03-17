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
package org.gmock

abstract class GMockTestCase extends GroovyTestCase {

    protected gMockController = new GMockController()

    protected mock(Object... args) {
        gMockController.mock(*args)
    }

    protected play(Closure closure) {
        gMockController.play(closure)
    }

    protected with(mock, Closure withClosure) {
        gMockController.with(mock, withClosure)
    }

    protected ordered(Closure orderedClosure) {
        gMockController.ordered(orderedClosure)
    }

    protected unordered(Closure unorderedClosure) {
        gMockController.unordered(unorderedClosure)
    }

    protected constructor(Object... args) {
        GMock.constructor(args)
    }

    protected invokeConstructor(Object... args) {
        GMock.invokeConstructor(args)
    }

    protected match(Closure matcher) {
        GMock.match(matcher)
    }

    protected name(String mockName) {
        GMock.name(mockName)
    }

}
