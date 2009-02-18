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

class InternalModeHelper {

    static doInternal(controller, Closure invokeOriginal, Closure work) {
        if (!controller.internal) {
            return doInternal(controller, work)
        } else {
            return invokeOriginal()
        }
    }

    static doInternal(controller, Closure work) {
        doWork(controller, work, true)
    }

    static doExternal(controller, Closure work) {
        doWork(controller, work, false)
    }

    static doWork(controller, Closure work, boolean mode) {
        def backup = controller.internal
        controller.internal = mode
        try {
            return work()
        } finally {
            controller.internal = backup
        }
    }

}
