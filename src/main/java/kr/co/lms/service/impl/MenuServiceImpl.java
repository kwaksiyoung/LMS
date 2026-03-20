package kr.co.lms.service.impl;

import kr.co.lms.mapper.MenuMapper;
import kr.co.lms.mapper.RoleMenuMapper;
import kr.co.lms.service.MenuService;
import kr.co.lms.vo.MenuVO;
import kr.co.lms.vo.RoleMenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 메뉴 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    /**
     * 메뉴 조회 (ID로, tenant_id 포함)
     */
    @Override
    @Transactional(readOnly = true)
    public MenuVO selectMenu(MenuVO menuVO) {
        logger.debug("메뉴 조회: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        return menuMapper.selectMenu(menuVO);
    }

    /**
     * 메뉴 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<MenuVO> selectMenuList(MenuVO menuVO) {
        logger.debug("메뉴 목록 조회: tenantId={}", menuVO.getTenantId());
        return menuMapper.selectMenuList(menuVO);
    }

    /**
     * 메뉴 목록 조회 (검색/페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public List<MenuVO> selectMenuListWithSearch(MenuVO menuVO) {
        logger.debug("메뉴 목록 조회 (검색/페이징): tenantId={}, keyword={}, page={}",
                menuVO.getTenantId(), menuVO.getSearchKeyword(), menuVO.getCurrentPage());
        return menuMapper.selectMenuListWithSearch(menuVO);
    }

    /**
     * 메뉴 등록
     */
    @Override
    public int insertMenu(MenuVO menuVO) {
        logger.info("메뉴 등록: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        int result = menuMapper.insertMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 등록 성공: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        } else {
            logger.warn("메뉴 등록 실패: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        }
        return result;
    }

    /**
     * 메뉴 수정
     */
    @Override
    public int updateMenu(MenuVO menuVO) {
        logger.info("메뉴 수정: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        int result = menuMapper.updateMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 수정 성공: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        } else {
            logger.warn("메뉴 수정 실패: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        }
        return result;
    }

    /**
     * 메뉴 삭제 (논리적 삭제)
     */
    @Override
    public int deleteMenu(MenuVO menuVO) {
        logger.info("메뉴 삭제: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        
        // 메뉴 삭제 전 해당 메뉴의 역할 매핑 삭제
        RoleMenuVO roleMenuVO = new RoleMenuVO();
        roleMenuVO.setMenuId(menuVO.getMenuId());
        roleMenuVO.setTenantId(menuVO.getTenantId());
        roleMenuMapper.deleteRoleMenusByMenu(roleMenuVO);
        
        int result = menuMapper.deleteMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 삭제 성공: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        } else {
            logger.warn("메뉴 삭제 실패: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        }
        return result;
    }

    /**
     * 메뉴 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectMenuCount(MenuVO menuVO) {
        logger.debug("메뉴 수 조회: tenantId={}", menuVO.getTenantId());
        return menuMapper.selectMenuCount(menuVO);
    }

    /**
     * 메뉴 수 조회 (검색/페이징)
     */
    @Override
    @Transactional(readOnly = true)
    public int selectMenuListWithSearchCount(MenuVO menuVO) {
        logger.debug("메뉴 수 조회 (검색/페이징): tenantId={}, keyword={}", menuVO.getTenantId(), menuVO.getSearchKeyword());
        return menuMapper.selectMenuListWithSearchCount(menuVO);
    }

    /**
     * 역할별 메뉴 조회 (사용자 로그인 시 메뉴 구성용)
     */
    @Override
    @Transactional(readOnly = true)
    public List<MenuVO> selectMenusByRole(RoleMenuVO roleMenuVO) {
        logger.debug("역할별 메뉴 조회: roleCd={}, tenantId={}", roleMenuVO.getRoleCd(), roleMenuVO.getTenantId());
        return roleMenuMapper.selectMenusByRole(roleMenuVO);
    }

    /**
     * URL별 접근 가능한 역할 조회 (접근 제어용)
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> selectRolesByUrl(RoleMenuVO roleMenuVO) {
        logger.debug("URL별 역할 조회: menuUrl={}, tenantId={}", roleMenuVO.getMenuUrl(), roleMenuVO.getTenantId());
        return roleMenuMapper.selectRolesByUrl(roleMenuVO);
    }

    /**
     * 메뉴에 매핑된 역할 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> selectRolesByMenu(RoleMenuVO roleMenuVO) {
        logger.debug("메뉴별 역할 조회: menuId={}, tenantId={}", roleMenuVO.getMenuId(), roleMenuVO.getTenantId());
        return roleMenuMapper.selectRolesByMenu(roleMenuVO);
    }

    /**
     * 역할-메뉴 매핑 등록 (단일)
     */
    @Override
    public int insertRoleMenu(RoleMenuVO roleMenuVO) {
        logger.info("역할-메뉴 매핑 등록: roleCd={}, menuId={}, tenantId={}", 
                roleMenuVO.getRoleCd(), roleMenuVO.getMenuId(), roleMenuVO.getTenantId());
        
        // 중복 확인
        if (roleMenuMapper.countRoleMenu(roleMenuVO) > 0) {
            logger.warn("역할-메뉴 매핑이 이미 존재: roleCd={}, menuId={}", 
                    roleMenuVO.getRoleCd(), roleMenuVO.getMenuId());
            return 0;
        }
        
        int result = roleMenuMapper.insertRoleMenu(roleMenuVO);
        if (result > 0) {
            logger.info("역할-메뉴 매핑 등록 성공");
        } else {
            logger.warn("역할-메뉴 매핑 등록 실패");
        }
        return result;
    }

    /**
     * 역할-메뉴 매핑 등록 (일괄)
     */
    @Override
    public int insertRoleMenuBatch(List<RoleMenuVO> roleMenuList) {
        logger.info("역할-메뉴 매핑 일괄 등록: 개수={}", roleMenuList.size());
        if (roleMenuList == null || roleMenuList.isEmpty()) {
            logger.warn("등록할 역할-메뉴 매핑이 없습니다.");
            return 0;
        }
        
        int result = roleMenuMapper.insertRoleMenuBatch(roleMenuList);
        if (result > 0) {
            logger.info("역할-메뉴 매핑 일괄 등록 성공: {}건", result);
        } else {
            logger.warn("역할-메뉴 매핑 일괄 등록 실패");
        }
        return result;
    }

    /**
     * 역할-메뉴 매핑 삭제 (특정 역할의 모든 메뉴 매핑 삭제)
     */
    @Override
    public int deleteRoleMenusByRole(RoleMenuVO roleMenuVO) {
        logger.info("역할별 역할-메뉴 매핑 삭제: roleCd={}, tenantId={}", 
                roleMenuVO.getRoleCd(), roleMenuVO.getTenantId());
        int result = roleMenuMapper.deleteRoleMenusByRole(roleMenuVO);
        if (result > 0) {
            logger.info("역할별 역할-메뉴 매핑 삭제 성공: {}건 삭제", result);
        } else {
            logger.debug("삭제할 역할-메뉴 매핑이 없습니다.");
        }
        return result;
    }

    /**
     * 역할-메뉴 매핑 삭제 (특정 메뉴의 모든 역할 매핑 삭제)
     */
    @Override
    public int deleteRoleMenusByMenu(RoleMenuVO roleMenuVO) {
        logger.info("메뉴별 역할-메뉴 매핑 삭제: menuId={}, tenantId={}", 
                roleMenuVO.getMenuId(), roleMenuVO.getTenantId());
        int result = roleMenuMapper.deleteRoleMenusByMenu(roleMenuVO);
        if (result > 0) {
            logger.info("메뉴별 역할-메뉴 매핑 삭제 성공: {}건 삭제", result);
        } else {
            logger.debug("삭제할 역할-메뉴 매핑이 없습니다.");
        }
        return result;
    }

    /**
     * 역할-메뉴 매핑 삭제 (단일)
     */
    @Override
    public int deleteRoleMenu(RoleMenuVO roleMenuVO) {
        logger.info("역할-메뉴 매핑 삭제: roleCd={}, menuId={}, tenantId={}", 
                roleMenuVO.getRoleCd(), roleMenuVO.getMenuId(), roleMenuVO.getTenantId());
        int result = roleMenuMapper.deleteRoleMenu(roleMenuVO);
        if (result > 0) {
            logger.info("역할-메뉴 매핑 삭제 성공");
        } else {
            logger.warn("역할-메뉴 매핑 삭제 실패");
        }
        return result;
    }

    /**
     * 역할-메뉴 매핑 존재 여부 확인
     */
    @Override
    @Transactional(readOnly = true)
    public int countRoleMenu(RoleMenuVO roleMenuVO) {
        logger.debug("역할-메뉴 매핑 존재 확인: roleCd={}, menuId={}", 
                roleMenuVO.getRoleCd(), roleMenuVO.getMenuId());
        return roleMenuMapper.countRoleMenu(roleMenuVO);
    }

    /**
     * 역할별 메뉴 수 조회 (특정 역할이 접근 가능한 메뉴 개수)
     */
    @Override
    @Transactional(readOnly = true)
    public int countMenusByRole(RoleMenuVO roleMenuVO) {
        logger.debug("역할별 메뉴 수 조회: roleCd={}, tenantId={}", 
                roleMenuVO.getRoleCd(), roleMenuVO.getTenantId());
        return roleMenuMapper.countMenusByRole(roleMenuVO);
    }
}
