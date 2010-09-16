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
package org.gmock.internal

import junit.framework.Assert
import org.gmock.internal.MockController
import org.gmock.internal.MockDelegate
import org.gmock.internal.MockFactory
import org.gmock.internal.callstate.CallState
import org.gmock.internal.expectation.ClassExpectations
import org.gmock.internal.expectation.OrderedExpectations
import org.gmock.internal.expectation.UnorderedExpectations
import org.gmock.internal.recorder.ConstructorRecorder

class InternalMockController implements MockController {

    def mockFactory

    def mockCollection = []
    def concreteMocks = []
    def classExpectations = new ClassExpectations(this)
    def orderedExpectations = new OrderedExpectations(this)
    def unorderedExpectations = new UnorderedExpectations()

    boolean replay = false
    Order order = Order.NONE
    def mockDelegate = null

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    boolean internal = false


    InternalMockController(){
        mockFactory = new MockFactory(this, mockCollection)
    }

    boolean isOrdered() {
        order != Order.NONE
    }

    boolean isStrictOrdered() {
        order == Order.STRICT
    }

    boolean isLooseOrdered() {
        order == Order.LOOSE
    }

    @Deprecated
    def mock(Map constraints, Class clazz = Object, Closure expectationClosure = null) {
        def mockArgs = [:]
        mockArgs.clazz = clazz
        mockArgs.constructorRecorder = new ConstructorRecorder(constraints.constructor)
        if (expectationClosure){
            mockArgs.expectationClosure = expectationClosure
        }

        return doMock(mockArgs)
    }

    def mock(Class clazz = Object, Object ... args) {
        def mockArgs = mockFactory.parseMockArgument(clazz, args)
        return doMock(mockArgs)
    }

    private doMock(mockArgs) {
        def mock
        doInternal {
            if (replay) {
                throw new IllegalStateException("Cannot create mocks in play closure.")
            }
            if (mockArgs.containsKey('concreteInstance')) {
                mock = mockFactory.createConcreteMock(mockArgs)
            } else {
                mock = mockFactory.createMock(mockArgs)
            }
            if (mockArgs.constructorRecorder) {
                def expectation = mockArgs.constructorRecorder.generateExpectation(mockArgs.clazz, mock.mockInstance)
                classExpectations.addConstructorExpectation(mockArgs.clazz, expectation)
            }
        }
        if (mockArgs.expectationClosure) {
            callClosureWithDelegate(mockArgs.expectationClosure, mock.mockInstance)
        }

        return mock.mockInstance
    }

    private callClosureWithDelegate(Closure closure, delegate) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        def backup = mockDelegate
        try {
            mockDelegate = closure.delegate = new MockDelegate(delegate, this)
            closure(delegate)
        } finally {
            mockDelegate = backup
        }
    }

    def play(Closure closure) {
        doInternal {
            if (replay) {
                throw new IllegalStateException("Cannot nest play closures.")
            }
            if (ordered) {
                throw new IllegalStateException("Play closures cannot be inside ordered closure.")
            }

            mockCollection*.validate()
            classExpectations.validate()
            orderedExpectations.validate()
            mockCollection*.replay()
            concreteMocks*.startProxy()
            classExpectations.startProxy()
        }
        try {
            try {
                replay = true
                closure.call()
            } finally {
                replay = false
                doInternal {
                    classExpectations.stopProxy()
                    concreteMocks*.stopProxy()
                }
            }
            doInternal {
                mockCollection*.verify()
                classExpectations.verify()
                orderedExpectations.verify()
            }
        } finally {
            doInternal {
                mockCollection*.reset()
                classExpectations.reset()
                orderedExpectations.reset()
                unorderedExpectations.reset()
            }
        }
    }

    def with(mock, Closure withClosure) {
        callClosureWithDelegate(withClosure, mock)
    }

    def ordered(Closure orderedClosure) {
        if (ordered) {
            throw new IllegalStateException("Cannot nest ordered closures.")
        }
        if (replay) {
            throw new IllegalStateException("Ordered closures cannot be inside play closure.")
        }

        orderedExpectations.newStrictGroup()
        callClosureWithMockDelegate(orderedClosure, Order.STRICT)
    }

    def unordered(Closure unorderedClosure) {
        if (looseOrdered) {
            throw new IllegalStateException("Cannot nest unordered closures.")
        }
        if (!strictOrdered) {
            throw new IllegalStateException("Unordered closures can only be inside ordered closure.")
        }

        orderedExpectations.newLooseGroup()
        callClosureWithMockDelegate(unorderedClosure, Order.LOOSE)
    }

    private def callClosureWithMockDelegate(Closure closure, Order order) {
        Order backup = this.order
        try {
            this.order = order
            if (mockDelegate) {
                closure.resolveStrategy = Closure.DELEGATE_FIRST
                closure.delegate = mockDelegate
            }
            closure(mockDelegate)
        } finally {
            this.order = backup
        }
    }

    def fail(message, signature = null) {
        def callState = callState(signature).toString()
        if (callState) { callState = "\n$callState" }
        signature = signature ? ' ' + signature.toString(mockCollection.size() > 1) : ''
        Assert.fail("$message$signature$callState")
    }

    private callState(signature) {
        def callState = new CallState(mockCollection.size() > 1, !orderedExpectations.empty)
        orderedExpectations.appendToCallState(callState)
        unorderedExpectations.appendToCallState(callState)
        if (signature) {
            callState.nowCalling(signature)
        }
        return callState
    }

    def doInternal(Closure invokeOriginal, Closure work) {
        if (!internal) {
            return doInternal(work)
        } else {
            return invokeOriginal()
        }
    }

    def doInternal(Closure work) {
        doWork(work, true)
    }

    def doExternal(Closure work) {
        doWork(work, false)
    }

    private doWork(Closure work, boolean mode) {
        def backup = internal
        internal = mode
        try {
            return work()
        } finally {
            internal = backup
        }
    }

}

enum Order {NONE, STRICT, LOOSE}
