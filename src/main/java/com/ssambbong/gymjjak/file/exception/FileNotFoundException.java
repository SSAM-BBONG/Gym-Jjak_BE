package com.ssambbong.gymjjak.file.exception;

public class FileNotFoundException extends FileException {

    public FileNotFoundException() {
        super(FileErrorCode.FILE_NOT_FOUND);
    }

    public FileNotFoundException(Long fileId) {
        super(FileErrorCode.FILE_NOT_FOUND,
                FileErrorCode.FILE_NOT_FOUND.getMessage() + " fileId: " + fileId);
    }
}
