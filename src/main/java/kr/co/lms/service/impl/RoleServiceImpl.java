package kr.co.lms.service.impl;

import kr.co.lms.mapper.RoleMapper;
import kr.co.lms.service.RoleService;
import kr.co.lms.vo.RoleVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 역할 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleMapper roleMapper;

    /**
     * 역할 조회 (코드로)
     */
    @Override
    @Transactional(readOnly = true)
    public RoleVO selectRole(String roleCd) {
        logger.debug("역할 조회: roleCd={}", roleCd);
        return roleMapper.selectRole(roleCd);
    }

    /**
     * 역할 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoleVO> selectRoleList(RoleVO roleVO) {
        logger.debug("역할 목록 조회: {}", roleVO);
        return roleMapper.selectRoleList(roleVO);
    }

    /**
     * 역할 등록
     */
    @Override
    public int insertRole(RoleVO roleVO) {
        logger.info("역할 등록: roleCd={}", roleVO.getRoleCd());
        int result = roleMapper.insertRole(roleVO);
        if (result > 0) {
            logger.info("역할 등록 성공: roleCd={}", roleVO.getRoleCd());
        } else {
            logger.warn("역할 등록 실패: roleCd={}", roleVO.getRoleCd());
        }
        return result;
    }

    /**
     * 역할 수정
     */
    @Override
    public int updateRole(RoleVO roleVO) {
        logger.info("역할 수정: roleCd={}", roleVO.getRoleCd());
        int result = roleMapper.updateRole(roleVO);
        if (result > 0) {
            logger.info("역할 수정 성공: roleCd={}", roleVO.getRoleCd());
        } else {
            logger.warn("역할 수정 실패: roleCd={}", roleVO.getRoleCd());
        }
        return result;
    }

    /**
     * 역할 삭제
     */
    @Override
    public int deleteRole(String roleCd) {
        logger.info("역할 삭제: roleCd={}", roleCd);
        int result = roleMapper.deleteRole(roleCd);
        if (result > 0) {
            logger.info("역할 삭제 성공: roleCd={}", roleCd);
        } else {
            logger.warn("역할 삭제 실패: roleCd={}", roleCd);
        }
        return result;
    }

    /**
     * 역할 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectRoleCount(RoleVO roleVO) {
        logger.debug("역할 수 조회");
        return roleMapper.selectRoleCount(roleVO);
    }
}
