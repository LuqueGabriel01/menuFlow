package com.gabriel.springboot.app.menuflow.exceptions;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.USER_ENABLED_MESSAGE;

public class UserEnabledException extends RuntimeException {

    public UserEnabledException() {
        super(USER_ENABLED_MESSAGE);
    }

    public UserEnabledException(String message) {
        super(message);
    }
}
