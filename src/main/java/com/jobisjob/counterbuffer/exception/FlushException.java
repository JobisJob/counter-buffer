package com.jobisjob.counterbuffer.exception;

public class FlushException extends Exception {

	private static final long serialVersionUID = 1L;

	public FlushException() {
        super();
    }

    public FlushException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlushException(String message) {
        super(message);
    }

    public FlushException(Throwable cause) {
        super(cause);
    }

}
