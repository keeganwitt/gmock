package org.gmock;

public interface Logger {

    void error(String message);

    void error(Throwable error);

    void debug(String message);

    void warning(String message);

    void info(String message);

}
