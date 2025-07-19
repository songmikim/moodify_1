package org.koreait.global.exceptions.script;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlertRedirectException extends AlertException {
    private final String target;
    private final String url;

    public AlertRedirectException(String message, String url, HttpStatus status, String target) {
        super(message, status);
        this.url = url;
        this.target = target;
    }

    public AlertRedirectException(String message, String url, HttpStatus status) {
        this(message, url, status, "self");
    }

    public AlertRedirectException(String message, String url, String target) {
        this(message, url, HttpStatus.BAD_REQUEST, target);
    }

    public AlertRedirectException(String message, String url) {
        this(message, url, HttpStatus.BAD_REQUEST);
    }
}
