package kr.co.lms.web.util;

import kr.co.lms.mapper.UserMapper;
import kr.co.lms.vo.UserVO;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;

/**
 * 권한 체크 유틸리티
 * 
 * HttpSession에 저장된 사용자 정보를 기반으로 권한을 확인합니다.
 */
@Component
public class AuthorizationUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationUtil.class);
    
    private final UserMapper userMapper;
    
    public AuthorizationUtil(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * 사용자가 ROLE_ADMIN 권한을 가지고 있는지 확인
     * 
     * @param session 사용자 세션
     * @return true: 관리자, false: 일반 사용자
     */
    public boolean isAdmin(HttpSession session) {
        Object isAdminObj = session.getAttribute("isAdmin");
        
        if (isAdminObj == null) {
            logger.debug("세션에 isAdmin 정보 없음");
            return false;
        }
        
        return (Boolean) isAdminObj;
    }
    
    /**
     * 사용자가 특정 역할을 가지고 있는지 확인
     * 
     * @param session 사용자 세션
     * @param roleCode 역할 코드 (예: ROLE_ADMIN, ROLE_STUDENT)
     * @return true: 해당 역할 있음, false: 없음
     */
    @SuppressWarnings("unchecked")
    public boolean hasRole(HttpSession session, String roleCode) {
        Object rolesObj = session.getAttribute("roles");
        
        if (rolesObj == null) {
            logger.debug("세션에 역할 정보 없음");
            return false;
        }
        
        try {
            java.util.List<String> roles = (java.util.List<String>) rolesObj;
            return roles.contains(roleCode);
        } catch (Exception e) {
            logger.error("역할 확인 중 오류: roleCode={}, error={}", roleCode, e.getMessage());
            return false;
        }
    }
    
    /**
     * 로그인 여부 확인
     * 
     * @param session 사용자 세션
     * @return true: 로그인함, false: 미로그인
     */
    public boolean isLoggedIn(HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        return userId != null && !userId.isEmpty();
    }
    
    /**
     * 세션에서 사용자 ID 추출
     */
    public String getUserId(HttpSession session) {
        return (String) session.getAttribute("userId");
    }
    
    /**
     * 세션에서 테넌트 ID 추출
     */
    public String getTenantId(HttpSession session) {
        return (String) session.getAttribute("tenantId");
    }
}
