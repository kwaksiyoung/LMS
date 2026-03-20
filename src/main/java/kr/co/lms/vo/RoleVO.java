package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 역할 VO (Value Object)
 * 
 * 데이터베이스의 tb_role 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 */
public class RoleVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String roleCd;
    private String tenantId;    // 멀티테넌시
    private String roleNm;
    private String roleDesc;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public RoleVO() {
    }

    public RoleVO(String roleCd, String roleNm) {
        this.roleCd = roleCd;
        this.roleNm = roleNm;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public RoleVO(String roleCd, String roleNm, String roleDesc) {
        this.roleCd = roleCd;
        this.roleNm = roleNm;
        this.roleDesc = roleDesc;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getRoleCd() {
        return roleCd;
    }

    public void setRoleCd(String roleCd) {
        this.roleCd = roleCd;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRoleNm() {
        return roleNm;
    }

    public void setRoleNm(String roleNm) {
        this.roleNm = roleNm;
    }

    public String getRoleDesc() {
        return roleDesc;
    }

    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
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

    @Override
    public String toString() {
        return "RoleVO{" +
                "roleCd='" + roleCd + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", roleNm='" + roleNm + '\'' +
                ", roleDesc='" + roleDesc + '\'' +
                ", useYn='" + useYn + '\'' +
                '}';
    }
}
