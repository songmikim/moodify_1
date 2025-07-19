package org.koreait.global.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class NotFoundException extends CommonException {

    public NotFoundException() {
        this("NotFound");
        setErrorCode(true);
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.NOT_FOUND);
    }

}
