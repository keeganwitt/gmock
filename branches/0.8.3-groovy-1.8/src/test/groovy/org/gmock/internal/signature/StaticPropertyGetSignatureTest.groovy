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
package org.gmock.internal.signature

import org.gmock.utils.Loader

class StaticPropertyGetSignatureTest extends GroovyTestCase {

    void testToString() {
        def signature = new StaticPropertyGetSignature(Loader, 'property')
        assertEquals 'Loader.property', signature.toString()
    }

    void testToStringWithMockName() {
        def signature = new StaticPropertyGetSignature(Loader, 'property')
        assertEquals "'Loader.property'", signature.toString(true)
    }

    void testToStringWithMockNameAndPostfix() {
        def signature = new StaticPropertyGetSignature(Loader, 'property')
        assertEquals "'Loader.property.other'", signature.toString(true, '.other')
    }

}
