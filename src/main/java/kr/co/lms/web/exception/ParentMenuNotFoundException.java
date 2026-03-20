package kr.co.lms.web.exception;

/**
 * 부모 메뉴 조회 실패 예외
 */
public class ParentMenuNotFoundException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public ParentMenuNotFoundException(String message) {
        super(message, 400);
    }
}
