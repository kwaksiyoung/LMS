package kr.co.lms.web.controller;

import kr.co.lms.service.UserService;
import kr.co.lms.vo.UserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * 인증 관련 Controller (로그인/로그아웃)
 * 
 * 요청 경로:
 * - GET  /auth/login      : 로그인 폼 페이지
 * - POST /auth/login      : 로그인 처리
 * - GET  /auth/logout     : 로그아웃 처리
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    /**
     * 로그인 페이지 (GET)
     */
    @GetMapping("/login")
    public String loginForm() {
        logger.info("로그인 페이지 요청");
        return "login/login";
    }

    /**
     * 로그인 처리 (POST)
     * 
     * 프로세스:
     * 1. userId와 password 입력받음
     * 2. 인증 수행 (authenticateUser)
     * 3. 성공 시: 세션에 사용자 정보 + 역할 정보 저장
     * 4. 실패 시: 로그인 페이지로 리다이렉트 (error 파라미터 포함)
     */
    @PostMapping("/login")
    public String login(String userId, String password, String tenantId, HttpSession session) {
        logger.info("로그인 처리: userId={}, tenantId={}", userId, tenantId);
        
        // 입력값 검증
        if (userId == null || userId.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            tenantId == null || tenantId.trim().isEmpty()) {
            logger.warn("로그인 실패: 입력값 누락");
            return "redirect:/auth/login?error=true";
        }
        
        try {
            // 사용자 인증
            UserVO user = userService.authenticateUser(userId, password, tenantId);
            
            if (user != null) {
                // 인증 성공 - 세션에 사용자 정보 저장
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("tenantId", user.getTenantId());
                session.setAttribute("userName", user.getUserName());
                session.setAttribute("userEmail", user.getEmail());
                
                // 사용자의 역할 정보도 세션에 저장
                java.util.List<String> roles = userService.selectUserRoles(user.getUserId(), user.getTenantId());
                session.setAttribute("roles", roles);
                
                // 관리자 여부 플래그 설정
                boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");
                session.setAttribute("isAdmin", isAdmin);
                
                logger.info("로그인 성공: userId={}, tenantId={}, roles={}, isAdmin={}", 
                           userId, user.getTenantId(), roles, isAdmin);
                
                // 디버깅: 세션 정보 확인
                logger.info("=== Session 저장 확인 ===");
                logger.info("Session ID: {}", session.getId());
                logger.info("Session userId: {}", session.getAttribute("userId"));
                logger.info("Session tenantId: {}", session.getAttribute("tenantId"));
                logger.info("Session isAdmin: {}", session.getAttribute("isAdmin"));
                logger.info("========================");
                
                // 홈으로 리다이렉트
                return "redirect:/";
            } else {
                // 인증 실패
                logger.warn("로그인 실패: 인증 실패 - userId={}", userId);
                return "redirect:/auth/login?error=true";
            }
            
        } catch (Exception e) {
            logger.error("로그인 처리 중 오류: userId={}, error={}", userId, e.getMessage(), e);
            return "redirect:/auth/login?error=true";
        }
    }

    /**
     * 로그아웃 처리 (GET)
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("로그아웃 처리");
        session.invalidate();
        return "redirect:/auth/login";
    }
}
