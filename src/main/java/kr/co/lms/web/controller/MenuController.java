package kr.co.lms.web.controller;

import kr.co.lms.service.MenuService;
import kr.co.lms.service.RoleService;
import kr.co.lms.vo.MenuVO;
import kr.co.lms.vo.RoleMenuVO;
import kr.co.lms.vo.RoleVO;
import kr.co.lms.web.util.AuthorizationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 메뉴 관리 Controller
 * 역할 기반 접근 제어(RBAC)를 통한 메뉴 관리 기능 제공
 */
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;
    private final RoleService roleService;
    private final AuthorizationUtil authorizationUtil;

    private static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 메뉴 목록 조회 (검색/페이징 포함)
     */
    @GetMapping("/list")
    public String listMenus(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            MenuVO menuVO,
            HttpSession session,
            Model model) {
        
        logger.debug("메뉴 목록 조회: page={}, keyword={}", page, keyword);
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        menuVO.setTenantId(tenantId);
        
        // 검색/페이징 설정
        menuVO.setCurrentPage(page);
        menuVO.setSearchKeyword(keyword);
        menuVO.setPageSize(DEFAULT_PAGE_SIZE);
        menuVO.setStartRow((page - 1) * DEFAULT_PAGE_SIZE);
        
        // 메뉴 목록 및 총 개수 조회
        List<MenuVO> menus = menuService.selectMenuListWithSearch(menuVO);
        int totalCount = menuService.selectMenuListWithSearchCount(menuVO);
        int totalPages = (totalCount + DEFAULT_PAGE_SIZE - 1) / DEFAULT_PAGE_SIZE;
        
        // 모델 설정
        model.addAttribute("menus", menus);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("pageSize", DEFAULT_PAGE_SIZE);
        
        logger.debug("메뉴 목록 조회 완료: totalCount={}, totalPages={}", totalCount, totalPages);
        return "menu/list";
    }

    /**
     * 메뉴 상세 조회
     */
    @GetMapping("/{menuId}")
    public String getMenu(
            @PathVariable String menuId,
            HttpSession session,
            Model model) {
        
        logger.debug("메뉴 상세 조회: menuId={}", menuId);
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuId(menuId);
        menuVO.setTenantId(tenantId);
        MenuVO menu = menuService.selectMenu(menuVO);
        
        if (menu == null) {
            logger.warn("메뉴를 찾을 수 없음: menuId={}, tenantId={}", menuId, tenantId);
            return "redirect:/menu/list";
        }
        
        model.addAttribute("menu", menu);
        return "menu/detail";
    }

    /**
     * 메뉴 생성 폼
     */
    @GetMapping("/create")
    public String createForm(
            HttpSession session,
            Model model) {
        
        logger.debug("메뉴 생성 폼 요청");
        
        // 관리자 권한 확인
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 메뉴 생성 폼 접근 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/menu/list";
        }
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        
        // 부모 메뉴 목록 조회
        MenuVO searchVO = new MenuVO();
        searchVO.setTenantId(tenantId);
        searchVO.setUseYn("Y");
        List<MenuVO> parentMenus = menuService.selectMenuList(searchVO);
        
        // 역할 목록 조회 (메뉴에 매핑할 역할)
        RoleVO roleVO = new RoleVO();
        roleVO.setTenantId(tenantId);
        roleVO.setUseYn("Y");
        List<RoleVO> roles = roleService.selectRoleList(roleVO);
        
        model.addAttribute("parentMenus", parentMenus);
        model.addAttribute("roles", roles);
        
        return "menu/create";
    }

    /**
     * 메뉴 등록 (역할 매핑 포함)
     */
    @PostMapping
    public String createMenu(
            MenuVO menuVO,
            @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles,
            HttpSession session,
            Model model) {
        
        logger.info("메뉴 등록: menuId={}", menuVO.getMenuId());
        
        // 관리자 권한 확인
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 메뉴 등록 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/menu/list";
        }
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        menuVO.setTenantId(tenantId);
        menuVO.setSelectedRoles(selectedRoles);
        
        try {
            // 메뉴 등록
            int result = menuService.insertMenu(menuVO);
            if (result > 0) {
                // 역할-메뉴 매핑 등록
                if (selectedRoles != null && !selectedRoles.isEmpty()) {
                    List<RoleMenuVO> roleMenuList = new java.util.ArrayList<>();
                    for (String roleCd : selectedRoles) {
                        RoleMenuVO roleMenuVO = new RoleMenuVO();
                        roleMenuVO.setRoleCd(roleCd);
                        roleMenuVO.setMenuId(menuVO.getMenuId());
                        roleMenuVO.setTenantId(tenantId);
                        roleMenuList.add(roleMenuVO);
                    }
                    menuService.insertRoleMenuBatch(roleMenuList);
                    logger.info("역할-메뉴 매핑 등록 완료: {}개 역할 매핑", selectedRoles.size());
                }
                
                logger.info("메뉴 등록 성공: menuId={}", menuVO.getMenuId());
                return "redirect:/menu/list";
            } else {
                logger.warn("메뉴 등록 실패: menuId={}", menuVO.getMenuId());
                model.addAttribute("errorMessage", "메뉴 등록에 실패했습니다.");
                return "menu/create";
            }
        } catch (Exception e) {
            logger.error("메뉴 등록 중 오류 발생", e);
            model.addAttribute("errorMessage", "메뉴 등록 중 오류가 발생했습니다.");
            return "menu/create";
        }
    }

    /**
     * 메뉴 수정 폼
     */
    @GetMapping("/{menuId}/edit")
    public String editForm(
            @PathVariable String menuId,
            HttpSession session,
            Model model) {
        
        logger.debug("메뉴 수정 폼: menuId={}", menuId);
        
        // 관리자 권한 확인
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 메뉴 수정 폼 접근 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/menu/list";
        }
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuId(menuId);
        menuVO.setTenantId(tenantId);
        MenuVO menu = menuService.selectMenu(menuVO);
        
        if (menu == null) {
            logger.warn("메뉴를 찾을 수 없음: menuId={}", menuId);
            return "redirect:/menu/list";
        }
        
        // 부모 메뉴 목록 조회
        MenuVO searchVO = new MenuVO();
        searchVO.setTenantId(tenantId);
        searchVO.setUseYn("Y");
        List<MenuVO> parentMenus = menuService.selectMenuList(searchVO);
        
        // 역할 목록 조회
        RoleVO roleVO = new RoleVO();
        roleVO.setTenantId(tenantId);
        roleVO.setUseYn("Y");
        List<RoleVO> roles = roleService.selectRoleList(roleVO);
        
        // 현재 메뉴에 매핑된 역할 조회
        RoleMenuVO roleMenuVO = new RoleMenuVO();
        roleMenuVO.setMenuId(menuId);
        roleMenuVO.setTenantId(tenantId);
        List<String> selectedRoles = menuService.selectRolesByMenu(roleMenuVO);
        
        model.addAttribute("menu", menu);
        model.addAttribute("parentMenus", parentMenus);
        model.addAttribute("roles", roles);
        model.addAttribute("selectedRoles", selectedRoles);
        
        return "menu/edit";
    }

    /**
     * 메뉴 수정 (역할 매핑 변경 포함)
     * 
     * HTML form은 GET/POST만 지원하므로 @PostMapping 사용
     * REST API는 @PutMapping 사용 (MenuApiController에서 처리)
     */
    @PostMapping("/{menuId}")
    public String updateMenu(
            @PathVariable String menuId,
            MenuVO menuVO,
            @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles,
            HttpSession session,
            Model model) {
        
        logger.info("메뉴 수정: menuId={}", menuId);
        
        // 관리자 권한 확인
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 메뉴 수정 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/menu/list";
        }
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        menuVO.setMenuId(menuId);
        menuVO.setTenantId(tenantId);
        menuVO.setSelectedRoles(selectedRoles);
        
        try {
            // 메뉴 수정
            int result = menuService.updateMenu(menuVO);
            if (result > 0) {
                // 기존 역할-메뉴 매핑 삭제 후 새로 등록
                RoleMenuVO roleMenuVO = new RoleMenuVO();
                roleMenuVO.setMenuId(menuId);
                roleMenuVO.setTenantId(tenantId);
                menuService.deleteRoleMenusByMenu(roleMenuVO);
                
                if (selectedRoles != null && !selectedRoles.isEmpty()) {
                    List<RoleMenuVO> roleMenuList = new java.util.ArrayList<>();
                    for (String roleCd : selectedRoles) {
                        RoleMenuVO newRoleMenuVO = new RoleMenuVO();
                        newRoleMenuVO.setRoleCd(roleCd);
                        newRoleMenuVO.setMenuId(menuId);
                        newRoleMenuVO.setTenantId(tenantId);
                        roleMenuList.add(newRoleMenuVO);
                    }
                    menuService.insertRoleMenuBatch(roleMenuList);
                    logger.info("역할-메뉴 매핑 업데이트 완료: {}개 역할 매핑", selectedRoles.size());
                }
                
                logger.info("메뉴 수정 성공: menuId={}", menuId);
                return "redirect:/menu/" + menuId;
            } else {
                logger.warn("메뉴 수정 실패: menuId={}", menuId);
                model.addAttribute("errorMessage", "메뉴 수정에 실패했습니다.");
                return "menu/edit";
            }
        } catch (Exception e) {
            logger.error("메뉴 수정 중 오류 발생", e);
            model.addAttribute("errorMessage", "메뉴 수정 중 오류가 발생했습니다.");
            return "menu/edit";
        }
    }

    /**
     * 메뉴 삭제
     * 
     * HTML form은 GET/POST만 지원하므로 @PostMapping 사용
     * URL: POST /menu/{menuId}/delete
     * REST API는 @DeleteMapping 사용 (MenuApiController에서 처리)
     */
    @PostMapping("/{menuId}/delete")
    public String deleteMenu(
            @PathVariable String menuId,
            HttpSession session,
            Model model) {
        
        logger.info("메뉴 삭제: menuId={}", menuId);
        
        // 관리자 권한 확인
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 메뉴 삭제 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/menu/list";
        }
        
        // 세션에서 tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuId(menuId);
        menuVO.setTenantId(tenantId);
        
        try {
            int result = menuService.deleteMenu(menuVO);
            if (result > 0) {
                logger.info("메뉴 삭제 성공: menuId={}", menuId);
            } else {
                logger.warn("메뉴 삭제 실패: menuId={}", menuId);
            }
        } catch (Exception e) {
            logger.error("메뉴 삭제 중 오류 발생", e);
            model.addAttribute("errorMessage", "메뉴 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/menu/list";
    }
}
