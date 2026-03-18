package kr.co.lms.mapper;

import kr.co.lms.vo.MenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 메뉴 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 */
@Mapper
public interface MenuMapper {
    
    /**
     * 메뉴 조회 (ID로)
     */
    MenuVO selectMenu(@Param("menuId") String menuId);
    
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
    int deleteMenu(@Param("menuId") String menuId);
    
    /**
     * 메뉴 수 조회
     */
    int selectMenuCount(MenuVO menuVO);
}
