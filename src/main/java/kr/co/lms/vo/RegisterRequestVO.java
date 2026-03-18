package kr.co.lms.vo;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 회원가입 요청 VO
 * 
 * 회원가입 시 클라이언트에서 전송하는 데이터
 * 검증 규칙:
 * - userId: 4-20자, 영문, 숫자, 언더스코어만 허용
 * - password: 8자 이상, 영문 대소문자, 숫자, 특수문자 포함
 * - email: 유효한 이메일 형식
 * - userName: 1-50자, 한글, 영문 허용
 * - phone: 10-11자, 숫자와 하이픈 허용
 * - address: 1-255자
 * - tenantId: 테넌트 ID (필수)
 */
public class RegisterRequestVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]{4,20}$",
        message = "사용자 ID는 4-20자이며 영문, 숫자, 언더스코어만 사용 가능합니다."
    )
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z\\d!@#$%^&*]{8,}$",
        message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;

    @NotBlank(message = "사용자명은 필수입니다.")
    @Size(min = 1, max = 50, message = "사용자명은 1-50자입니다.")
    private String userName;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Pattern(
        regexp = "^\\d{3}-\\d{3,4}-\\d{4}$|^\\d{10,11}$",
        message = "전화번호는 010-1234-5678 형식이거나 숫자만 입력해주세요."
    )
    private String phone;

    @Size(max = 255, message = "주소는 255자 이하여야 합니다.")
    private String address;

    @NotBlank(message = "테넌트 ID는 필수입니다.")
    private String tenantId;

    // ============ Constructor ============
    public RegisterRequestVO() {
    }

    public RegisterRequestVO(String userId, String password, String userName, 
                            String email, String tenantId) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.tenantId = tenantId;
    }

    // ============ Getters and Setters ============
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String toString() {
        return "RegisterRequestVO{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", tenantId='" + tenantId + '\'' +
                '}';
    }
}
