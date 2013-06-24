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
package org.gmock.utils;

import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

public class JavaTestHelper {

    public static final String SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    
    public static final String SCHEMA_LANGUAGE_VALUE = "http://www.w3.org/2001/XMLSchema";

    public static boolean equalsToEachOther(Object a, Object b) {
        return a.equals(b);
    }

    public static int hashCodeOf(Object obj) {
        return obj.hashCode();
    }

    public static String toStringOn(Object obj) {
        return obj.toString();
    }

    public static String chainedCallsOn(ChainA a) {
        return a.getB().getC().getText();
    }

    public static String chainedMethodsOn(ChainA a) {
        return a.methodA(1).methodB(2).getText();
    }

    public static void setSAXReaderProperty(SAXReader reader) throws SAXException {
        reader.setProperty(SCHEMA_LANGUAGE, SCHEMA_LANGUAGE_VALUE);
    }

}
