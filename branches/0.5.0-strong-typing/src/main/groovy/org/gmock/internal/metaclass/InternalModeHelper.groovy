package org.gmock.internal.metaclass

class InternalModeHelper {

    static doInternal(controller, Closure invokeOriginal, Closure work) {
        if (!controller.internal) {
            controller.internal = true
            try {
                return work()
            } finally {
                controller.internal = false
            }
        } else {
            return invokeOriginal()
        }
    }

}
