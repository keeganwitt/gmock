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
import org.gmock.internal.MockDelegate
import org.gmock.internal.MockFactory
import org.gmock.internal.MockInternal
import org.gmock.internal.callstate.CallState
import org.gmock.internal.expectation.ClassExpectations
import org.gmock.internal.expectation.OrderedExpectations
import org.gmock.internal.expectation.UnorderedExpectations
import org.gmock.internal.recorder.MockNameRecorder

class InternalMockController {

    def mockFactory

    def mockCollection = []
    def concreteMocks = []
    def classExpectations = new ClassExpectations(this)
    def orderedExpectations = new OrderedExpectations(this)
    def unorderedExpectations = new UnorderedExpectations(this)

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

    def mock(Class clazz = Object, Object ... args) {
        def mockArgs = null, mockInstance = null
        doInternal {
            if (replay) {
                throw new IllegalStateException("Cannot create mocks in play closure.")
            }

            mockArgs = mockFactory.parseMockArgument(clazz, args)
            if (mockArgs.containsKey('concreteInstance')) {
                mockInstance = mockFactory.createConcreteMock(mockArgs)
            } else {
                mockInstance = mockFactory.createMock(mockArgs)
            }

            if (mockArgs.constructorRecorder) {
                def expectation = mockArgs.constructorRecorder.generateExpectation(mockArgs.clazz, mockInstance)
                classExpectations.addConstructorExpectation(mockArgs.clazz, expectation)
            }
        }
        if (mockArgs.expectationClosure) {
            callClosureWithDelegate(mockArgs.expectationClosure, mockInstance)
        }

        return mockInstance
    }

    /**
     * Used by the chains mocking
     */
    def createMockInternal() {
        mockFactory.createMockInternal(Object, new MockNameRecorder(''))
    }

    def createMockOfClass(Class clazz, MockInternal mockInternal) {
        mockFactory.createMockOfClass(clazz, mockInternal)
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

    Object doInternal(invokeOriginal, work) {
        if (!internal) {
            return doInternal(work)
        } else {
            return invokeOriginal.call()
        }
    }

    Object doInternal(work) {
        doWork(work, true)
    }

    Object doExternal(work) {
        doWork(work, false)
    }

    private doWork(work, boolean mode) {
        def backup = internal
        internal = mode
        try {
            return work.call()
        } finally {
            internal = backup
        }
    }

}

enum Order {NONE, STRICT, LOOSE}
