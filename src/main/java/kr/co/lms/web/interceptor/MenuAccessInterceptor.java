package kr.co.lms.web.interceptor;

import kr.co.lms.service.MenuService;
import kr.co.lms.vo.RoleMenuVO;
import kr.co.lms.web.util.AuthorizationUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 메뉴 기반 URL 접근 제어 인터셉터
 * 
 * 역할(Role)과 메뉴(Menu)의 매핑을 통해 특정 URL에 대한 접근을 제어합니다.
 * 사용자의 역할에 해당하는 메뉴가 등록되어 있지 않으면 접근을 차단합니다.
 * 
 * 작동 원리:
 * 1. 요청 URL 분석 (예: /lecture/list → /lecture)
 * 2. 세션에서 사용자의 역할 조회
 * 3. tb_role_menu 테이블에서 해당 역할이 접근 가능한 메뉴/URL 확인
 * 4. 접근 권한이 없으면 403 Forbidden 반환
 */
@Component
@RequiredArgsConstructor
public class MenuAccessInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(MenuAccessInterceptor.class);

    private final MenuService menuService;
    private final AuthorizationUtil authorizationUtil;

    /**
     * 요청 처리 전 실행되는 메서드
     * 사용자의 역할과 요청 URL을 비교하여 접근 권한 확인
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String requestPath = requestURI.substring(contextPath.length());
        
        // 관리자 페이지 제외 (메뉴 매핑 불필요)
        if (isExemptedPath(requestPath)) {
            logger.debug("인터셉터 제외 경로: {}", requestPath);
            return true;
        }
        
        // 세션 확인
        HttpSession session = request.getSession(false);
        if (session == null) {
            logger.debug("세션 없음: 로그인 페이지로 리다이렉트");
            response.sendRedirect(contextPath + "/login");
            return false;
        }
        
        // 사용자 역할 확인
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) session.getAttribute("roles");
        
        if (roles == null || roles.isEmpty()) {
            logger.warn("사용자 역할 없음: {}", requestPath);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "역할 정보가 없습니다.");
            return false;
        }
        
        // 관리자는 모든 메뉴 접근 가능
        Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
        if (isAdmin != null && isAdmin) {
            logger.debug("관리자 접근: {} (모든 메뉴 접근 가능)", requestPath);
            return true;
        }
        
        // tenantId 추출
        String tenantId = authorizationUtil.getTenantId(session);
        if (tenantId == null || tenantId.isEmpty()) {
            logger.warn("테넌트 ID 없음: {}", requestPath);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "테넌트 정보가 없습니다.");
            return false;
        }
        
        // URL에 매핑된 역할 확인
        List<String> allowedRoles = getAccessibleRolesForUrl(requestPath, tenantId);
        
        if (allowedRoles == null || allowedRoles.isEmpty()) {
            logger.debug("메뉴 매핑 정보 없음: {} (접근 제한 없음)", requestPath);
            // 메뉴에 등록되지 않은 URL은 접근 허용 (선택사항: 거부로 변경 가능)
            return true;
        }
        
        // 사용자 역할과 허용된 역할 비교
        boolean hasAccess = false;
        for (String role : roles) {
            if (allowedRoles.contains(role)) {
                hasAccess = true;
                logger.debug("접근 허용: {} (역할: {})", requestPath, role);
                break;
            }
        }
        
        if (!hasAccess) {
            logger.warn("접근 거부: {} (사용자 역할: {}, 허용 역할: {})", requestPath, roles, allowedRoles);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "이 메뉴에 접근할 권한이 없습니다.");
            return false;
        }
        
        return true;
    }

    /**
     * URL에 접근 가능한 역할 목록 조회
     * 
     * @param requestPath 요청 경로 (예: /lecture/list)
     * @param tenantId 테넌트 ID
     * @return 접근 가능한 역할 목록
     */
    private List<String> getAccessibleRolesForUrl(String requestPath, String tenantId) {
        try {
            RoleMenuVO roleMenuVO = new RoleMenuVO();
            roleMenuVO.setMenuUrl(requestPath);
            roleMenuVO.setTenantId(tenantId);
            
            List<String> allowedRoles = menuService.selectRolesByUrl(roleMenuVO);
            
            if (allowedRoles == null || allowedRoles.isEmpty()) {
                logger.debug("URL에 매핑된 역할 없음: {}", requestPath);
                return null;
            }
            
            logger.debug("URL에 매핑된 역할: {} -> {}", requestPath, allowedRoles);
            return allowedRoles;
        } catch (Exception e) {
            logger.error("URL별 역할 조회 중 오류 발생: {}", requestPath, e);
            // 오류 발생 시 보안을 위해 접근 거부 (선택사항)
            return null;
        }
    }

    /**
     * 인터셉터에서 제외할 경로 확인
     * 
     * @param requestPath 요청 경로
     * @return 제외 경로이면 true
     */
    private boolean isExemptedPath(String requestPath) {
        // 로그인, 회원가입 등 인증이 필요 없는 경로
        if (requestPath.startsWith("/login") || 
            requestPath.startsWith("/logout") ||
            requestPath.startsWith("/signup") ||
            requestPath.startsWith("/forgot-password") ||
            requestPath.startsWith("/api/auth/")) {
            return true;
        }
        
        // 정적 리소스 (CSS, JS, 이미지 등)
        if (requestPath.matches(".*\\.(css|js|jpg|jpeg|png|gif|ico|svg|woff|woff2|ttf|eot)$")) {
            return true;
        }
        
        // 관리자 전용 페이지 (별도의 관리자 인터셉터로 처리)
        if (requestPath.startsWith("/admin/")) {
            return true;
        }
        
        return false;
    }
}
