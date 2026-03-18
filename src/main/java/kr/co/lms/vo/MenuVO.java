package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 메뉴 VO (Value Object)
 * 
 * 데이터베이스의 tb_menu 테이블에 매핑되는 값 객체
 * 요청/응답에도 동일하게 사용
 */
public class MenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String menuId;
    private String menuNm;
    private String menuUrl;
    private String menuIcon;
    private Integer sortOrder;
    private String parentMenuId;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;

    // Constructors
    public MenuVO() {
    }

    public MenuVO(String menuId, String menuNm) {
        this.menuId = menuId;
        this.menuNm = menuNm;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public MenuVO(String menuId, String menuNm, String menuUrl) {
        this.menuId = menuId;
        this.menuNm = menuNm;
        this.menuUrl = menuUrl;
        this.useYn = "Y";
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuNm() {
        return menuNm;
    }

    public void setMenuNm(String menuNm) {
        this.menuNm = menuNm;
    }

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getParentMenuId() {
        return parentMenuId;
    }

    public void setParentMenuId(String parentMenuId) {
        this.parentMenuId = parentMenuId;
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
        return "MenuVO{" +
                "menuId='" + menuId + '\'' +
                ", menuNm='" + menuNm + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", sortOrder=" + sortOrder +
                ", useYn='" + useYn + '\'' +
                '}';
    }
}
