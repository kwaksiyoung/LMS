package kr.co.lms.service.impl;

import kr.co.lms.mapper.MenuMapper;
import kr.co.lms.service.MenuService;
import kr.co.lms.vo.MenuVO;
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

    /**
     * 메뉴 조회 (ID로)
     */
    @Override
    @Transactional(readOnly = true)
    public MenuVO selectMenu(String menuId) {
        logger.debug("메뉴 조회: menuId={}", menuId);
        return menuMapper.selectMenu(menuId);
    }

    /**
     * 메뉴 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<MenuVO> selectMenuList(MenuVO menuVO) {
        logger.debug("메뉴 목록 조회: {}", menuVO);
        return menuMapper.selectMenuList(menuVO);
    }

    /**
     * 메뉴 등록
     */
    @Override
    public int insertMenu(MenuVO menuVO) {
        logger.info("메뉴 등록: menuId={}", menuVO.getMenuId());
        int result = menuMapper.insertMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 등록 성공: menuId={}", menuVO.getMenuId());
        } else {
            logger.warn("메뉴 등록 실패: menuId={}", menuVO.getMenuId());
        }
        return result;
    }

    /**
     * 메뉴 수정
     */
    @Override
    public int updateMenu(MenuVO menuVO) {
        logger.info("메뉴 수정: menuId={}", menuVO.getMenuId());
        int result = menuMapper.updateMenu(menuVO);
        if (result > 0) {
            logger.info("메뉴 수정 성공: menuId={}", menuVO.getMenuId());
        } else {
            logger.warn("메뉴 수정 실패: menuId={}", menuVO.getMenuId());
        }
        return result;
    }

    /**
     * 메뉴 삭제
     */
    @Override
    public int deleteMenu(String menuId) {
        logger.info("메뉴 삭제: menuId={}", menuId);
        int result = menuMapper.deleteMenu(menuId);
        if (result > 0) {
            logger.info("메뉴 삭제 성공: menuId={}", menuId);
        } else {
            logger.warn("메뉴 삭제 실패: menuId={}", menuId);
        }
        return result;
    }

    /**
     * 메뉴 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectMenuCount(MenuVO menuVO) {
        logger.debug("메뉴 수 조회");
        return menuMapper.selectMenuCount(menuVO);
    }
}
