package kr.co.lms.web.api.v1;

import kr.co.lms.service.UserService;
import kr.co.lms.vo.UserVO;
import kr.co.lms.web.api.common.ApiAuthUtil;
import kr.co.lms.web.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 사용자 관련 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/users/me          - 내 정보 조회
 * - PUT    /api/v1/users/me          - 내 정보 수정
 * - GET    /api/v1/users             - 전체 사용자 목록 (관리자)
 * - GET    /api/v1/users/{userId}    - 특정 사용자 조회 (관리자)
 * - DELETE /api/v1/users/{userId}    - 사용자 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserApiController {

  private static final Logger logger = LoggerFactory.getLogger(UserApiController.class);

  private final UserService userService;

  /**
   * 내 정보 조회
   * GET /api/v1/users/me
   */
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserVO>> getMyInfo(HttpServletRequest request) {
    String userId = ApiAuthUtil.getCurrentUserId(request);
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);

    logger.info("내 정보 조회 API: userId={}", userId);

    UserVO user = userService.selectUserForLogin(userId, tenantId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
    }

    user.setPassword(null);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * 내 정보 수정
   * PUT /api/v1/users/me
   *
   * userId, tenantId는 JWT에서 강제 설정 (클라이언트 조작 방지)
   * password는 이 API로 변경 불가
   */
  @PutMapping("/me")
  public ResponseEntity<ApiResponse<Void>> updateMyInfo(
      @RequestBody UserVO updateRequest,
      HttpServletRequest request) {

    String userId = ApiAuthUtil.getCurrentUserId(request);
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);

    logger.info("내 정보 수정 API: userId={}", userId);

    updateRequest.setUserId(userId);
    updateRequest.setTenantId(tenantId);
    updateRequest.setPassword(null); // 비밀번호 변경 차단

    int result = userService.updateUser(updateRequest);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 사용자를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "사용자 정보가 수정되었습니다."));
  }

  /**
   * 전체 사용자 목록 조회 (관리자 전용)
   * GET /api/v1/users
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<UserVO>>> getUserList(HttpServletRequest request) {
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    logger.info("전체 사용자 목록 조회 API: tenantId={}", tenantId);

    UserVO searchVO = new UserVO();
    searchVO.setTenantId(tenantId);

    List<UserVO> users = userService.selectUserList(searchVO);
    users.forEach(u -> u.setPassword(null));

    return ResponseEntity.ok(ApiResponse.success(users));
  }

  /**
   * 특정 사용자 조회 (관리자 전용)
   * GET /api/v1/users/{userId}
   */
  @GetMapping("/{userId}")
  public ResponseEntity<ApiResponse<UserVO>> getUser(
      @PathVariable String userId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("특정 사용자 조회 API: userId={}", userId);

    UserVO user = userService.selectUser(userId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("사용자를 찾을 수 없습니다: " + userId));
    }

    user.setPassword(null);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * 사용자 삭제 (관리자 전용)
   * DELETE /api/v1/users/{userId}
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(
      @PathVariable String userId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    // 본인 삭제 방지
    String currentUserId = ApiAuthUtil.getCurrentUserId(request);
    if (userId.equals(currentUserId)) {
      return ResponseEntity.badRequest()
          .body(ApiResponse.error("자기 자신은 삭제할 수 없습니다."));
    }

    logger.info("사용자 삭제 API: userId={}", userId);

    int result = userService.deleteUser(userId);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 사용자를 찾을 수 없습니다: " + userId));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "사용자가 삭제되었습니다."));
  }
}
