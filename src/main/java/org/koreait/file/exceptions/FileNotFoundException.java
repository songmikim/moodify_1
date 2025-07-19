package org.koreait.file.exceptions;

import org.koreait.global.exceptions.NotFoundException;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException() {
        super("NotFound.file");
        setErrorCode(true);
    }
}
