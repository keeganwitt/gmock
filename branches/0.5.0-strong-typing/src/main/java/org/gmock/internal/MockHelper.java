package org.gmock.internal;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Factory;

/**
 * A helper class containing the methods which have to be written in Java.
 */
public class MockHelper {

    public static void setCallbacksTo(Factory object, Callback[] callbacks) {
        object.setCallbacks(callbacks);
    }

}
