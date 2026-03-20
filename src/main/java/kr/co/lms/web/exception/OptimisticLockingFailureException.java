package kr.co.lms.web.exception;

/**
 * 낙관적 락 실패 예외 (동시 수정 감지)
 */
public class OptimisticLockingFailureException extends MenuException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 생성자
     * 
     * @param message 에러 메시지
     */
    public OptimisticLockingFailureException(String message) {
        super(message, 409);
    }
}
