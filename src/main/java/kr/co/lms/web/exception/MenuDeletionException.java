package kr.co.lms.web.exception;

/**
 * 메뉴 삭제 실패 예외
 */
public class MenuDeletionException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public MenuDeletionException(String message) {
        super(message, 500);
    }
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     * @param cause 원인 예외
     */
    public MenuDeletionException(String message, Throwable cause) {
        super(message, cause);
    }
}
