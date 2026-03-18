package kr.co.lms.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 회원가입 응답 VO
 * 
 * 회원가입 결과를 클라이언트에 전달하는 응답 객체
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponseVO {
    
    private boolean success;
    private String message;
    private String userId;
    private String email;
    private String userName;
    private String tenantId;

    // ============ Constructor ============
    public RegisterResponseVO() {
    }

    public RegisterResponseVO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RegisterResponseVO(boolean success, String message, String userId, 
                            String email, String userName, String tenantId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.tenantId = tenantId;
    }

    // ============ Getters and Setters ============
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "RegisterResponseVO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
