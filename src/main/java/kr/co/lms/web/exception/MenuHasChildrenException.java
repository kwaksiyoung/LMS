package kr.co.lms.web.exception;

/**
 * 자식 메뉴가 있어서 삭제 불가 예외
 */
public class MenuHasChildrenException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public MenuHasChildrenException(String message) {
        super(message, 409);
    }
}
