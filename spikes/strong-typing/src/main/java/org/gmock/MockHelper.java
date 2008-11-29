package org.gmock;

import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.Callback;

public class MockHelper {

    public static void setCallbacksTo(Factory proxy, Callback[] callbacks) {
        proxy.setCallbacks(callbacks);
    }

}
