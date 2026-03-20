package kr.co.lms.web.exception;

/**
 * 메뉴 조회 실패 예외
 */
public class MenuNotFoundException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public MenuNotFoundException(String message) {
        super(message, 404);
    }
}
