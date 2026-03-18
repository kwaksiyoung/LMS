package kr.co.lms.mapper;

import kr.co.lms.vo.EncryptionKeyVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 암호화 키 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 * 테넌트별 AES-256 암호화 키 CRUD 작업
 */
@Mapper
public interface EncryptionKeyMapper {
    
    /**
     * 활성 암호화 키 조회 (테넌트별)
     * - 현재 활성중인 암호화 키를 반환
     * - 암호화/복호화 시에 항상 활성 키 사용
     * 
     * @param tenantId 고객사 ID
     * @return 활성 암호화 키
     */
    EncryptionKeyVO selectActiveKeyByTenant(@Param("tenantId") String tenantId);
    
    /**
     * 특정 버전의 암호화 키 조회
     * - 키 로테이션 후 이전 버전의 암호문을 복호화할 때 사용
     * 
     * @param tenantId 고객사 ID
     * @param keyName 키 이름 (버전)
     * @return 암호화 키
     */
    EncryptionKeyVO selectKeyByTenantAndName(@Param("tenantId") String tenantId, @Param("keyName") String keyName);
    
    /**
     * 암호화 키 등록
     * - 새로운 암호화 키를 생성할 때 사용
     * - 이전 활성 키는 자동으로 비활성화됨 (트리거 또는 서비스 로직)
     * 
     * @param keyVO 암호화 키 정보
     * @return 등록 건수
     */
    int insertEncryptionKey(EncryptionKeyVO keyVO);
    
    /**
     * 암호화 키 활성 상태 변경
     * - 키 로테이션 시: 새 키는 활성화(Y), 기존 키는 비활성화(N)
     * 
     * @param keyId 암호화 키 ID
     * @param isActive 활성 상태 (Y/N)
     * @return 업데이트 건수
     */
    int updateKeyActiveStatus(@Param("keyId") String keyId, @Param("isActive") String isActive);
    
    /**
     * 암호화 키 로테이션 정보 업데이트
     * - 마지막 로테이션 일시 기록
     * - 로테이션 정책 업데이트
     * 
     * @param keyVO 암호화 키 정보 (rotatedDt, rotationPolicy 포함)
     * @return 업데이트 건수
     */
    int updateKeyRotationInfo(EncryptionKeyVO keyVO);
    
    /**
     * 암호화 키 조회 (키 ID로)
     * - 특정 키 버전을 조회할 때 사용
     * 
     * @param keyId 암호화 키 ID
     * @return 암호화 키
     */
    EncryptionKeyVO selectKeyById(@Param("keyId") String keyId);
}
