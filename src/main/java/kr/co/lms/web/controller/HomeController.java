package kr.co.lms.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈페이지 컨트롤러
 * "/" 및 "/index" 요청을 처리합니다.
 */
@Slf4j
@Controller
public class HomeController {
    
    /**
     * 홈페이지 (주석 처리 - web.xml의 welcome-file-list가 처리)
     * GET / → /src/main/webapp/index.jsp (정적 파일 제공)
     *
     * 참고: dispatcher servlet이 "/" 패턴을 처리하지만,
     * 매핑되는 핸들러가 없으면 welcome-file-list의 index.jsp가 로드됨
     */
    // @GetMapping({"/", "/index"})
    // public String index() {
    //     log.debug("홈페이지 접속");
    //     return "index";
    // }
    
    /**
     * 로그인 페이지 리다이렉트
     * GET /login → /user/login (UserController로 리다이렉트)
     */
    @GetMapping("/login")
    public String loginRedirect() {
        log.debug("로그인 페이지 리다이렉트");
        return "redirect:/user/login";
    }
}
