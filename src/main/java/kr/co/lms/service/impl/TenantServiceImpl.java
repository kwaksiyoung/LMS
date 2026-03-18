package kr.co.lms.service.impl;

import kr.co.lms.mapper.TenantMapper;
import kr.co.lms.service.TenantService;
import kr.co.lms.vo.TenantVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 테넌트 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);

    private final TenantMapper tenantMapper;

    /**
     * 활성 테넌트 목록 조회
     * 
     * 회원가입 시 사용자가 선택할 수 있는 테넌트 목록
     * subscription_status = 'ACTIVE'인 것만 반환
     */
    @Override
    @Transactional(readOnly = true)
    public List<TenantVO> selectActiveTenants() {
        logger.debug("활성 테넌트 목록 조회");
        
        List<TenantVO> tenants = tenantMapper.selectActiveTenants();
        
        logger.info("활성 테넌트 조회 완료: {} 개", tenants.size());
        
        return tenants;
    }

    /**
     * 전체 테넌트 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<TenantVO> selectAllTenants() {
        logger.debug("전체 테넌트 목록 조회");
        
        List<TenantVO> tenants = tenantMapper.selectAllTenants();
        
        logger.info("전체 테넌트 조회 완료: {} 개", tenants.size());
        
        return tenants;
    }

    /**
     * 테넌트 ID로 조회
     */
    @Override
    @Transactional(readOnly = true)
    public TenantVO selectTenantById(String tenantId) {
        logger.debug("테넌트 조회: tenantId={}", tenantId);
        
        TenantVO tenant = tenantMapper.selectTenantById(tenantId);
        
        if (tenant != null) {
            logger.info("테넌트 조회 성공: tenantId={}, tenantNm={}", tenantId, tenant.getTenantNm());
        } else {
            logger.warn("테넌트를 찾을 수 없음: tenantId={}", tenantId);
        }
        
        return tenant;
    }

    /**
     * 테넌트 등록
     */
    @Override
    public int insertTenant(TenantVO tenantVO) {
        logger.info("테넌트 등록: tenantId={}, tenantNm={}", tenantVO.getTenantId(), tenantVO.getTenantNm());
        
        try {
            int result = tenantMapper.insertTenant(tenantVO);
            
            if (result > 0) {
                logger.info("테넌트 등록 성공: tenantId={}", tenantVO.getTenantId());
            } else {
                logger.warn("테넌트 등록 실패: tenantId={}", tenantVO.getTenantId());
            }
            
            return result;
        } catch (Exception e) {
            logger.error("테넌트 등록 중 오류: tenantId={}, error={}", tenantVO.getTenantId(), e.getMessage(), e);
            throw new RuntimeException("테넌트 등록에 실패했습니다.", e);
        }
    }

    /**
     * 테넌트 수정
     */
    @Override
    public int updateTenant(TenantVO tenantVO) {
        logger.info("테넌트 수정: tenantId={}", tenantVO.getTenantId());
        
        try {
            int result = tenantMapper.updateTenant(tenantVO);
            
            if (result > 0) {
                logger.info("테넌트 수정 성공: tenantId={}", tenantVO.getTenantId());
            } else {
                logger.warn("테넌트 수정 실패: tenantId={}", tenantVO.getTenantId());
            }
            
            return result;
        } catch (Exception e) {
            logger.error("테넌트 수정 중 오류: tenantId={}, error={}", tenantVO.getTenantId(), e.getMessage(), e);
            throw new RuntimeException("테넌트 수정에 실패했습니다.", e);
        }
    }

    /**
     * 테넌트 삭제
     */
    @Override
    public int deleteTenant(String tenantId) {
        logger.info("테넌트 삭제: tenantId={}", tenantId);
        
        try {
            int result = tenantMapper.deleteTenant(tenantId);
            
            if (result > 0) {
                logger.info("테넌트 삭제 성공: tenantId={}", tenantId);
            } else {
                logger.warn("테넌트 삭제 실패: tenantId={}", tenantId);
            }
            
            return result;
        } catch (Exception e) {
            logger.error("테넌트 삭제 중 오류: tenantId={}, error={}", tenantId, e.getMessage(), e);
            throw new RuntimeException("테넌트 삭제에 실패했습니다.", e);
        }
    }
}
