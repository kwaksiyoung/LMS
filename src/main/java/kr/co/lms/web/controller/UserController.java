package kr.co.lms.web.controller;

import kr.co.lms.service.UserService;
import kr.co.lms.vo.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * 사용자 관리 Controller (JSP용)
 */
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * 사용자 목록 페이지
     */
    @GetMapping("/list")
    public String list(Model model) {
        logger.info("사용자 목록 페이지 요청");
        return "user/list";
    }

    /**
     * 사용자 조회 페이지
     */
    @GetMapping("/view")
    public String view(String userId, Model model) {
        logger.info("사용자 조회 페이지: userId={}", userId);
        UserVO user = userService.selectUser(userId);
        model.addAttribute("user", user);
        return "user/view";
    }

    /**
     * 사용자 등록 폼
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        logger.info("사용자 등록 폼 요청");
        model.addAttribute("user", new UserVO());
        return "user/form";
    }

    /**
     * 사용자 등록 처리
     */
    @PostMapping("/create")
    public String create(UserVO userVO) {
        logger.info("사용자 등록 처리: userId={}", userVO.getUserId());
        userService.insertUser(userVO);
        return "redirect:/user/list";
    }

    /**
     * 사용자 수정 폼
     */
    @GetMapping("/edit")
    public String editForm(String userId, Model model) {
        logger.info("사용자 수정 폼: userId={}", userId);
        UserVO user = userService.selectUser(userId);
        model.addAttribute("user", user);
        return "user/form";
    }

    /**
     * 사용자 수정 처리
     */
    @PostMapping("/edit")
    public String edit(UserVO userVO) {
        logger.info("사용자 수정 처리: userId={}", userVO.getUserId());
        userService.updateUser(userVO);
        return "redirect:/user/view?userId=" + userVO.getUserId();
    }

    /**
     * 사용자 삭제
     */
    @PostMapping("/delete")
    public String delete(String userId) {
        logger.info("사용자 삭제: userId={}", userId);
        userService.deleteUser(userId);
        return "redirect:/user/list";
    }

    /**
     * 내 정보 조회
     */
    @GetMapping("/myinfo")
    public String myInfo(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("userId");
        logger.info("내 정보 조회: userId={}", userId);
        UserVO user = userService.selectUser(userId);
        model.addAttribute("user", user);
        return "user/myinfo";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/register")
    public String registerForm(Model model) {
        logger.info("회원가입 페이지 요청");
        return "user/register";
    }


}
