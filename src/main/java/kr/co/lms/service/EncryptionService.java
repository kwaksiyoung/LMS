package kr.co.lms.service;

import kr.co.lms.vo.EncryptionKeyVO;
import javax.crypto.SecretKey;

/**
 * 암호화 서비스 인터페이스
 * 
 * 개인정보(email, phone, address) 암호화/복호화 관리
 * 테넌트별 AES-256-GCM 키를 사용하여 암호화 수행
 */
public interface EncryptionService {
    
    /**
     * 테넌트별 암호화 키 조회
     * 
     * @param tenantId 고객사 ID
     * @return 활성 암호화 키 정보
     */
    EncryptionKeyVO getActiveEncryptionKey(String tenantId);
    
    /**
     * 특정 버전의 암호화 키 조회 (복호화용)
     * 
     * @param tenantId 고객사 ID
     * @param keyName 키 이름 (버전)
     * @return 암호화 키 정보
     */
    EncryptionKeyVO getEncryptionKeyByVersion(String tenantId, String keyName);
    
    /**
     * 새로운 암호화 키 생성 및 저장
     * 
     * @param tenantId 고객사 ID
     * @param keyName 키 이름 (버전)
     * @return 생성된 키 정보
     */
    EncryptionKeyVO createNewEncryptionKey(String tenantId, String keyName);
    
    /**
     * 이메일 암호화
     * 
     * @param tenantId 고객사 ID
     * @param email 평문 이메일
     * @return 암호화된 이메일 (Base64)
     */
    String encryptEmail(String tenantId, String email);
    
    /**
     * 이메일 복호화
     * 
     * @param tenantId 고객사 ID
     * @param encryptedEmail 암호화된 이메일 (Base64)
     * @return 복호화된 평문 이메일
     */
    String decryptEmail(String tenantId, String encryptedEmail);
    
    /**
     * 전화번호 암호화
     * 
     * @param tenantId 고객사 ID
     * @param phone 평문 전화번호
     * @return 암호화된 전화번호 (Base64)
     */
    String encryptPhone(String tenantId, String phone);
    
    /**
     * 전화번호 복호화
     * 
     * @param tenantId 고객사 ID
     * @param encryptedPhone 암호화된 전화번호 (Base64)
     * @return 복호화된 평문 전화번호
     */
    String decryptPhone(String tenantId, String encryptedPhone);
    
    /**
     * 주소 암호화
     * 
     * @param tenantId 고객사 ID
     * @param address 평문 주소
     * @return 암호화된 주소 (Base64)
     */
    String encryptAddress(String tenantId, String address);
    
    /**
     * 주소 복호화
     * 
     * @param tenantId 고객사 ID
     * @param encryptedAddress 암호화된 주소 (Base64)
     * @return 복호화된 평문 주소
     */
    String decryptAddress(String tenantId, String encryptedAddress);
    
    /**
     * 테넌트별 키 로테이션 수행
     * - 새 키 생성
     * - 기존 키 비활성화
     * - 새 키 활성화
     * 
     * @param tenantId 고객사 ID
     * @param newKeyName 새로운 키 이름
     * @return 생성된 새로운 키 정보
     */
    EncryptionKeyVO rotateEncryptionKey(String tenantId, String newKeyName);
}
