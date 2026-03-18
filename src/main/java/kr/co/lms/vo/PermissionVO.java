package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 권한 VO (Value Object)
 * 
 * 데이터베이스의 tb_permission 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 */
public class PermissionVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String permCd;
    private String permNm;
    private String permDesc;
    private String resourceUrl;
    private String httpMethod;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public PermissionVO() {
    }

    public PermissionVO(String permCd, String permNm) {
        this.permCd = permCd;
        this.permNm = permNm;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public PermissionVO(String permCd, String permNm, String resourceUrl, String httpMethod) {
        this.permCd = permCd;
        this.permNm = permNm;
        this.resourceUrl = resourceUrl;
        this.httpMethod = httpMethod;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getPermCd() {
        return permCd;
    }

    public void setPermCd(String permCd) {
        this.permCd = permCd;
    }

    public String getPermNm() {
        return permNm;
    }

    public void setPermNm(String permNm) {
        this.permNm = permNm;
    }

    public String getPermDesc() {
        return permDesc;
    }

    public void setPermDesc(String permDesc) {
        this.permDesc = permDesc;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
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
        return "PermissionVO{" +
                "permCd='" + permCd + '\'' +
                ", permNm='" + permNm + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", useYn='" + useYn + '\'' +
                '}';
    }
}
