package com.gabriel.springboot.app.menuflow.exceptions;

import static com.gabriel.springboot.app.menuflow.constants.ExceptionConstants.INVALID_INVOICE_MESSAGE;

public class InvalidInvoiceException extends RuntimeException {

    public InvalidInvoiceException() {
        super(INVALID_INVOICE_MESSAGE);
    }

    public InvalidInvoiceException(String message) {
        super(message);
    }
}
