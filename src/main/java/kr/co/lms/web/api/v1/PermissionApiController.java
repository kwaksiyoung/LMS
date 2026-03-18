package kr.co.lms.web.api.v1;

import kr.co.lms.service.PermissionService;
import kr.co.lms.vo.PermissionVO;
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
 * 권한 관리 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/permissions              - 권한 목록
 * - GET    /api/v1/permissions/{permCd}     - 권한 상세
 * - POST   /api/v1/permissions              - 권한 등록 (관리자)
 * - PUT    /api/v1/permissions/{permCd}     - 권한 수정 (관리자)
 * - DELETE /api/v1/permissions/{permCd}     - 권한 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionApiController {

  private static final Logger logger = LoggerFactory.getLogger(PermissionApiController.class);

  private final PermissionService permissionService;

  /**
   * 권한 목록 조회
   * GET /api/v1/permissions
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<PermissionVO>>> getPermissionList() {
    logger.info("권한 목록 조회 API");

    PermissionVO searchVO = new PermissionVO();
    List<PermissionVO> permissions = permissionService.selectPermissionList(searchVO);
    return ResponseEntity.ok(ApiResponse.success(permissions));
  }

  /**
   * 권한 상세 조회
   * GET /api/v1/permissions/{permCd}
   */
  @GetMapping("/{permCd}")
  public ResponseEntity<ApiResponse<PermissionVO>> getPermission(@PathVariable String permCd) {
    logger.info("권한 상세 조회 API: permCd={}", permCd);

    PermissionVO permission = permissionService.selectPermission(permCd);
    if (permission == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("권한을 찾을 수 없습니다: " + permCd));
    }

    return ResponseEntity.ok(ApiResponse.success(permission));
  }

  /**
   * 권한 등록 (관리자)
   * POST /api/v1/permissions
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createPermission(
      @RequestBody PermissionVO permissionVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("권한 등록 API: permCd={}", permissionVO.getPermCd());

    int result = permissionService.insertPermission(permissionVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("권한 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "권한이 등록되었습니다."));
  }

  /**
   * 권한 수정 (관리자)
   * PUT /api/v1/permissions/{permCd}
   */
  @PutMapping("/{permCd}")
  public ResponseEntity<ApiResponse<Void>> updatePermission(
      @PathVariable String permCd,
      @RequestBody PermissionVO permissionVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    permissionVO.setPermCd(permCd);
    logger.info("권한 수정 API: permCd={}", permCd);

    int result = permissionService.updatePermission(permissionVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 권한을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "권한이 수정되었습니다."));
  }

  /**
   * 권한 삭제 (관리자)
   * DELETE /api/v1/permissions/{permCd}
   */
  @DeleteMapping("/{permCd}")
  public ResponseEntity<ApiResponse<Void>> deletePermission(
      @PathVariable String permCd,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("권한 삭제 API: permCd={}", permCd);

    int result = permissionService.deletePermission(permCd);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 권한을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "권한이 삭제되었습니다."));
  }
}
