package kr.co.lms.web.exception;

import kr.co.lms.web.api.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 메뉴 관리 전역 예외 처리기
 * 
 * REST API의 모든 예외를 일관된 JSON 형식으로 처리
 * 응답 형식: { "success": false, "message": "에러 메시지", "data": null }
 */
@RestControllerAdvice(basePackages = "kr.co.lms.web.api")
public class MenuExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuExceptionHandler.class);
    
    /**
     * 메뉴 관련 비즈니스 예외 처리
     * 
     * @param e MenuException
     * @return 400 Bad Request
     */
    @ExceptionHandler(MenuException.class)
    public ResponseEntity<ApiResponse<Void>> handleMenuException(MenuException e) {
        logger.warn("메뉴 비즈니스 예외: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 메뉴 조회 실패 (404 Not Found)
     * 
     * @param e MenuNotFoundException
     * @return 404 Not Found
     */
    @ExceptionHandler(MenuNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleMenuNotFound(MenuNotFoundException e) {
        logger.warn("메뉴 조회 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("메뉴를 찾을 수 없습니다."));
    }
    
    /**
     * 중복 메뉴 등록 시도 (409 Conflict)
     * 
     * @param e DuplicateMenuException
     * @return 409 Conflict
     */
    @ExceptionHandler(DuplicateMenuException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateMenu(DuplicateMenuException e) {
        logger.warn("중복 메뉴 등록 시도: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("이미 등록된 메뉴입니다."));
    }
    
    /**
     * 부모 메뉴 조회 실패 (400 Bad Request)
     * 메뉴 등록/수정 시 부모 메뉴가 없을 때
     * 
     * @param e ParentMenuNotFoundException
     * @return 400 Bad Request
     */
    @ExceptionHandler(ParentMenuNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleParentMenuNotFound(ParentMenuNotFoundException e) {
        logger.warn("부모 메뉴 조회 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("부모 메뉴를 찾을 수 없습니다."));
    }
    
    /**
     * 자식 메뉴 존재로 인한 삭제 불가 (409 Conflict)
     * 
     * @param e MenuHasChildrenException
     * @return 409 Conflict
     */
    @ExceptionHandler(MenuHasChildrenException.class)
    public ResponseEntity<ApiResponse<Void>> handleMenuHasChildren(MenuHasChildrenException e) {
        logger.warn("자식 메뉴 존재로 삭제 불가: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("자식 메뉴가 있어서 삭제할 수 없습니다."));
    }
    
    /**
     * 메뉴 삭제 실패 (500 Internal Server Error)
     * 
     * @param e MenuDeletionException
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(MenuDeletionException.class)
    public ResponseEntity<ApiResponse<Void>> handleMenuDeletionException(MenuDeletionException e) {
        logger.error("메뉴 삭제 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("메뉴 삭제에 실패했습니다."));
    }
    
    /**
     * 역할-메뉴 매핑 삽입 실패 (500 Internal Server Error)
     * 
     * @param e RoleMenuInsertionException
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(RoleMenuInsertionException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleMenuInsertionException(RoleMenuInsertionException e) {
        logger.error("역할-메뉴 매핑 등록 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("역할-메뉴 매핑 등록에 실패했습니다."));
    }
    
    /**
     * 동시 수정 감지 (409 Conflict)
     * 낙관적 락 실패 시
     * 
     * @param e OptimisticLockingFailureException
     * @return 409 Conflict
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailure(
            OptimisticLockingFailureException e) {
        logger.warn("동시 수정 감지: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse.error("메뉴가 다른 사용자에 의해 수정되었습니다. 다시 시도해주세요."));
    }
    
    /**
     * 데이터베이스 접근 오류 (500 Internal Server Error)
     * 
     * @param e DataAccessException
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(
            org.springframework.dao.DataAccessException e) {
        logger.error("DB 접근 중 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("데이터베이스 작업 중 오류가 발생했습니다."));
    }
    
    /**
     * 잘못된 요청 (400 Bad Request)
     * 필수 파라미터 누락 등
     * 
     * @param e IllegalArgumentException
     * @return 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        logger.warn("잘못된 인자: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(e.getMessage()));
    }
    
    /**
     * 모든 예상 외 예외 처리 (500 Internal Server Error)
     * 
     * @param e Exception
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        logger.error("예상치 못한 오류 발생", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("서버 오류가 발생했습니다. 관리자에게 문의하세요."));
    }
}
