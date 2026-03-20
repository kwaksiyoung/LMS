package kr.co.lms.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 메뉴 VO (Value Object)
 * 
 * 데이터베이스의 tb_menu 테이블에 매핑되는 값 객체
 * 요청/응답, 검색/페이징, 역할 매핑에 사용
 */
public class MenuVO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 기본 정보
    private String menuId;
    private String tenantId;
    private String menuNm;
    private String menuUrl;
    private String menuIcon;
    private Integer sortOrder;
    private Integer menuLevel;
    private String parentMenuId;
    private String useYn;
    private LocalDateTime regDt;
    private LocalDateTime updDt;
    
    // 검색/필터링/페이징 필드
    private String searchKeyword;
    private Integer startRow;
    private Integer pageSize;
    private Integer totalCount;
    private Integer totalPages;
    private Integer currentPage;
    
    // 관계 데이터
    private java.util.List<String> selectedRoles;     // 선택된 역할 ID 리스트
    private java.util.List<MenuVO> childMenus;        // 하위 메뉴 리스트

    // Constructors
    public MenuVO() {
    }

    public MenuVO(String menuId, String menuNm) {
        this.menuId = menuId;
        this.menuNm = menuNm;
        this.useYn = "Y";
        this.sortOrder = 0;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    public MenuVO(String menuId, String menuNm, String menuUrl) {
        this.menuId = menuId;
        this.menuNm = menuNm;
        this.menuUrl = menuUrl;
        this.useYn = "Y";
        this.sortOrder = 0;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }
    
    public MenuVO(String menuId, String tenantId, String menuNm, String menuUrl) {
        this.menuId = menuId;
        this.tenantId = tenantId;
        this.menuNm = menuNm;
        this.menuUrl = menuUrl;
        this.useYn = "Y";
        this.sortOrder = 0;
        this.regDt = LocalDateTime.now();
        this.updDt = LocalDateTime.now();
    }

    // Getters and Setters - 기본 정보
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

    public Integer getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(Integer menuLevel) {
        this.menuLevel = menuLevel;
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

    // Getters and Setters - 검색/필터링/페이징
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

    public Integer getTotalPages() {
        if (this.totalCount != null && this.pageSize != null && this.pageSize > 0) {
            return (this.totalCount + this.pageSize - 1) / this.pageSize;
        }
        return 0;
    }

    public void setTotalPages(Integer totalPages) {
        // 읽기 전용 (getTotalPages에서 자동 계산)
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    // Getters and Setters - 관계 데이터
    public java.util.List<String> getSelectedRoles() {
        return selectedRoles;
    }

    public void setSelectedRoles(java.util.List<String> selectedRoles) {
        this.selectedRoles = selectedRoles;
    }

    public java.util.List<MenuVO> getChildMenus() {
        return childMenus;
    }

    public void setChildMenus(java.util.List<MenuVO> childMenus) {
        this.childMenus = childMenus;
    }

    @Override
    public String toString() {
        return "MenuVO{" +
                "menuId='" + menuId + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", menuNm='" + menuNm + '\'' +
                ", menuUrl='" + menuUrl + '\'' +
                ", sortOrder=" + sortOrder +
                ", menuLevel=" + menuLevel +
                ", useYn='" + useYn + '\'' +
                ", searchKeyword='" + searchKeyword + '\'' +
                '}';
    }
}
