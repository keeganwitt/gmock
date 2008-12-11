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
        def backup = controller.internal
        controller.internal = true
        try {
            return work()
        } finally {
            controller.internal = backup
        }
    }

}
