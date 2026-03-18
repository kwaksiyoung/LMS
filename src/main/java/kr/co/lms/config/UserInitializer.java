package kr.co.lms.config;

import kr.co.lms.service.UserService;
import kr.co.lms.vo.UserVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

/**
 * 초기 사용자 데이터 설정 (eGovFrame)
 * 
 * 기능:
 * 1. Spring 빈 생성 후 자동으로 실행 (@PostConstruct)
 * 2. 각 테넌트별 초기 사용자 생성
 * 3. UserService를 통해 암호화하여 저장 (자동 암호화)
 * 4. 이미 존재하는 사용자는 스킵 (멱등성)
 * 
 * 실행 시점:
 * - EncryptionInitializer 이후 (암호화 키 준비 완료 후)
 * - UserService 빈 생성 완료 후
 * 
 * 암호화 흐름:
 * 평문 email/phone/address
 *   → UserInitializer에서 UserVO 생성
 *   → UserService.insertUser() 호출
 *   → UserServiceImpl.insertUser() 에서 자동 암호화
 *   → EncryptionService.encryptEmail/Phone/Address()
 *   → AES-256-GCM 암호화
 *   → 암호화된 값 DB 저장 ✅
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserInitializer {
    
    private final UserService userService;
    
    /**
     * Spring 빈 생성 후 자동으로 실행
     * @PostConstruct를 통해 Spring 생명주기에 연결됨
     */
    @PostConstruct
    public void initializeUsers() {
        log.info("[USER_INIT] 초기 사용자 데이터 설정 시작...");
        
        try {
            // TENANT001 초기 사용자들
            initializeTenant001Users();
            
            // TENANT002 초기 사용자들
            initializeTenant002Users();
            
            log.info("[USER_INIT] 초기 사용자 데이터 설정 완료");
            
        } catch (Exception e) {
            log.error("[USER_INIT] 초기 사용자 데이터 설정 실패: {}", e.getMessage(), e);
            // 초기화 실패해도 애플리케이션 시작은 계속됨
        }
    }
    
    /**
     * TENANT001 초기 사용자 생성
     */
    private void initializeTenant001Users() {
        log.debug("[USER_INIT] TENANT001 초기 사용자 설정 시작");
        
        // 관리자
        createUserIfNotExists(
            "admin001",
            "TENANT001",
            "관리자",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",  // password
            "admin001@abc.com",       // email (평문 → 자동 암호화됨)
            "010-0000-0001",          // phone (평문 → 자동 암호화됨)
            "서울특별시 강남구 테헤란로 123",  // address (평문 → 자동 암호화됨)
            "MGT",
            "관리부"
        );
        
        // 담당자 (김수영)
        createUserIfNotExists(
            "user001",
            "TENANT001",
            "김수영",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "user001@abc.com",
            "010-1111-0001",
            "서울특별시 강남구 강남대로 102",
            "HRM",
            "인사부"
        );
        
        // 학생 (이영희)
        createUserIfNotExists(
            "user002",
            "TENANT001",
            "이영희",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "user002@abc.com",
            "010-1111-0002",
            "서울특별시 서초구 서초대로 38",
            "IT",
            "정보시스템부"
        );
        
        // 강사 (박교수)
        createUserIfNotExists(
            "instructor001",
            "TENANT001",
            "박교수",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "instructor001@abc.com",
            "010-2222-0001",
            "서울특별시 마포구 월드컵북로 369",
            "EDU",
            "교육부"
        );
        
        log.debug("[USER_INIT] TENANT001 초기 사용자 설정 완료");
    }
    
    /**
     * TENANT002 초기 사용자 생성
     */
    private void initializeTenant002Users() {
        log.debug("[USER_INIT] TENANT002 초기 사용자 설정 시작");
        
        // 시스템관리자
        createUserIfNotExists(
            "admin002",
            "TENANT002",
            "시스템관리자",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "admin002@xyz.com",
            "010-0000-0002",
            "부산광역시 해운대구 센텀중앙로 54",
            "MGT",
            "운영팀"
        );
        
        // 교육팀원 (최동욱)
        createUserIfNotExists(
            "user003",
            "TENANT002",
            "최동욱",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "user003@xyz.com",
            "010-3333-0001",
            "부산광역시 남구 수영로 321",
            "EDU",
            "교육팀"
        );
        
        // 수강자 (정은아)
        createUserIfNotExists(
            "user004",
            "TENANT002",
            "정은아",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "user004@xyz.com",
            "010-3333-0002",
            "부산광역시 금정구 부산대로 63번길 2",
            "EDU",
            "교육팀"
        );
        
        // 강사 (최강강사)
        createUserIfNotExists(
            "instructor002",
            "TENANT002",
            "최강강사",
            "$2a$10$slYQmyNdGzin7olVN3/p2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMm2",
            "instructor002@xyz.com",
            "010-4444-0001",
            "대구광역시 중구 달구벌대로 2",
            "EDU",
            "강사팀"
        );
        
        log.debug("[USER_INIT] TENANT002 초기 사용자 설정 완료");
    }
    
    /**
     * 사용자 생성 (존재하지 않으면)
     * 
     * 멱등성 보장:
     * - 이미 존재하는 사용자는 스킵 (중복 생성 방지)
     * - 여러 번 실행해도 안전함
     * 
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @param userName 사용자명
     * @param password 비밀번호 (BCrypt 암호화됨)
     * @param email 이메일 (평문 → UserService에서 AES-256-GCM 암호화됨)
     * @param phone 전화번호 (평문 → UserService에서 AES-256-GCM 암호화됨)
     * @param address 주소 (평문 → UserService에서 AES-256-GCM 암호화됨)
     * @param deptCd 부서 코드
     * @param deptNm 부서명
     */
    private void createUserIfNotExists(String userId, String tenantId, String userName, 
                                       String password, String email, String phone, 
                                       String address, String deptCd, String deptNm) {
        try {
            // 사용자 존재 여부 확인
            UserVO existingUser = userService.selectUser(userId);
            
            if (existingUser != null) {
                // 이미 존재함 (복호화되어 반환됨)
                log.debug("[USER_INIT] 사용자 이미 존재 - userId: {}, tenantId: {}", userId, tenantId);
                return;
            }
            
            // 새 사용자 생성
            UserVO newUser = new UserVO();
            newUser.setUserId(userId);
            newUser.setTenantId(tenantId);
            newUser.setUserName(userName);
            newUser.setPassword(password);
            newUser.setEmail(email);           // 평문 입력
            newUser.setPhone(phone);           // 평문 입력
            newUser.setAddress(address);       // 평문 입력
            newUser.setDeptCd(deptCd);
            newUser.setUseYn("Y");
            newUser.setRegDt(LocalDateTime.now());
            newUser.setUpdDt(LocalDateTime.now());
            
            // UserService를 통해 저장
            // → UserServiceImpl.insertUser()에서 자동으로 암호화됨!
            int result = userService.insertUser(newUser);
            
            if (result > 0) {
                log.info("[USER_INIT] 사용자 생성 성공 - userId: {}, tenantId: {}, email: {} (암호화됨)", 
                         userId, tenantId, email);
            } else {
                log.warn("[USER_INIT] 사용자 생성 실패 - userId: {}, tenantId: {}", userId, tenantId);
            }
            
        } catch (Exception e) {
            log.error("[USER_INIT] 사용자 생성 중 오류 - userId: {}, tenantId: {}, error: {}", 
                      userId, tenantId, e.getMessage());
            // 개별 사용자 생성 실패해도 다른 사용자는 계속 진행
        }
    }
}
