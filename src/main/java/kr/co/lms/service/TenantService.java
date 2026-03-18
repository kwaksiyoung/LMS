package kr.co.lms.service;

import kr.co.lms.vo.TenantVO;
import java.util.List;

/**
 * 테넌트 Service 인터페이스
 */
public interface TenantService {

    /**
     * 활성 테넌트 목록 조회 (회원가입에 사용)
     * 
     * @return 활성 테넌트 목록
     */
    List<TenantVO> selectActiveTenants();

    /**
     * 전체 테넌트 목록 조회
     * 
     * @return 전체 테넌트 목록
     */
    List<TenantVO> selectAllTenants();

    /**
     * 테넌트 ID로 조회
     * 
     * @param tenantId 테넌트 ID
     * @return 테넌트 정보
     */
    TenantVO selectTenantById(String tenantId);

    /**
     * 테넌트 등록
     * 
     * @param tenantVO 테넌트 정보
     * @return 등록된 행 수
     */
    int insertTenant(TenantVO tenantVO);

    /**
     * 테넌트 수정
     * 
     * @param tenantVO 테넌트 정보
     * @return 수정된 행 수
     */
    int updateTenant(TenantVO tenantVO);

    /**
     * 테넌트 삭제
     * 
     * @param tenantId 테넌트 ID
     * @return 삭제된 행 수
     */
    int deleteTenant(String tenantId);
}
