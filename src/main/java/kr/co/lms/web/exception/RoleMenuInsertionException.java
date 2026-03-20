package kr.co.lms.web.exception;

/**
 * 역할-메뉴 매핑 삽입 실패 예외
 */
public class RoleMenuInsertionException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public RoleMenuInsertionException(String message) {
        super(message, 500);
    }
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     * @param cause 원인 예외
     */
    public RoleMenuInsertionException(String message, Throwable cause) {
        super(message, cause);
    }
}
