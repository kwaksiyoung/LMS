package kr.co.lms.web.api.v1;

import kr.co.lms.service.RoleService;
import kr.co.lms.vo.RoleVO;
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
 * 역할 관리 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/roles              - 역할 목록
 * - GET    /api/v1/roles/{roleCd}     - 역할 상세
 * - POST   /api/v1/roles              - 역할 등록 (관리자)
 * - PUT    /api/v1/roles/{roleCd}     - 역할 수정 (관리자)
 * - DELETE /api/v1/roles/{roleCd}     - 역할 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleApiController {

  private static final Logger logger = LoggerFactory.getLogger(RoleApiController.class);

  private final RoleService roleService;

  /**
   * 역할 목록 조회
   * GET /api/v1/roles
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<RoleVO>>> getRoleList() {
    logger.info("역할 목록 조회 API");

    RoleVO searchVO = new RoleVO();
    List<RoleVO> roles = roleService.selectRoleList(searchVO);
    return ResponseEntity.ok(ApiResponse.success(roles));
  }

  /**
   * 역할 상세 조회
   * GET /api/v1/roles/{roleCd}
   */
  @GetMapping("/{roleCd}")
  public ResponseEntity<ApiResponse<RoleVO>> getRole(
      @PathVariable String roleCd,
      HttpServletRequest request) {
    logger.info("역할 상세 조회 API: roleCd={}", roleCd);

    // JWT 토큰에서 tenantId 추출
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    if (tenantId == null || tenantId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
    }

    RoleVO roleVO = new RoleVO();
    roleVO.setRoleCd(roleCd);
    roleVO.setTenantId(tenantId);
    
    RoleVO role = roleService.selectRole(roleVO);
    if (role == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("역할을 찾을 수 없습니다: " + roleCd));
    }

    return ResponseEntity.ok(ApiResponse.success(role));
  }

  /**
   * 역할 등록 (관리자)
   * POST /api/v1/roles
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createRole(
      @RequestBody RoleVO roleVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("역할 등록 API: roleCd={}", roleVO.getRoleCd());

    int result = roleService.insertRole(roleVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("역할 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "역할이 등록되었습니다."));
  }

  /**
   * 역할 수정 (관리자)
   * PUT /api/v1/roles/{roleCd}
   */
  @PutMapping("/{roleCd}")
  public ResponseEntity<ApiResponse<Void>> updateRole(
      @PathVariable String roleCd,
      @RequestBody RoleVO roleVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    roleVO.setRoleCd(roleCd);
    logger.info("역할 수정 API: roleCd={}", roleCd);

    int result = roleService.updateRole(roleVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 역할을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "역할이 수정되었습니다."));
  }

  /**
   * 역할 삭제 (관리자)
   * DELETE /api/v1/roles/{roleCd}
   */
  @DeleteMapping("/{roleCd}")
  public ResponseEntity<ApiResponse<Void>> deleteRole(
      @PathVariable String roleCd,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("역할 삭제 API: roleCd={}", roleCd);

    // JWT 토큰에서 tenantId 추출
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    if (tenantId == null || tenantId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
    }

    RoleVO roleVO = new RoleVO();
    roleVO.setRoleCd(roleCd);
    roleVO.setTenantId(tenantId);

    int result = roleService.deleteRole(roleVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 역할을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "역할이 삭제되었습니다."));
  }
}
