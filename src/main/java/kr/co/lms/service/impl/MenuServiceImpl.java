package kr.co.lms.service.impl;

import kr.co.lms.mapper.MenuMapper;
import kr.co.lms.mapper.RoleMenuMapper;
import kr.co.lms.service.MenuService;
import kr.co.lms.vo.MenuVO;
import kr.co.lms.vo.RoleMenuVO;
import kr.co.lms.web.exception.MenuNotFoundException;
import kr.co.lms.web.exception.MenuHasChildrenException;
import kr.co.lms.web.exception.MenuDeletionException;
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
     * 
     * 트랜잭션 안전화:
     * 1. 역할-메뉴 매핑 삭제 (Cascading delete)
     * 2. 메뉴 삭제
     * 3. 모두 실패하거나 모두 성공 (원자성 보장)
     */
    @Override
    @Transactional  // 🔴 CRITICAL: 트랜잭션 보장으로 일관성 유지
    public int deleteMenu(MenuVO menuVO) {
        logger.info("메뉴 삭제: menuId={}, tenantId={}", menuVO.getMenuId(), menuVO.getTenantId());
        
        try {
            // 1단계: 메뉴 존재 확인
            MenuVO existingMenu = menuMapper.selectMenu(menuVO);
            if (existingMenu == null) {
                logger.warn("메뉴를 찾을 수 없습니다: menuId={}, tenantId={}", 
                           menuVO.getMenuId(), menuVO.getTenantId());
                throw new MenuNotFoundException("메뉴를 찾을 수 없습니다.");
            }
            
            // 2단계: 자식 메뉴 확인 (선택사항: 자식이 있으면 삭제 불가)
            // 만약 자식 메뉴까지 함께 삭제하고 싶으면 이 부분을 주석 처리하세요.
            MenuVO childSearchVO = new MenuVO();
            childSearchVO.setParentMenuId(menuVO.getMenuId());
            childSearchVO.setTenantId(menuVO.getTenantId());
            int childCount = menuMapper.selectMenuCount(childSearchVO);
            
            if (childCount > 0) {
                logger.warn("자식 메뉴가 있어서 삭제 불가: menuId={}, childCount={}", 
                           menuVO.getMenuId(), childCount);
                throw new MenuHasChildrenException("자식 메뉴가 있어서 삭제할 수 없습니다.");
            }
            
            // 3단계: 역할 매핑 삭제 (Cascading delete)
            RoleMenuVO roleMenuVO = new RoleMenuVO();
            roleMenuVO.setMenuId(menuVO.getMenuId());
            roleMenuVO.setTenantId(menuVO.getTenantId());
            int deletedRoleCount = roleMenuMapper.deleteRoleMenusByMenu(roleMenuVO);
            logger.info("역할 매핑 삭제 완료: menuId={}, deletedCount={}", 
                       menuVO.getMenuId(), deletedRoleCount);
            
            // 4단계: 메뉴 삭제
            int result = menuMapper.deleteMenu(menuVO);
            if (result > 0) {
                logger.info("메뉴 삭제 성공: menuId={}, tenantId={}, 역할삭제건={}", 
                           menuVO.getMenuId(), menuVO.getTenantId(), deletedRoleCount);
            } else {
                logger.warn("메뉴 삭제 실패: menuId={}, tenantId={}", 
                           menuVO.getMenuId(), menuVO.getTenantId());
                throw new MenuDeletionException("메뉴 삭제에 실패했습니다.");
            }
            
            return result;
            
        } catch (MenuNotFoundException | MenuHasChildrenException e) {
            // 비즈니스 예외는 그대로 throw
            throw e;
        } catch (org.springframework.dao.DataAccessException e) {
            logger.error("메뉴 삭제 중 DB 오류: menuId={}", menuVO.getMenuId(), e);
            throw new MenuDeletionException("메뉴 삭제 중 데이터베이스 오류가 발생했습니다.", e);
        } catch (Exception e) {
            logger.error("메뉴 삭제 중 예상치 못한 오류: menuId={}", menuVO.getMenuId(), e);
            throw new MenuDeletionException("메뉴 삭제에 실패했습니다.", e);
        }
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

    /**
     * 메뉴 일괄 등록 (벌크 인서트)
     */
    @Override
    public int insertMenuBatch(List<MenuVO> menuList) {
        logger.info("메뉴 일괄 등록 시작: 개수={}", menuList != null ? menuList.size() : 0);
        
        if (menuList == null || menuList.isEmpty()) {
            logger.warn("등록할 메뉴가 없습니다.");
            return 0;
        }
        
        // 메뉴 일괄 등록
        int result = menuMapper.insertMenuBatch(menuList);
        
        if (result > 0) {
            logger.info("메뉴 일괄 등록 성공: {}건", result);
            
            // 역할 매핑이 있는 경우 처리
            for (MenuVO menu : menuList) {
                if (menu.getSelectedRoles() != null && !menu.getSelectedRoles().isEmpty()) {
                    List<RoleMenuVO> roleMenuList = new java.util.ArrayList<>();
                    for (String roleCd : menu.getSelectedRoles()) {
                        RoleMenuVO roleMenuVO = new RoleMenuVO();
                        roleMenuVO.setRoleCd(roleCd);
                        roleMenuVO.setMenuId(menu.getMenuId());
                        roleMenuVO.setTenantId(menu.getTenantId());
                        roleMenuList.add(roleMenuVO);
                    }
                    
                    if (!roleMenuList.isEmpty()) {
                        int roleMenuResult = roleMenuMapper.insertRoleMenuBatch(roleMenuList);
                        logger.info("메뉴 {}에 대한 역할 매핑 등록 완료: {}건", 
                                menu.getMenuId(), roleMenuResult);
                    }
                }
            }
        } else {
            logger.warn("메뉴 일괄 등록 실패");
        }
        
        return result;
    }

    /**
     * 메뉴 순서 변경
     */
    @Override
    public int updateMenuSortOrder(String menuId, int newSortOrder, String tenantId) {
        logger.info("메뉴 순서 변경: menuId={}, newSortOrder={}, tenantId={}", 
                menuId, newSortOrder, tenantId);
        
        MenuVO menuVO = new MenuVO();
        menuVO.setMenuId(menuId);
        menuVO.setSortOrder(newSortOrder);
        menuVO.setTenantId(tenantId);
        
        int result = menuMapper.updateMenuSortOrder(menuVO);
        
        if (result > 0) {
            logger.info("메뉴 순서 변경 성공: menuId={}, newSortOrder={}", menuId, newSortOrder);
        } else {
            logger.warn("메뉴 순서 변경 실패: menuId={}, tenantId={}", menuId, tenantId);
        }
        
        return result;
    }

    /**
     * 메뉴 사용여부 일괄 변경
     */
    @Override
    public int updateMenuUseYnBatch(List<String> menuIds, String useYn, String tenantId) {
        logger.info("메뉴 사용여부 일괄 변경: 개수={}, useYn={}, tenantId={}", 
                menuIds != null ? menuIds.size() : 0, useYn, tenantId);
        
        if (menuIds == null || menuIds.isEmpty()) {
            logger.warn("변경할 메뉴가 없습니다.");
            return 0;
        }
        
        int result = menuMapper.updateMenuUseYnBatch(menuIds, useYn, tenantId);
        
        if (result > 0) {
            logger.info("메뉴 사용여부 일괄 변경 성공: {}건", result);
        } else {
            logger.warn("메뉴 사용여부 일괄 변경 실패");
        }
        
        return result;
    }
}
