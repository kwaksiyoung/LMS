package kr.co.lms.vo;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 로그인 요청 VO
 */
public class LoginRequestVO implements Serializable {
  private static final long serialVersionUID = 1L;

  @NotBlank(message = "사용자 ID는 필수입니다.")
  private String userId;

  @NotBlank(message = "비밀번호는 필수입니다.")
  private String password;

  @NotBlank(message = "테넌트 ID는 필수입니다.")
  private String tenantId;

  public LoginRequestVO() {}

  public String getUserId() { return userId; }
  public void setUserId(String userId) { this.userId = userId; }

  public String getPassword() { return password; }
  public void setPassword(String password) { this.password = password; }

  public String getTenantId() { return tenantId; }
  public void setTenantId(String tenantId) { this.tenantId = tenantId; }
}
