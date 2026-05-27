package com.ssambbong.gymjjak.file.exception;

public class FileUploadException extends FileException {

    public FileUploadException() {
        super(FileErrorCode.FILE_UPLOAD_FAILED);
    }

    public FileUploadException(Throwable cause) {
        super(FileErrorCode.FILE_UPLOAD_FAILED, cause);
    }
}
