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
     * 메뉴 조회 (ID + tenant_id로)
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
     * 메뉴 일괄 등록 (벌크 인서트)
     */
    int insertMenuBatch(List<MenuVO> menuList);
    
    /**
     * 메뉴 순서 변경 (sort_order 업데이트)
     */
    int updateMenuSortOrder(MenuVO menuVO);
    
    /**
     * 메뉴 사용여부 일괄 변경 (여러 메뉴의 use_yn 업데이트)
     */
    int updateMenuUseYnBatch(@Param("menuIds") List<String> menuIds, 
                             @Param("useYn") String useYn, 
                             @Param("tenantId") String tenantId);
}
