package kr.co.lms.web.api.common;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * API 인증/권한 확인 유틸리티
 * JwtAuthenticationFilter에서 request attribute에 저장한 값을 읽습니다.
 */
public class ApiAuthUtil {

  private ApiAuthUtil() {}

  public static String getCurrentUserId(HttpServletRequest request) {
    return (String) request.getAttribute("_jwt_userId");
  }

  public static String getCurrentTenantId(HttpServletRequest request) {
    return (String) request.getAttribute("_jwt_tenantId");
  }

  @SuppressWarnings("unchecked")
  public static List<String> getCurrentRoles(HttpServletRequest request) {
    return (List<String>) request.getAttribute("_jwt_roles");
  }

  public static boolean isAdmin(HttpServletRequest request) {
    List<String> roles = getCurrentRoles(request);
    return roles != null && roles.contains("ROLE_ADMIN");
  }
}
