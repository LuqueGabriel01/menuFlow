package com.gabriel.springboot.app.menuflow.exceptions;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.USER_DISABLED_MESSAGE;

public class UserDisabledException extends RuntimeException {

    public UserDisabledException() {
        super(USER_DISABLED_MESSAGE);
    }

    public UserDisabledException(String message) {
        super(message);
    }
}
