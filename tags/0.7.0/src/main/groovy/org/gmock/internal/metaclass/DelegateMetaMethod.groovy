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
package org.gmock.internal.metaclass

class DelegateMetaMethod extends ProxyMetaMethod {

    Object delegate

    DelegateMetaMethod(MetaClass metaClass, String name, Class[] parameterTypes, Object delegate) {
        super(metaClass, name, parameterTypes)
        this.delegate = delegate
    }

    Object invoke(Object object, Object[] arguments) {
        delegate."$name"(*arguments)
    }

}