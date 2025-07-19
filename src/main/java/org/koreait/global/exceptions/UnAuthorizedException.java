package org.koreait.global.exceptions;

import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class UnAuthorizedException extends CommonException {
    public UnAuthorizedException() {
        this("UnAuthorized");
        setErrorCode(true);
    }

    public UnAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }

    public UnAuthorizedException(Map<String, List<String>> errorMessages) {
        super(errorMessages, HttpStatus.UNAUTHORIZED);
    }
}
