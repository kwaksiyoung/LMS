package kr.co.lms.web.api.v1;

import kr.co.lms.config.JwtTokenProvider;
import kr.co.lms.service.UserService;
import kr.co.lms.vo.*;
import kr.co.lms.web.api.common.ApiAuthUtil;
import kr.co.lms.web.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 인증 관련 REST API 컨트롤러
 *
 * 엔드포인트:
 * - POST /api/v1/auth/login          - 로그인 (JWT 발급)
 * - POST /api/v1/auth/logout         - 로그아웃
 * - GET  /api/v1/auth/me             - 현재 사용자 정보
 * - POST /api/v1/auth/register       - 회원가입
 * - POST /api/v1/auth/check-userid   - 아이디 중복 확인
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthApiController {

  private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);

  private final UserService userService;
  private final JwtTokenProvider jwtTokenProvider;

  @Value("${jwt.expiration}")
  private long jwtExpiration;

  /**
   * 로그인 API
   * POST /api/v1/auth/login
   *
   * 인증 성공 시 JWT 토큰 발급
   */
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponseVO>> login(
      @Valid @RequestBody LoginRequestVO loginRequest,
      BindingResult bindingResult) {

    logger.info("로그인 API 요청: userId={}, tenantId={}", loginRequest.getUserId(), loginRequest.getTenantId());

    if (bindingResult.hasErrors()) {
      StringBuilder errorMessage = new StringBuilder();
      bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
      return ResponseEntity.badRequest()
          .body(ApiResponse.error(errorMessage.toString()));
    }

    try {
      UserVO user = userService.authenticateUser(
          loginRequest.getUserId(),
          loginRequest.getPassword(),
          loginRequest.getTenantId()
      );

      if (user == null) {
        logger.warn("로그인 실패: userId={}", loginRequest.getUserId());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("아이디 또는 비밀번호가 올바르지 않습니다."));
      }

      List<String> roles = userService.selectUserRoles(user.getUserId(), user.getTenantId());
      String token = jwtTokenProvider.createToken(user.getUserId(), user.getTenantId(), roles);

      LoginResponseVO response = LoginResponseVO.of(token, jwtExpiration, user, roles);
      logger.info("로그인 성공: userId={}, tenantId={}, roles={}", user.getUserId(), user.getTenantId(), roles);

      return ResponseEntity.ok(ApiResponse.success(response, "로그인 성공"));

    } catch (Exception e) {
      logger.error("로그인 처리 중 오류: userId={}, error={}", loginRequest.getUserId(), e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("로그인 처리 중 오류가 발생했습니다."));
    }
  }

  /**
   * 로그아웃 API
   * POST /api/v1/auth/logout
   *
   * JWT는 stateless이므로 클라이언트에서 토큰을 삭제해야 합니다.
   */
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout() {
    return ResponseEntity.ok(
        ApiResponse.success(null, "로그아웃 되었습니다. 클라이언트에서 토큰을 삭제해주세요.")
    );
  }

  /**
   * 현재 사용자 정보 조회
   * GET /api/v1/auth/me
   */
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<UserVO>> getMe(HttpServletRequest request) {
    String userId = ApiAuthUtil.getCurrentUserId(request);
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);

    UserVO user = userService.selectUserForLogin(userId, tenantId);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
    }

    user.setPassword(null);
    return ResponseEntity.ok(ApiResponse.success(user));
  }

  /**
   * 회원가입 API
   * POST /api/v1/auth/register
   */
  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponseVO>> register(
      @Valid @RequestBody RegisterRequestVO registerRequest,
      BindingResult bindingResult) {

    logger.info("회원가입 API 요청: userId={}, tenantId={}", registerRequest.getUserId(), registerRequest.getTenantId());

    if (bindingResult.hasErrors()) {
      StringBuilder errorMessage = new StringBuilder();
      bindingResult.getAllErrors().forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, null, errorMessage.toString()));
    }

    try {
      RegisterResponseVO response = userService.registerUser(registerRequest);

      if (response.isSuccess()) {
        logger.info("회원가입 성공: userId={}", registerRequest.getUserId());
        return ResponseEntity.ok(new ApiResponse<>(true, response, response.getMessage()));
      } else {
        logger.warn("회원가입 실패: {}", response.getMessage());
        return ResponseEntity.badRequest()
            .body(new ApiResponse<>(false, null, response.getMessage()));
      }

    } catch (Exception e) {
      logger.error("회원가입 중 오류: userId={}, error={}", registerRequest.getUserId(), e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, null, "회원가입 중 오류가 발생했습니다."));
    }
  }

  /**
   * 아이디 중복 확인 API
   * POST /api/v1/auth/check-userid
   */
  @PostMapping("/check-userid")
  public ResponseEntity<ApiResponse<Map<String, Object>>> checkUserIdDuplicate(
      @RequestBody Map<String, String> request) {

    String userId = request.get("userId");
    String tenantId = request.get("tenantId");

    logger.info("아이디 중복 확인 API 요청: userId={}, tenantId={}", userId, tenantId);

    if (userId == null || userId.trim().isEmpty() ||
        tenantId == null || tenantId.trim().isEmpty()) {
      return ResponseEntity.badRequest()
          .body(new ApiResponse<>(false, null, "userId와 tenantId는 필수입니다."));
    }

    try {
      boolean isDuplicate = userService.isUserIdDuplicate(userId, tenantId);

      Map<String, Object> data = new HashMap<>();
      data.put("isDuplicate", isDuplicate);
      data.put("userId", userId);

      String message = isDuplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.";
      return ResponseEntity.ok(new ApiResponse<>(true, data, message));

    } catch (Exception e) {
      logger.error("아이디 중복 확인 중 오류: userId={}, error={}", userId, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, null, "아이디 중복 확인 중 오류가 발생했습니다."));
    }
  }
}
