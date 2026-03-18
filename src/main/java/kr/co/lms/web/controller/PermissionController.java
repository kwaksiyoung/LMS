package kr.co.lms.web.controller;

import kr.co.lms.service.PermissionService;
import kr.co.lms.vo.PermissionVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 권한 관리 Controller
 */
@Controller
@RequestMapping("/permission")
@RequiredArgsConstructor
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    /**
     * 권한 목록 조회
     */
    @GetMapping("/list")
    public String listPermissions(PermissionVO permissionVO, Model model) {
        logger.debug("권한 목록 조회");
        List<PermissionVO> permissions = permissionService.selectPermissionList(permissionVO);
        model.addAttribute("permissions", permissions);
        return "permission/list";
    }

    /**
     * 권한 상세 조회
     */
    @GetMapping("/{permCd}")
    public String getPermission(@PathVariable String permCd, Model model) {
        logger.debug("권한 조회: permCd={}", permCd);
        PermissionVO permission = permissionService.selectPermission(permCd);
        model.addAttribute("permission", permission);
        return "permission/detail";
    }

    /**
     * 권한 생성 폼
     */
    @GetMapping("/create")
    public String createForm() {
        logger.debug("권한 생성 폼");
        return "permission/create";
    }

    /**
     * 권한 등록
     */
    @PostMapping
    public String createPermission(PermissionVO permissionVO) {
        logger.info("권한 등록: permCd={}", permissionVO.getPermCd());
        int result = permissionService.insertPermission(permissionVO);
        if (result > 0) {
            logger.info("권한 등록 성공");
            return "redirect:/permission/list";
        } else {
            logger.warn("권한 등록 실패");
            return "permission/create";
        }
    }

    /**
     * 권한 수정 폼
     */
    @GetMapping("/{permCd}/edit")
    public String editForm(@PathVariable String permCd, Model model) {
        logger.debug("권한 수정 폼: permCd={}", permCd);
        PermissionVO permission = permissionService.selectPermission(permCd);
        model.addAttribute("permission", permission);
        return "permission/edit";
    }

    /**
     * 권한 수정
     */
    @PutMapping("/{permCd}")
    public String updatePermission(@PathVariable String permCd, PermissionVO permissionVO) {
        logger.info("권한 수정: permCd={}", permCd);
        permissionVO.setPermCd(permCd);
        int result = permissionService.updatePermission(permissionVO);
        if (result > 0) {
            logger.info("권한 수정 성공");
            return "redirect:/permission/" + permCd;
        } else {
            logger.warn("권한 수정 실패");
            return "permission/edit";
        }
    }

    /**
     * 권한 삭제
     */
    @DeleteMapping("/{permCd}")
    public String deletePermission(@PathVariable String permCd) {
        logger.info("권한 삭제: permCd={}", permCd);
        int result = permissionService.deletePermission(permCd);
        if (result > 0) {
            logger.info("권한 삭제 성공");
        } else {
            logger.warn("권한 삭제 실패");
        }
        return "redirect:/permission/list";
    }
}
