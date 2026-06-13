package com.ssambbong.gymjjak.file.exception;

public class FileAccessDeniedException extends FileException {

    public FileAccessDeniedException() {
        super(FileErrorCode.FILE_ACCESS_DENIED);
    }
}
