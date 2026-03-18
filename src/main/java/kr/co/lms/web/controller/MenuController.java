package kr.co.lms.web.controller;

import kr.co.lms.service.MenuService;
import kr.co.lms.vo.MenuVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 메뉴 관리 Controller
 */
@Controller
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;

    /**
     * 메뉴 목록 조회
     */
    @GetMapping("/list")
    public String listMenus(MenuVO menuVO, Model model) {
        logger.debug("메뉴 목록 조회");
        List<MenuVO> menus = menuService.selectMenuList(menuVO);
        model.addAttribute("menus", menus);
        return "menu/list";
    }

    /**
     * 메뉴 상세 조회
     */
    @GetMapping("/{menuId}")
    public String getMenu(@PathVariable String menuId, Model model) {
        logger.debug("메뉴 조회: menuId={}", menuId);
        MenuVO menu = menuService.selectMenu(menuId);
        model.addAttribute("menu", menu);
        return "menu/detail";
    }

    /**
     * 메뉴 생성 폼
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        logger.debug("메뉴 생성 폼");
        List<MenuVO> parentMenus = menuService.selectMenuList(new MenuVO());
        model.addAttribute("parentMenus", parentMenus);
        return "menu/create";
    }

    /**
     * 메뉴 등록
     */
    @PostMapping
    public String createMenu(MenuVO menuVO) {
        logger.info("메뉴 등록: menuId={}", menuVO.getMenuId());
        int result = menuService.insertMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 등록 성공");
            return "redirect:/menu/list";
        } else {
            logger.warn("메뉴 등록 실패");
            return "menu/create";
        }
    }

    /**
     * 메뉴 수정 폼
     */
    @GetMapping("/{menuId}/edit")
    public String editForm(@PathVariable String menuId, Model model) {
        logger.debug("메뉴 수정 폼: menuId={}", menuId);
        MenuVO menu = menuService.selectMenu(menuId);
        List<MenuVO> parentMenus = menuService.selectMenuList(new MenuVO());
        model.addAttribute("menu", menu);
        model.addAttribute("parentMenus", parentMenus);
        return "menu/edit";
    }

    /**
     * 메뉴 수정
     */
    @PutMapping("/{menuId}")
    public String updateMenu(@PathVariable String menuId, MenuVO menuVO) {
        logger.info("메뉴 수정: menuId={}", menuId);
        menuVO.setMenuId(menuId);
        int result = menuService.updateMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 수정 성공");
            return "redirect:/menu/" + menuId;
        } else {
            logger.warn("메뉴 수정 실패");
            return "menu/edit";
        }
    }

    /**
     * 메뉴 삭제
     */
    @DeleteMapping("/{menuId}")
    public String deleteMenu(@PathVariable String menuId) {
        logger.info("메뉴 삭제: menuId={}", menuId);
        int result = menuService.deleteMenu(menuId);
        if (result > 0) {
            logger.info("메뉴 삭제 성공");
        } else {
            logger.warn("메뉴 삭제 실패");
        }
        return "redirect:/menu/list";
    }
}
