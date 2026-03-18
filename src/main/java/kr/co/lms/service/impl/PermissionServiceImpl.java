package kr.co.lms.service.impl;

import kr.co.lms.mapper.PermissionMapper;
import kr.co.lms.service.PermissionService;
import kr.co.lms.vo.PermissionVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 권한 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionServiceImpl.class);

    private final PermissionMapper permissionMapper;

    /**
     * 권한 조회 (코드로)
     */
    @Override
    @Transactional(readOnly = true)
    public PermissionVO selectPermission(String permCd) {
        logger.debug("권한 조회: permCd={}", permCd);
        return permissionMapper.selectPermission(permCd);
    }

    /**
     * 권한 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<PermissionVO> selectPermissionList(PermissionVO permissionVO) {
        logger.debug("권한 목록 조회: {}", permissionVO);
        return permissionMapper.selectPermissionList(permissionVO);
    }

    /**
     * 권한 등록
     */
    @Override
    public int insertPermission(PermissionVO permissionVO) {
        logger.info("권한 등록: permCd={}", permissionVO.getPermCd());
        int result = permissionMapper.insertPermission(permissionVO);
        if (result > 0) {
            logger.info("권한 등록 성공: permCd={}", permissionVO.getPermCd());
        } else {
            logger.warn("권한 등록 실패: permCd={}", permissionVO.getPermCd());
        }
        return result;
    }

    /**
     * 권한 수정
     */
    @Override
    public int updatePermission(PermissionVO permissionVO) {
        logger.info("권한 수정: permCd={}", permissionVO.getPermCd());
        int result = permissionMapper.updatePermission(permissionVO);
        if (result > 0) {
            logger.info("권한 수정 성공: permCd={}", permissionVO.getPermCd());
        } else {
            logger.warn("권한 수정 실패: permCd={}", permissionVO.getPermCd());
        }
        return result;
    }

    /**
     * 권한 삭제
     */
    @Override
    public int deletePermission(String permCd) {
        logger.info("권한 삭제: permCd={}", permCd);
        int result = permissionMapper.deletePermission(permCd);
        if (result > 0) {
            logger.info("권한 삭제 성공: permCd={}", permCd);
        } else {
            logger.warn("권한 삭제 실패: permCd={}", permCd);
        }
        return result;
    }

    /**
     * 권한 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectPermissionCount(PermissionVO permissionVO) {
        logger.debug("권한 수 조회");
        return permissionMapper.selectPermissionCount(permissionVO);
    }
}
