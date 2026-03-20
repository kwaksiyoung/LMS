package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 역할-메뉴 매핑 VO (Value Object)
 * 
 * 데이터베이스의 tb_role_menu 테이블에 매핑되는 값 객체
 * 역할과 메뉴의 다대다 관계를 나타냄
 * 
 * 테이블 구조:
 * - role_cd (FK)
 * - menu_id (FK)
 * - tenant_id (FK)
 * - reg_dt
 */
public class RoleMenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보 (PK)
    private String roleCd;
    private String menuId;
    private String tenantId;
    private LocalDateTime regDt;

    // 관계 데이터 (조회 시 포함)
    private String roleNm;          // tb_role에서 조인
    private String menuNm;          // tb_menu에서 조인
    private String menuUrl;         // tb_menu에서 조인
    
    // 검색/필터링 필드
    private String searchKeyword;
    private Integer startRow;
    private Integer pageSize;
    private Integer totalCount;

    // Constructors
    public RoleMenuVO() {
    }

    public RoleMenuVO(String roleCd, String menuId) {
        this.roleCd = roleCd;
        this.menuId = menuId;
        this.regDt = LocalDateTime.now();
    }

    public RoleMenuVO(String roleCd, String menuId, String tenantId) {
        this.roleCd = roleCd;
        this.menuId = menuId;
        this.tenantId = tenantId;
        this.regDt = LocalDateTime.now();
    }

    // Getters and Setters - 기본 정보
    public String getRoleCd() {
        return roleCd;
    }

    public void setRoleCd(String roleCd) {
        this.roleCd = roleCd;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getRegDt() {
        return regDt;
    }

    public void setRegDt(LocalDateTime regDt) {
        this.regDt = regDt;
    }

    // Getters and Setters - 관계 데이터
    public String getRoleNm() {
        return roleNm;
    }

    public void setRoleNm(String roleNm) {
        this.roleNm = roleNm;
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

    // Getters and Setters - 검색/필터링
    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public Integer getStartRow() {
        return startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "RoleMenuVO{" +
                "roleCd='" + roleCd + '\'' +
                ", menuId='" + menuId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", roleNm='" + roleNm + '\'' +
                ", menuNm='" + menuNm + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                '}';
    }
}
