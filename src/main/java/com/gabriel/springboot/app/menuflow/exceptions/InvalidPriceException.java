package com.gabriel.springboot.app.menuflow.exceptions;

public class InvalidPriceException extends RuntimeException {

    public InvalidPriceException() {
      super("Price must be greater than zero");
    }

    public InvalidPriceException(String message) {
        super(message);
    }
}
