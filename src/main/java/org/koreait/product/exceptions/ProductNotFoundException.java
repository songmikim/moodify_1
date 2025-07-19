package org.koreait.product.exceptions;

import org.koreait.global.exceptions.script.AlertBackException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends AlertBackException {
    public ProductNotFoundException() {
        super("NotFound.product", HttpStatus.NOT_FOUND);
        setErrorCode(true);
    }
}
