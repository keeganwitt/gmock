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
package org.gmock.internal.factory

import static org.gmock.GMock.*
import groovy.lang.MissingMethodException

class MockFactoryTest extends GroovyTestCase {

    MockFactory mockFactory = new MockFactory(null, null)

    void testParseFullMockArgument() {
        def args = mockFactory.parseMockArgument(String.class,
                [name("my name"), constructor("arg1", "arg2"), invokeConstructor("inv1", "inv2"), { "closure" }])
        assertEquals "my name", args.mockNameRecorder.mockName
        assertEquals String.class, args.clazz
        assertEquals(["arg1", "arg2"], args.constructorRecorder.args)
        assertEquals(["inv1", "inv2"], args.invokeConstructorRecorder.args)
        assertEquals("closure", args.expectationClosure())
        assertNull args.concreteInstance
    }

    void testParsePartialMockArgument() {
        def args = mockFactory.parseMockArgument(Object.class, [name("my name")])
        assertEquals "my name", args.mockNameRecorder.mockName
        assertEquals Object.class, args.clazz
        assertNull args.constructorRecorder
        assertNull args.invokeConstructorRecorder
        assertNull args.expectationClosure
        assertNull args.concreteInstance
    }

    void testParseInvalidMockArgument() {
        shouldFail(MissingMethodException) {
            mockFactory.parseMockArgument(Object.class, [name("my name"), "name"])
        }
    }

    void testParseFullConcreteMockArgument() {
        def concreteObject = "concrete"
        def args = mockFactory.parseMockArgument(Object.class, [concreteObject, name("my name")])
        assertEquals concreteObject, args.concreteInstance
        assertEquals "my name", args.mockNameRecorder.mockName
        assertEquals String, args.clazz
        assertNull args.constructorRecorder
        assertNull args.invokeConstructorRecorder
        assertNull args.expectationClosure
    }

    void testParsePartialConcreteMockArgument() {
        def concreteObject = "concrete"
        def args = mockFactory.parseMockArgument(Object.class, [concreteObject])
        assertEquals concreteObject, args.concreteInstance
        assertEquals String, args.clazz
        assertNull args.constructorRecorder
        assertNull args.invokeConstructorRecorder
        assertNull args.expectationClosure
        assertNull args.mockNameRecorder
    }


    void testParseInvalidConcreteMockArgument() {
        def concreteObject = "concrete"
        shouldFail(MissingMethodException) {
            mockFactory.parseMockArgument(Object.class, [concreteObject, name("my name"), invokeConstructor(["wrong"])])
        }
    }


}