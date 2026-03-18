package kr.co.lms.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 로그인 응답 VO
 * JWT 토큰과 사용자 기본 정보 반환
 */
public class LoginResponseVO implements Serializable {
  private static final long serialVersionUID = 1L;

  private String accessToken;
  private String tokenType;
  private long expiresIn;  // 초 단위
  private String userId;
  private String userName;
  private String tenantId;
  private List<String> roles;

  public LoginResponseVO() {}

  public static LoginResponseVO of(String token, long expiresInMs,
                                   UserVO user, List<String> roles) {
    LoginResponseVO vo = new LoginResponseVO();
    vo.accessToken = token;
    vo.tokenType = "Bearer";
    vo.expiresIn = expiresInMs / 1000;
    vo.userId = user.getUserId();
    vo.userName = user.getUserName();
    vo.tenantId = user.getTenantId();
    vo.roles = roles;
    return vo;
  }

  public String getAccessToken() { return accessToken; }
  public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

  public String getTokenType() { return tokenType; }
  public void setTokenType(String tokenType) { this.tokenType = tokenType; }

  public long getExpiresIn() { return expiresIn; }
  public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public String getUserName() { return userName; }
  public void setUserName(String userName) { this.userName = userName; }

  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }

  public List<String> getRoles() { return roles; }
  public void setRoles(List<String> roles) { this.roles = roles; }
}
