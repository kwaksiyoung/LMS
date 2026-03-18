package kr.co.lms.service;

import kr.co.lms.vo.MenuVO;
import java.util.List;

/**
 * 메뉴 관리 Service 인터페이스
 */
public interface MenuService {
    
    /**
     * 메뉴 조회 (ID로)
     */
    MenuVO selectMenu(String menuId);
    
    /**
     * 메뉴 목록 조회
     */
    List<MenuVO> selectMenuList(MenuVO menuVO);
    
    /**
     * 메뉴 등록
     */
    int insertMenu(MenuVO menuVO);
    
    /**
     * 메뉴 수정
     */
    int updateMenu(MenuVO menuVO);
    
    /**
     * 메뉴 삭제
     */
    int deleteMenu(String menuId);
    
    /**
     * 메뉴 수 조회
     */
    int selectMenuCount(MenuVO menuVO);
}
