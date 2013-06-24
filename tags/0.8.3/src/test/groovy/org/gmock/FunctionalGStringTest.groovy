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

import org.junit.Test
import org.gmock.utils.JavaFinalClass
import org.gmock.utils.JavaLoader

@WithGMock
class FunctionalGStringTest {
    
    @Test void "mock toString and use in GString passing to dynamic mock"() {
        def m1 = mock()
        m1.toString().returns("mock")
        def m2 = mock()
        m2.method("mock").returns(1)
        play {
            assert m2.method("$m1") == 1
        }
    }
    
    @Test void "mock toString and use in GString passing to mock of final class"() {
        def m1 = mock()
        m1.toString().returns("mock")
        def m2 = mock(JavaFinalClass)
        m2.method("mock").returns(1)
        play {
            assert m2.method("$m1") == 1
        }
    }
    
    @Test void "mock toString and use in GString passing to concrete mock"() {
        def m1 = mock()
        m1.toString().returns("mock")
        def o = new Object()
        def m2 = mock(o)
        m2.method("mock").returns(1)
        play {
            assert o.method("$m1") == 1
        }
    }
    
    @Test void "mock toString and use in GString passing to static mock"() {
        def m1 = mock()
        m1.toString().returns("mock")
        mock(JavaLoader).static.method("mock").returns(1)
        play {
            assert JavaLoader.method("$m1") == 1
        }
    }
    
    @Test void "mock toString and use in GString passing to mocked constructor"() {
        def m1 = mock()
        m1.toString().returns("mock")
        mock JavaLoader, constructor("mock")
        play {
            new JavaLoader("$m1")
        }
    }
    
}
