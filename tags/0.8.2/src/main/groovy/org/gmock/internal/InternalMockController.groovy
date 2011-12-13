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
import org.gmock.internal.MockInternal
import org.gmock.internal.ChainsMockInternal
import org.gmock.internal.callstate.CallState
import org.gmock.internal.expectation.ClassExpectations
import org.gmock.internal.expectation.OrderedExpectations
import org.gmock.internal.expectation.UnorderedExpectations
import org.gmock.internal.factory.MockFactory
import org.gmock.internal.recorder.MockNameRecorder

class InternalMockController {

    MockFactory mockFactory

    def mockCollection = []
    def mockCount = 0
    def concreteMocks = []
    def classExpectations = new ClassExpectations(this)

    OrderingController orderingController = new OrderingController(this)

    boolean replay = false
    def mockDelegate = null

    // while running in internal mode, we should not mock any methods, instead, we should invoke the original implements
    // it is a little like the kernel mode in OS
    boolean internal = false


    InternalMockController(){
        mockFactory = new MockFactory(this, mockCollection)
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

        ++mockCount

        return mockInstance
    }

    /**
     * Used by the chains mocking
     */
    def createChainsMockInternal(previousSignature) {
        mockFactory.createChainsMockInternal(previousSignature)
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
        }
        try {
            doInternal {
                if (orderingController.ordered) {
                    throw new IllegalStateException("Play closures cannot be inside ordered closure.")
                }
                
                mockCollection*.validate()
                classExpectations.validate()
                orderingController.validate()
                mockCollection*.replay()
                concreteMocks*.startProxy()
                classExpectations.startProxy()
            }
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
                orderingController.verify()
            }
        } finally {
            doInternal {
                mockCollection*.reset()
                classExpectations.reset()
                orderingController.reset()
                removeChainsMocks()
            }
        }
    }
    
    private removeChainsMocks() {
        mockCollection.removeAll { it instanceof ChainsMockInternal }
    }

    def with(mock, Closure withClosure) {
        callClosureWithDelegate(withClosure, mock)
    }

    def ordered(Closure orderedClosure) {
      orderingController.ordered(orderedClosure)
    }

    def unordered(Closure unorderedClosure) {
      orderingController.unordered(unorderedClosure)
    }

    def addToExpectations(expectation, expectations) {
      orderingController.addToExpectations(expectation, expectations)
    }  


    def fail(message, signature = null) {
        def callState = callState(signature).toString()
        if (callState) { callState = "\n$callState" }
        signature = signature ? ' ' + signature.toString(mockCount > 1) : ''
        Assert.fail("$message$signature$callState")
    }

    private callState(signature) {
        def callState = new CallState(mockCount > 1, !orderingController.orderedExpectations.empty)
        orderingController.orderedExpectations.appendToCallState(callState)
        orderingController.unorderedExpectations.appendToCallState(callState)
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

