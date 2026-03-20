package kr.co.lms.service;

import kr.co.lms.vo.MenuVO;
import kr.co.lms.vo.RoleMenuVO;
import java.util.List;

/**
 * 메뉴 관리 Service 인터페이스
 */
public interface MenuService {
    
    /**
     * 메뉴 조회 (ID로, tenant_id 포함)
     */
    MenuVO selectMenu(MenuVO menuVO);
    
    /**
     * 메뉴 목록 조회
     */
    List<MenuVO> selectMenuList(MenuVO menuVO);
    
    /**
     * 메뉴 목록 조회 (검색/페이징)
     */
    List<MenuVO> selectMenuListWithSearch(MenuVO menuVO);
    
    /**
     * 메뉴 등록
     */
    int insertMenu(MenuVO menuVO);
    
    /**
     * 메뉴 수정
     */
    int updateMenu(MenuVO menuVO);
    
    /**
     * 메뉴 삭제 (논리적 삭제)
     */
    int deleteMenu(MenuVO menuVO);
    
    /**
     * 메뉴 수 조회
     */
    int selectMenuCount(MenuVO menuVO);
    
    /**
     * 메뉴 수 조회 (검색/페이징)
     */
    int selectMenuListWithSearchCount(MenuVO menuVO);
    
    /**
     * 역할별 메뉴 조회 (사용자 로그인 시 메뉴 구성용)
     */
    List<MenuVO> selectMenusByRole(RoleMenuVO roleMenuVO);
    
    /**
     * URL별 접근 가능한 역할 조회 (접근 제어용)
     */
    List<String> selectRolesByUrl(RoleMenuVO roleMenuVO);
    
    /**
     * 메뉴에 매핑된 역할 조회
     */
    List<String> selectRolesByMenu(RoleMenuVO roleMenuVO);
    
    /**
     * 역할-메뉴 매핑 등록 (단일)
     */
    int insertRoleMenu(RoleMenuVO roleMenuVO);
    
    /**
     * 역할-메뉴 매핑 등록 (일괄)
     */
    int insertRoleMenuBatch(List<RoleMenuVO> roleMenuList);
    
    /**
     * 역할-메뉴 매핑 삭제 (특정 역할의 모든 메뉴 매핑 삭제)
     */
    int deleteRoleMenusByRole(RoleMenuVO roleMenuVO);
    
    /**
     * 역할-메뉴 매핑 삭제 (특정 메뉴의 모든 역할 매핑 삭제)
     */
    int deleteRoleMenusByMenu(RoleMenuVO roleMenuVO);
    
    /**
     * 역할-메뉴 매핑 삭제 (단일)
     */
    int deleteRoleMenu(RoleMenuVO roleMenuVO);
    
    /**
     * 역할-메뉴 매핑 존재 여부 확인
     */
    int countRoleMenu(RoleMenuVO roleMenuVO);
    
    /**
     * 역할별 메뉴 수 조회 (특정 역할이 접근 가능한 메뉴 개수)
     */
    int countMenusByRole(RoleMenuVO roleMenuVO);
}
