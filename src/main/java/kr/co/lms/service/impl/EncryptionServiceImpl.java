package kr.co.lms.service.impl;

import kr.co.lms.crypto.AesGcmCryptoUtil;
import kr.co.lms.mapper.EncryptionKeyMapper;
import kr.co.lms.service.EncryptionService;
import kr.co.lms.vo.EncryptionKeyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;

/**
 * 암호화 서비스 구현체
 * 
 * 기능:
 * 1. 테넌트별 AES-256-GCM 암호화 키 관리
 * 2. 개인정보(email, phone, address) 암호화/복호화
 * 3. 암호화 키 로테이션 (수동)
 * 
 * 보안 고려사항:
 * - DB에 저장된 암호화 키는 마스터 키로 추가 암호화 필요 (향후 개선)
 * - 암호화 키 로테이션 시 이전 키도 보관 (복호화용)
 * - 모든 암호화/복호화는 로깅됨
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EncryptionServiceImpl implements EncryptionService {
    
    private final EncryptionKeyMapper encryptionKeyMapper;
    
    /**
     * 테넌트별 활성 암호화 키 조회
     * 
     * 캐싱 전략:
     * - 현재: DB 직접 조회 (매번)
     * - 향후: Redis 캐싱 (키는 자주 변경되지 않으므로)
     */
    @Override
    @Transactional(readOnly = true)
    public EncryptionKeyVO getActiveEncryptionKey(String tenantId) {
        log.debug("[ENCRYPTION] 활성 암호화 키 조회 - tenantId: {}", tenantId);
        
        EncryptionKeyVO key = encryptionKeyMapper.selectActiveKeyByTenant(tenantId);
        
        if (key == null) {
            log.warn("[ENCRYPTION] 테넌트의 활성 암호화 키 없음 - tenantId: {}", tenantId);
            throw new RuntimeException("테넌트 '" + tenantId + "'의 활성 암호화 키가 없습니다. 관리자에게 문의하세요.");
        }
        
        log.debug("[ENCRYPTION] 활성 암호화 키 조회 성공 - keyId: {}, algorithm: {}", 
                  key.getKeyId(), key.getAlgorithm());
        
        return key;
    }
    
    /**
     * 특정 버전의 암호화 키 조회 (복호화용)
     * 
     * 용도:
     * - 키 로테이션 후 이전 버전으로 암호화된 데이터 복호화
     * - 여러 버전의 암호화 데이터가 혼재할 때 사용
     */
    @Override
    @Transactional(readOnly = true)
    public EncryptionKeyVO getEncryptionKeyByVersion(String tenantId, String keyName) {
        log.debug("[ENCRYPTION] 특정 버전 암호화 키 조회 - tenantId: {}, keyName: {}", 
                  tenantId, keyName);
        
        EncryptionKeyVO key = encryptionKeyMapper.selectKeyByTenantAndName(tenantId, keyName);
        
        if (key == null) {
            log.warn("[ENCRYPTION] 지정된 버전의 암호화 키 없음 - tenantId: {}, keyName: {}", 
                     tenantId, keyName);
            throw new RuntimeException("테넌트 '" + tenantId + "'의 암호화 키 버전 '" + keyName + 
                                       "'을(를) 찾을 수 없습니다.");
        }
        
        return key;
    }
    
    /**
     * 새로운 암호화 키 생성 및 저장
     * 
     * 프로세스:
     * 1. AES-256 키 생성 (256비트)
     * 2. Base64 인코딩
     * 3. DB에 저장
     * 
     * 주의: 마스터 키로 추가 암호화 필요 (현재 미구현)
     */
    @Override
    public EncryptionKeyVO createNewEncryptionKey(String tenantId, String keyName) {
        log.info("[ENCRYPTION] 새로운 암호화 키 생성 - tenantId: {}, keyName: {}", 
                 tenantId, keyName);
        
        try {
            // Step 1: AES-256 키 생성
            SecretKey newKey = AesGcmCryptoUtil.generateKey();
            log.debug("[ENCRYPTION] AES-256 키 생성 완료");
            
            // Step 2: Base64 인코딩
            String encryptedKey = AesGcmCryptoUtil.keyToString(newKey);
            
            // Step 3: VO 생성 및 DB 저장
            EncryptionKeyVO keyVO = new EncryptionKeyVO(tenantId, keyName, encryptedKey);
            keyVO.setRotationPolicy("manual"); // 수동 로테이션
            
            encryptionKeyMapper.insertEncryptionKey(keyVO);
            
            log.info("[ENCRYPTION] 새로운 암호화 키 생성 완료 - keyId: {}, keyName: {}", 
                     keyVO.getKeyId(), keyName);
            
            return keyVO;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 암호화 키 생성 실패 - tenantId: {}, keyName: {}, error: {}", 
                      tenantId, keyName, e.getMessage());
            throw new RuntimeException("암호화 키 생성에 실패했습니다.", e);
        }
    }
    
    /**
     * 이메일 암호화
     */
    @Override
    public String encryptEmail(String tenantId, String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String encrypted = AesGcmCryptoUtil.encrypt(email, secretKey);
            log.debug("[ENCRYPTION] 이메일 암호화 성공 - tenantId: {}", tenantId);
            
            return encrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 이메일 암호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("이메일 암호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 이메일 복호화
     */
    @Override
    public String decryptEmail(String tenantId, String encryptedEmail) {
        if (encryptedEmail == null || encryptedEmail.isEmpty()) {
            return encryptedEmail;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String decrypted = AesGcmCryptoUtil.decrypt(encryptedEmail, secretKey);
            log.debug("[ENCRYPTION] 이메일 복호화 성공 - tenantId: {}", tenantId);
            
            return decrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 이메일 복호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("이메일 복호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 전화번호 암호화
     */
    @Override
    public String encryptPhone(String tenantId, String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String encrypted = AesGcmCryptoUtil.encrypt(phone, secretKey);
            log.debug("[ENCRYPTION] 전화번호 암호화 성공 - tenantId: {}", tenantId);
            
            return encrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 전화번호 암호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("전화번호 암호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 전화번호 복호화
     */
    @Override
    public String decryptPhone(String tenantId, String encryptedPhone) {
        if (encryptedPhone == null || encryptedPhone.isEmpty()) {
            return encryptedPhone;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String decrypted = AesGcmCryptoUtil.decrypt(encryptedPhone, secretKey);
            log.debug("[ENCRYPTION] 전화번호 복호화 성공 - tenantId: {}", tenantId);
            
            return decrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 전화번호 복호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("전화번호 복호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 주소 암호화
     */
    @Override
    public String encryptAddress(String tenantId, String address) {
        if (address == null || address.isEmpty()) {
            return address;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String encrypted = AesGcmCryptoUtil.encrypt(address, secretKey);
            log.debug("[ENCRYPTION] 주소 암호화 성공 - tenantId: {}", tenantId);
            
            return encrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 주소 암호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("주소 암호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 주소 복호화
     */
    @Override
    public String decryptAddress(String tenantId, String encryptedAddress) {
        if (encryptedAddress == null || encryptedAddress.isEmpty()) {
            return encryptedAddress;
        }
        
        try {
            EncryptionKeyVO keyVO = getActiveEncryptionKey(tenantId);
            SecretKey secretKey = AesGcmCryptoUtil.stringToKey(keyVO.getEncryptedKey());
            
            String decrypted = AesGcmCryptoUtil.decrypt(encryptedAddress, secretKey);
            log.debug("[ENCRYPTION] 주소 복호화 성공 - tenantId: {}", tenantId);
            
            return decrypted;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 주소 복호화 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("주소 복호화에 실패했습니다.", e);
        }
    }
    
    /**
     * 암호화 키 로테이션 (수동)
     * 
     * 프로세스:
     * 1. 새로운 키 생성 및 저장
     * 2. 기존 활성 키 비활성화
     * 3. 새 키 활성화
     * 4. 로테이션 일시 기록
     * 
     * 주의: 이전 키도 보관하여 기존 암호화 데이터 복호화 가능
     */
    @Override
    public EncryptionKeyVO rotateEncryptionKey(String tenantId, String newKeyName) {
        log.warn("[ENCRYPTION] 암호화 키 로테이션 시작 - tenantId: {}, newKeyName: {}", 
                 tenantId, newKeyName);
        
        try {
            // Step 1: 기존 활성 키 조회
            EncryptionKeyVO oldKey = getActiveEncryptionKey(tenantId);
            
            // Step 2: 새로운 키 생성
            EncryptionKeyVO newKey = createNewEncryptionKey(tenantId, newKeyName);
            
            // Step 3: 기존 키 비활성화
            encryptionKeyMapper.updateKeyActiveStatus(oldKey.getKeyId(), "N");
            log.info("[ENCRYPTION] 기존 암호화 키 비활성화 - keyId: {}", oldKey.getKeyId());
            
            // Step 4: 새 키 로테이션 정보 기록
            newKey.setRotatedDt(LocalDateTime.now());
            newKey.setRotationPolicy("manual");
            encryptionKeyMapper.updateKeyRotationInfo(newKey);
            
            log.warn("[ENCRYPTION] 암호화 키 로테이션 완료 - 새 keyId: {}, 기존 keyId: {}", 
                     newKey.getKeyId(), oldKey.getKeyId());
            
            return newKey;
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 암호화 키 로테이션 실패 - tenantId: {}, error: {}", 
                      tenantId, e.getMessage());
            throw new RuntimeException("암호화 키 로테이션에 실패했습니다.", e);
        }
    }
}
