package kr.co.lms.web.api.common;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * ApiExceptionHandler에서 처리됨
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
