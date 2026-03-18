package kr.co.lms.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 사용자 VO (Value Object)
 * 
 * 데이터베이스의 tb_user 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 * 
 * 암호화 대상 필드:
 * - email: 이메일 (AES-256-GCM 암호화)
 * - phone: 전화번호 (AES-256-GCM 암호화)
 * - address: 주소 (AES-256-GCM 암호화)
 * 
 * 테넌트별 멀티테넌시 지원:
 * - tenantId: 고객사 ID (모든 쿼리에서 WHERE tenantId = ? 조건 필수)
 */
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String userId;
    private String tenantId;        // 고객사 ID (멀티테넌시)
    private String userName;
    private String password;        // 비밀번호 (일방향 암호화 - BCrypt/Argon2)
    private String email;           // 이메일 (양방향 암호화 - AES-256-GCM) ★ 암호화
    private String phone;           // 전화번호 (양방향 암호화 - AES-256-GCM) ★ 암호화
    private String address;         // 주소 (양방향 암호화 - AES-256-GCM) ★ 암호화
    private String deptCd;
    
    // 상태 정보
    private String useYn;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime regDt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updDt;
    
    // 추가 정보 (select 쿼리에서 join)
    private String deptNm;

    // Constructors
    public UserVO() {
    }

    public UserVO(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public UserVO(String userId, String userName, String password, String email) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getDeptCd() {
        return deptCd;
    }

    public void setDeptCd(String deptCd) {
        this.deptCd = deptCd;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }

    public LocalDateTime getUpdDt() {
        return updDt;
    }

    public void setUpdDt(LocalDateTime updDt) {
        this.updDt = updDt;
    }

    public String getDeptNm() {
        return deptNm;
    }

    public void setDeptNm(String deptNm) {
        this.deptNm = deptNm;
    }

    @Override
    public String toString() {
        return "UserVO{" +
                "userId='" + userId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", deptCd='" + deptCd + '\'' +
                ", useYn='" + useYn + '\'' +
                '}';
    }
}
