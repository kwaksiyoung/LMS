package kr.co.lms.config;

import kr.co.lms.mapper.EncryptionKeyMapper;
import kr.co.lms.service.EncryptionService;
import kr.co.lms.vo.EncryptionKeyVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 암호화 키 초기화 (eGovFrame)
 * 
 * 기능:
 * 1. Spring 빈 생성 후 자동으로 실행 (@PostConstruct)
 * 2. 각 테넌트별 활성 암호화 키 존재 여부 확인
 * 3. 키가 없으면 자동으로 생성
 * 4. 초기화 과정 로깅
 * 
 * 실행 시점:
 * - Spring Context 초기화 후
 * - EncryptionInitializer 빈 생성 완료 후
 * - EncryptionService 의존성 주입 완료 후
 * 
 * eGovFrame 호환:
 * - ApplicationRunner 대신 @PostConstruct 사용 (Spring Framework 표준)
 * - WAR 패키징 지원
 * - 전통 Spring MVC 호환
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionInitializer {
    
    private final EncryptionService encryptionService;
    private final EncryptionKeyMapper encryptionKeyMapper;
    
    /**
     * Spring 빈 생성 후 자동으로 실행
     * @PostConstruct를 통해 Spring 생명주기에 연결됨
     * 
     * 실행 순서:
     * 1. EncryptionInitializer 빈 생성
     * 2. 의존성 주입 (encryptionService, encryptionKeyMapper)
     * 3. @PostConstruct 메서드 실행 (이 메서드)
     */
    @PostConstruct
    public void initializeEncryptionKeys() {
        log.info("[ENCRYPTION] 암호화 키 초기화 시작...");
        
        try {
            // 초기화할 테넌트 리스트
            List<String> tenantIds = Arrays.asList(
                "TENANT001",
                "TENANT002"
            );
            
            // 각 테넌트별로 활성 키가 있는지 확인 및 생성
            for (String tenantId : tenantIds) {
                initializeEncryptionKeyForTenant(tenantId);
            }
            
            log.info("[ENCRYPTION] 암호화 키 초기화 완료");
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 암호화 키 초기화 실패: {}", e.getMessage());
            // 초기화 실패해도 애플리케이션 시작은 계속됨 (경고만 기록)
            // 필요시 throw new RuntimeException("암호화 키 초기화 실패", e);
        }
    }
    
    /**
     * 특정 테넌트의 암호화 키 초기화
     * 
     * @param tenantId 고객사 ID
     */
    private void initializeEncryptionKeyForTenant(String tenantId) {
        try {
            log.debug("[ENCRYPTION] 테넌트 암호화 키 초기화 - tenantId: {}", tenantId);
            
            // Step 1: 활성 암호화 키 존재 여부 확인
            EncryptionKeyVO existingKey = encryptionKeyMapper.selectActiveKeyByTenant(tenantId);
            
            if (existingKey != null) {
                // 활성 키가 이미 존재
                log.info("[ENCRYPTION] 테넌트 '{}' 활성 키 이미 존재 - keyId: {}, keyName: {}", 
                         tenantId, existingKey.getKeyId(), existingKey.getKeyName());
                return;
            }
            
            // Step 2: 활성 키가 없으면 새로 생성
            log.info("[ENCRYPTION] 테넌트 '{}' 활성 키 없음 - 새로 생성합니다", tenantId);
            
            String keyName = "AES-256-GCM-v1";  // 초기 키 버전
            EncryptionKeyVO newKey = encryptionService.createNewEncryptionKey(tenantId, keyName);
            
            if (newKey != null) {
                log.info("[ENCRYPTION] 테넌트 '{}' 새로운 암호화 키 생성 완료 - keyId: {}, keyName: {}", 
                         tenantId, newKey.getKeyId(), keyName);
            } else {
                log.warn("[ENCRYPTION] 테넌트 '{}' 암호화 키 생성 실패 - 반환값이 null입니다", tenantId);
            }
            
        } catch (Exception e) {
            log.error("[ENCRYPTION] 테넌트 '{}' 암호화 키 초기화 실패 - error: {}", tenantId, e.getMessage());
            // 특정 테넌트 키 생성 실패해도 다른 테넌트는 계속 진행
        }
    }
}
