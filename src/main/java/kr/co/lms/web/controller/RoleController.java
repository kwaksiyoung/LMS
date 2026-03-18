package kr.co.lms.web.controller;

import kr.co.lms.service.RoleService;
import kr.co.lms.vo.RoleVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 역할 관리 Controller
 */
@Controller
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    /**
     * 역할 목록 조회
     */
    @GetMapping("/list")
    public String listRoles(RoleVO roleVO, Model model) {
        logger.debug("역할 목록 조회");
        List<RoleVO> roles = roleService.selectRoleList(roleVO);
        model.addAttribute("roles", roles);
        return "role/list";
    }

    /**
     * 역할 상세 조회
     */
    @GetMapping("/{roleCd}")
    public String getRole(@PathVariable String roleCd, Model model) {
        logger.debug("역할 조회: roleCd={}", roleCd);
        RoleVO role = roleService.selectRole(roleCd);
        model.addAttribute("role", role);
        return "role/detail";
    }

    /**
     * 역할 생성 폼
     */
    @GetMapping("/create")
    public String createForm() {
        logger.debug("역할 생성 폼");
        return "role/create";
    }

    /**
     * 역할 등록
     */
    @PostMapping
    public String createRole(RoleVO roleVO) {
        logger.info("역할 등록: roleCd={}", roleVO.getRoleCd());
        int result = roleService.insertRole(roleVO);
        if (result > 0) {
            logger.info("역할 등록 성공");
            return "redirect:/role/list";
        } else {
            logger.warn("역할 등록 실패");
            return "role/create";
        }
    }

    /**
     * 역할 수정 폼
     */
    @GetMapping("/{roleCd}/edit")
    public String editForm(@PathVariable String roleCd, Model model) {
        logger.debug("역할 수정 폼: roleCd={}", roleCd);
        RoleVO role = roleService.selectRole(roleCd);
        model.addAttribute("role", role);
        return "role/edit";
    }

    /**
     * 역할 수정
     */
    @PutMapping("/{roleCd}")
    public String updateRole(@PathVariable String roleCd, RoleVO roleVO) {
        logger.info("역할 수정: roleCd={}", roleCd);
        roleVO.setRoleCd(roleCd);
        int result = roleService.updateRole(roleVO);
        if (result > 0) {
            logger.info("역할 수정 성공");
            return "redirect:/role/" + roleCd;
        } else {
            logger.warn("역할 수정 실패");
            return "role/edit";
        }
    }

    /**
     * 역할 삭제
     */
    @DeleteMapping("/{roleCd}")
    public String deleteRole(@PathVariable String roleCd) {
        logger.info("역할 삭제: roleCd={}", roleCd);
        int result = roleService.deleteRole(roleCd);
        if (result > 0) {
            logger.info("역할 삭제 성공");
        } else {
            logger.warn("역할 삭제 실패");
        }
        return "redirect:/role/list";
    }
}
