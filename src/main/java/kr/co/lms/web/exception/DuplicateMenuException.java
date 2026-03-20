package kr.co.lms.web.exception;

/**
 * 중복 메뉴 등록 예외
 */
public class DuplicateMenuException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public DuplicateMenuException(String message) {
        super(message, 409);
    }
}
