package com.kasoverskiy.ovchipkaart;

/**
 * Created by joycollector on 4/3/16.
 */
public class OvException extends RuntimeException {

    public OvException() {
    }

    public OvException(String message) {
        super(message);
    }

    public OvException(String message, Throwable cause) {
        super(message, cause);
    }

    public OvException(Throwable cause) {
        super(cause);
    }

    public OvException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
