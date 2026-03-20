package kr.co.lms.web.exception;

/**
 * 메뉴 관련 기본 예외 클래스
 */
public class MenuException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    private final int httpStatusCode;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public MenuException(String message) {
        super(message);
        this.httpStatusCode = 400;
    }
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     * @param cause 원인 예외
     */
    public MenuException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatusCode = 400;
    }
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     * @param httpStatusCode HTTP 상태 코드
     */
    public MenuException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }
    
    /**
     * HTTP 상태 코드 조회
     * 
     * @return HTTP 상태 코드
     */
    public int getHttpStatus() {
        return this.httpStatusCode;
    }
}
