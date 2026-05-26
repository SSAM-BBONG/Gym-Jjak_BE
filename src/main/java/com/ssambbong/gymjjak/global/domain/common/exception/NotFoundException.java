package com.ssambbong.gymjjak.global.domain.common.exception;

// 도메인 예외 시, 호출하는 중간 클래스
// 404 리소스 찾을 수 없을 때 에러
public abstract class NotFoundException extends BusinessException {

  protected NotFoundException(ErrorCode errorCode) {
    super(errorCode, errorCode.getMessage());
  }

  protected NotFoundException(ErrorCode errorCode, String message) {
    super(errorCode, message);
  }

  protected NotFoundException(ErrorCode errorCode, String message, Throwable cause) {
    super(errorCode, message, cause);
  }
}
