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
import java.util.List;

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
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<MenuVO>>> getMenuList() {
    logger.info("메뉴 목록 조회 API");

    MenuVO searchVO = new MenuVO();
    List<MenuVO> menus = menuService.selectMenuList(searchVO);
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
    String tenantId = ApiAuthUtil.getTenantId(request);
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
    String tenantId = ApiAuthUtil.getTenantId(request);
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
}
