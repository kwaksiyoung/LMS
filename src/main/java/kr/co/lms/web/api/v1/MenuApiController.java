package kr.co.lms.web.api.v1;

import kr.co.lms.service.MenuService;
import kr.co.lms.vo.MenuVO;
import kr.co.lms.web.api.common.ApiAuthUtil;
import kr.co.lms.web.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 메뉴 관리 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/menus              - 메뉴 목록
 * - GET    /api/v1/menus/{menuId}     - 메뉴 상세
 * - POST   /api/v1/menus              - 메뉴 등록 (관리자)
 * - PUT    /api/v1/menus/{menuId}     - 메뉴 수정 (관리자)
 * - DELETE /api/v1/menus/{menuId}     - 메뉴 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuApiController {

  private static final Logger logger = LoggerFactory.getLogger(MenuApiController.class);

  private final MenuService menuService;

  /**
   * 메뉴 목록 조회
   * GET /api/v1/menus
   * 
   * tenantId를 JWT 토큰에서 추출하여 멀티테넌시 처리
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<MenuVO>>> getMenuList(HttpServletRequest request) {
    logger.info("메뉴 목록 조회 API");

    // JWT 토큰에서 tenantId 추출
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    if (tenantId == null || tenantId.isEmpty()) {
      logger.warn("테넌트 정보를 찾을 수 없습니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
    }

    MenuVO searchVO = new MenuVO();
    searchVO.setTenantId(tenantId);  // 🔴 CRITICAL: tenantId 반드시 설정
    searchVO.setUseYn("Y");  // 사용 중인 메뉴만 조회
    
    List<MenuVO> menus = menuService.selectMenuList(searchVO);
    logger.info("메뉴 목록 조회 성공: {}건 (tenantId={})", menus.size(), tenantId);
    
    return ResponseEntity.ok(ApiResponse.success(menus));
  }

  /**
   * 메뉴 상세 조회
   * GET /api/v1/menus/{menuId}
   */
  @GetMapping("/{menuId}")
  public ResponseEntity<ApiResponse<MenuVO>> getMenu(
      @PathVariable String menuId,
      HttpServletRequest request) {
    logger.info("메뉴 상세 조회 API: menuId={}", menuId);

    // JWT 토큰에서 tenantId 추출
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    if (tenantId == null || tenantId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
    }

    MenuVO menuVO = new MenuVO();
    menuVO.setMenuId(menuId);
    menuVO.setTenantId(tenantId);
    
    MenuVO menu = menuService.selectMenu(menuVO);
    if (menu == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("메뉴를 찾을 수 없습니다: " + menuId));
    }

    return ResponseEntity.ok(ApiResponse.success(menu));
  }

  /**
   * 메뉴 등록 (관리자)
   * POST /api/v1/menus
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createMenu(
      @RequestBody MenuVO menuVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("메뉴 등록 API: menuId={}", menuVO.getMenuId());

    int result = menuService.insertMenu(menuVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("메뉴 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "메뉴가 등록되었습니다."));
  }

  /**
   * 메뉴 수정 (관리자)
   * PUT /api/v1/menus/{menuId}
   */
  @PutMapping("/{menuId}")
  public ResponseEntity<ApiResponse<Void>> updateMenu(
      @PathVariable String menuId,
      @RequestBody MenuVO menuVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    menuVO.setMenuId(menuId);
    logger.info("메뉴 수정 API: menuId={}", menuId);

    int result = menuService.updateMenu(menuVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 메뉴를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "메뉴가 수정되었습니다."));
  }

  /**
   * 메뉴 삭제 (관리자)
   * DELETE /api/v1/menus/{menuId}
   */
  @DeleteMapping("/{menuId}")
  public ResponseEntity<ApiResponse<Void>> deleteMenu(
      @PathVariable String menuId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("메뉴 삭제 API: menuId={}", menuId);

    // JWT 토큰에서 tenantId 추출
    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    if (tenantId == null || tenantId.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
    }

    MenuVO menuVO = new MenuVO();
    menuVO.setMenuId(menuId);
    menuVO.setTenantId(tenantId);

    int result = menuService.deleteMenu(menuVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 메뉴를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "메뉴가 삭제되었습니다."));
  }

  /**
   * 메뉴 벌크 등록 (관리자)
   * POST /api/v1/menus/batch
   */
  @PostMapping("/batch")
  public ResponseEntity<ApiResponse<Map<String, Object>>> createMenuBatch(
      @RequestBody List<MenuVO> menus,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("메뉴 벌크 등록 API 호출: 개수={}", menus != null ? menus.size() : 0);

    try {
      // JWT 토큰에서 tenantId 추출
      String tenantId = ApiAuthUtil.getCurrentTenantId(request);
      if (tenantId == null || tenantId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
      }

      // 모든 메뉴에 tenantId 설정
      for (MenuVO menu : menus) {
        menu.setTenantId(tenantId);
      }

      int count = menuService.insertMenuBatch(menus);
      
      Map<String, Object> result = new HashMap<>();
      result.put("success", count > 0);
      result.put("message", count > 0 ? "메뉴 벌크 등록 성공" : "메뉴 벌크 등록 실패");
      result.put("count", count);

      if (count == 0) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error("메뉴 벌크 등록에 실패했습니다."));
      }

      return ResponseEntity.status(HttpStatus.CREATED)
          .body(ApiResponse.success(result, "메뉴가 일괄 등록되었습니다."));

    } catch (RuntimeException e) {
      logger.error("메뉴 벌크 등록 중 오류 발생", e);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("success", false);
      errorResult.put("message", e.getMessage());
      errorResult.put("count", 0);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * 메뉴 순서 변경 (관리자)
   * PUT /api/v1/menus/{menuId}/sort
   */
  @PutMapping("/{menuId}/sort")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateMenuSortOrder(
      @PathVariable String menuId,
      @RequestBody Map<String, Integer> requestBody,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("메뉴 순서 변경 API 호출: menuId={}", menuId);

    try {
      // JWT 토큰에서 tenantId 추출
      String tenantId = ApiAuthUtil.getCurrentTenantId(request);
      if (tenantId == null || tenantId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
      }

      Integer newSortOrder = requestBody.get("newSortOrder");
      if (newSortOrder == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("newSortOrder 값이 필요합니다."));
      }

      int result = menuService.updateMenuSortOrder(menuId, newSortOrder, tenantId);
      
      Map<String, Object> resultMap = new HashMap<>();
      resultMap.put("success", result > 0);
      resultMap.put("message", result > 0 ? "메뉴 순서 변경 성공" : "메뉴 순서 변경 실패");

      if (result == 0) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("변경할 메뉴를 찾을 수 없습니다."));
      }

      return ResponseEntity.ok(ApiResponse.success(resultMap, "메뉴 순서가 변경되었습니다."));

    } catch (RuntimeException e) {
      logger.error("메뉴 순서 변경 중 오류 발생", e);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("success", false);
      errorResult.put("message", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * 메뉴 일괄 활성화/비활성화 (관리자)
   * PUT /api/v1/menus/useYn
   */
  @PutMapping("/useYn")
  public ResponseEntity<ApiResponse<Map<String, Object>>> updateMenuUseYnBatch(
      @RequestBody Map<String, Object> requestBody,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("메뉴 일괄 활성화/비활성화 API 호출");

    try {
      // JWT 토큰에서 tenantId 추출
      String tenantId = ApiAuthUtil.getCurrentTenantId(request);
      if (tenantId == null || tenantId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
      }

      @SuppressWarnings("unchecked")
      List<String> menuIds = (List<String>) requestBody.get("menuIds");
      String useYn = (String) requestBody.get("useYn");

      if (menuIds == null || menuIds.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("menuIds 배열이 필요합니다."));
      }

      if (useYn == null || (!useYn.equals("Y") && !useYn.equals("N"))) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error("useYn 값은 Y 또는 N이어야 합니다."));
      }

      int count = menuService.updateMenuUseYnBatch(menuIds, useYn, tenantId);
      
      Map<String, Object> result = new HashMap<>();
      result.put("success", count > 0);
      result.put("message", count > 0 ? "메뉴 상태 변경 성공" : "메뉴 상태 변경 실패");
      result.put("count", count);

      if (count == 0) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error("변경할 메뉴를 찾을 수 없습니다."));
      }

      return ResponseEntity.ok(ApiResponse.success(result, "메뉴 상태가 변경되었습니다."));

    } catch (RuntimeException e) {
      logger.error("메뉴 상태 변경 중 오류 발생", e);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("success", false);
      errorResult.put("message", e.getMessage());
      errorResult.put("count", 0);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }

  /**
   * 메뉴별 역할 조회
   * GET /api/v1/menus/{menuId}/roles
   */
  @GetMapping("/{menuId}/roles")
  public ResponseEntity<ApiResponse<Map<String, Object>>> getMenuRoles(
      @PathVariable String menuId,
      HttpServletRequest request) {

    logger.info("메뉴별 역할 조회 API 호출: menuId={}", menuId);

    try {
      // JWT 토큰에서 tenantId 추출
      String tenantId = ApiAuthUtil.getCurrentTenantId(request);
      if (tenantId == null || tenantId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("테넌트 정보를 찾을 수 없습니다."));
      }

      kr.co.lms.vo.RoleMenuVO roleMenuVO = new kr.co.lms.vo.RoleMenuVO();
      roleMenuVO.setMenuId(menuId);
      roleMenuVO.setTenantId(tenantId);

      List<String> roles = menuService.selectRolesByMenu(roleMenuVO);
      
      Map<String, Object> result = new HashMap<>();
      result.put("success", true);
      result.put("roles", roles);

      return ResponseEntity.ok(ApiResponse.success(result, "메뉴별 역할 조회 성공"));

    } catch (RuntimeException e) {
      logger.error("메뉴별 역할 조회 중 오류 발생", e);
      Map<String, Object> errorResult = new HashMap<>();
      errorResult.put("success", false);
      errorResult.put("roles", new java.util.ArrayList<>());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error(e.getMessage()));
    }
  }
}
