package com.gabriel.springboot.app.menuflow.exceptions;

public class InvalidInvoiceException extends RuntimeException {

    public InvalidInvoiceException() {
        super("Invoice is null");
    }

    public InvalidInvoiceException(String message) {
        super(message);
    }
}
