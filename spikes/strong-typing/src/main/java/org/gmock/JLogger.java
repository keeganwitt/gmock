package org.gmock;

public class JLogger implements Logger {

    public void error(String message) {
        System.out.println("Java Error: " + message);
    }

    public void error(Throwable error) {
        System.out.println("Java Error: " + error.getMessage());
    }

    public void debug(String message) {
        System.out.println("Java Debug: " + message);
    }

    public void warning(String message) {
        System.out.println("Java Warning: " + message);
    }

    public void info(String message) {
        System.out.println("Java Info: " + message);
    }

}
