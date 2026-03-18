package kr.co.lms.web.api.common;

import kr.co.lms.config.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 인증 필터
 *
 * /api/** 경로에 적용되며, 화이트리스트 경로는 인증 없이 통과합니다.
 * 유효한 토큰이면 request attribute에 사용자 정보를 저장합니다.
 *
 * 저장하는 attribute:
 *   - _jwt_userId   : 사용자 ID
 *   - _jwt_tenantId : 테넌트 ID
 *   - _jwt_roles    : 역할 목록 (List<String>)
 *
 * 주의: web.xml에서 직접 Filter로 등록 (DelegatingFilterProxy 미사용)
 *      JwtTokenProvider는 Spring Context에서 lazy 초기화
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  // 인증 없이 접근 가능한 정확한 경로
  private static final List<String> WHITE_LIST = Arrays.asList(
      "/api/v1/auth/login",
      "/api/v1/auth/register",
      "/api/v1/auth/check-userid",
      "/api/v1/health",
      "/api/v1/health/detailed",
      "/api/v1/tenants"
  );

  // 인증 없이 접근 가능한 경로 접두사 (하위 경로 포함 - GET 전용 공개 리소스)
  private static final List<String> WHITE_LIST_PREFIX = Arrays.asList(
      "/api/v1/courses"
  );

  // lazy 초기화 (Filter 생성 시점에 Spring Context 미준비)
  private volatile JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {

    String contextPath = request.getContextPath();
    String requestUri = request.getRequestURI();
    String path = requestUri.substring(contextPath.length());

    // /api/** 경로가 아니면 필터 미적용
    if (!path.startsWith("/api/")) {
      filterChain.doFilter(request, response);
      return;
    }

    // OPTIONS preflight 요청 통과 (CORS 지원)
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    // 화이트리스트 경로는 인증 없이 통과
    if (isWhiteListed(path)) {
      filterChain.doFilter(request, response);
      return;
    }

    // JwtTokenProvider lazy 초기화
    if (jwtTokenProvider == null) {
      synchronized (this) {
        if (jwtTokenProvider == null) {
          WebApplicationContext context = WebApplicationContextUtils
              .getRequiredWebApplicationContext(request.getServletContext());
          jwtTokenProvider = context.getBean(JwtTokenProvider.class);
        }
      }
    }

    // Authorization 헤더에서 토큰 추출
    String token = extractToken(request);

    if (token == null) {
      logger.warn("JWT 토큰 없음: path={}", path);
      sendUnauthorizedResponse(response, "인증 토큰이 필요합니다.");
      return;
    }

    if (!jwtTokenProvider.validateToken(token)) {
      logger.warn("유효하지 않은 JWT 토큰: path={}", path);
      sendUnauthorizedResponse(response, "유효하지 않거나 만료된 토큰입니다.");
      return;
    }

    // 토큰에서 사용자 정보 추출 후 request attribute에 저장
    String userId = jwtTokenProvider.getUserId(token);
    String tenantId = jwtTokenProvider.getTenantId(token);
    List<String> roles = jwtTokenProvider.getRoles(token);

    request.setAttribute("_jwt_userId", userId);
    request.setAttribute("_jwt_tenantId", tenantId);
    request.setAttribute("_jwt_roles", roles);

    logger.debug("JWT 인증 성공: userId={}, tenantId={}, path={}", userId, tenantId, path);

    filterChain.doFilter(request, response);
  }

  private String extractToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

  private boolean isWhiteListed(String path) {
    // 정확한 경로 일치
    if (WHITE_LIST.stream().anyMatch(path::equals)) {
      return true;
    }
    // 접두사 일치 (하위 경로 포함) - GET 요청만 공개
    return WHITE_LIST_PREFIX.stream().anyMatch(path::startsWith);
  }

  private void sendUnauthorizedResponse(HttpServletResponse response, String message)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json;charset=UTF-8");
    String timestamp = LocalDateTime.now().toString();
    String body = String.format(
        "{\"success\":false,\"message\":\"%s\",\"timestamp\":\"%s\"}",
        message, timestamp
    );
    response.getWriter().write(body);
  }
}
