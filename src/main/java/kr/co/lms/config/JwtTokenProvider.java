package kr.co.lms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * JWT 토큰 생성 및 검증 유틸리티
 * jjwt 0.11.5 API 기준
 *
 * 토큰 클레임: sub(userId), tenantId, roles, iat, exp
 */
@Component
public class JwtTokenProvider {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expiration; // 밀리초 단위 (기본 3600000 = 1시간)

  private Key key;

  @PostConstruct
  public void init() {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    this.key = Keys.hmacShaKeyFor(keyBytes);
    logger.info("JwtTokenProvider 초기화 완료, 만료시간={}ms", expiration);
  }

  /**
   * JWT 토큰 생성
   */
  public String createToken(String userId, String tenantId, List<String> roles) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    return Jwts.builder()
        .setSubject(userId)
        .claim("tenantId", tenantId)
        .claim("roles", roles)
        .setIssuedAt(now)
        .setExpiration(expiryDate)
        .signWith(key)
        .compact();
  }

  /**
   * 토큰에서 userId 추출
   */
  public String getUserId(String token) {
    return parseClaims(token).getSubject();
  }

  /**
   * 토큰에서 tenantId 추출
   */
  public String getTenantId(String token) {
    return (String) parseClaims(token).get("tenantId");
  }

  /**
   * 토큰에서 roles 추출
   */
  @SuppressWarnings("unchecked")
  public List<String> getRoles(String token) {
    return (List<String>) parseClaims(token).get("roles");
  }

  /**
   * 토큰 만료 시간 반환 (밀리초)
   */
  public long getExpiration() {
    return expiration;
  }

  /**
   * 토큰 유효성 검증
   */
  public boolean validateToken(String token) {
    try {
      parseClaims(token);
      return true;
    } catch (ExpiredJwtException e) {
      logger.warn("만료된 JWT 토큰: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      logger.warn("지원하지 않는 JWT 토큰: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      logger.warn("잘못된 형식의 JWT 토큰: {}", e.getMessage());
    } catch (io.jsonwebtoken.security.SecurityException e) {
      logger.warn("JWT 서명 검증 실패: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      logger.warn("JWT 토큰이 비어있음: {}", e.getMessage());
    }
    return false;
  }

  private Claims parseClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
