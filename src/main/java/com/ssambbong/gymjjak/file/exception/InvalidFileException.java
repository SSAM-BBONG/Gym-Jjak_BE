package com.ssambbong.gymjjak.file.exception;

public class InvalidFileException extends FileException {

    public InvalidFileException(FileErrorCode fileErrorCode) {
        super(fileErrorCode);
    }

    public InvalidFileException(FileErrorCode fileErrorCode, String message) {
        super(fileErrorCode, message);
    }
}
