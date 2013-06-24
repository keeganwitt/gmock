package org.gmock.sample;

public class NotFoundException extends Exception {

    public NotFoundException(String key) {
        super("Key '" + key + "' not found.");
    }

}
