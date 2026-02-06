package com.gabriel.springboot.app.menuflow.exceptions;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.INVALID_PRICE_MESSAGE;

public class InvalidPriceException extends RuntimeException {

    public InvalidPriceException() {
      super(INVALID_PRICE_MESSAGE);
    }

    public InvalidPriceException(String message) {
        super(message);
    }
}
