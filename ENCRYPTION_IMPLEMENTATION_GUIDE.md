# 회원 정보 암호화 구현 가이드

## 📋 개요

본 문서는 LMS 프로젝트에 구현된 AES-256-GCM 기반 회원 정보 암호화 시스템에 대한 종합 가이드입니다.

**구현 기간**: 2025-03-17  
**표준 준수**: KISA 양방향 암호화 표준 (개인정보 보호법 안전성 확보조치 기준 제7조)

---

## 🔐 암호화 체계

### 1. 암호화 알고리즘: AES-256-GCM

| 속성 | 값 | 설명 |
|------|-----|------|
| **알고리즘** | AES-256-GCM | 국제 표준 (NIST FIPS 197) |
| **키 길이** | 256비트 | KISA 권장 길이 |
| **GCM 태그** | 128비트 | 무결성 인증 (인증된 암호화) |
| **IV 길이** | 96비트 (12바이트) | GCM 최적 길이 |
| **패딩** | None (GCM에 내장) | 자동 처리 |

### 2. 암호화 대상 (개인정보)

```
✅ email    - 이메일 (양방향 암호화)
✅ phone    - 전화번호 (양방향 암호화)
✅ address  - 주소 (양방향 암호화)

❌ password - 비밀번호 (일방향 암호화 - BCrypt/Argon2 별도 처리)
❌ userId   - 사용자 ID (암호화 없음 - 식별자)
❌ userName - 사용자명 (암호화 없음)
```

### 3. 암호화 결과 형식

```
[IV(12바이트) + 암호문 + GCM 인증태그] → Base64 인코딩
```

**예시**:
```
평문:   test@example.com
암호화: GKqP2+8Xm9vZ1a3bC4dE5fG6hI7jK8lM9nO0pQ1rS2tU3vW4xY5zA6bC7dE8fG
                (Base64 인코딩된 암호문)
```

---

## 🏗️ 아키텍처

### 시스템 구성도

```
┌─────────────────────────────────────────────────────┐
│                   UserService API                   │
│        insertUser() / selectUser() / updateUser()   │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│    UserServiceImpl (암호화/복호화 로직 통합)          │
│  - insertUser(): email/phone/address 암호화 후 저장 │
│  - selectUser(): DB에서 조회 후 복호화              │
│  - updateUser(): 변경 필드 암호화 후 수정            │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│     EncryptionService (테넌트별 키 관리)            │
│  - getActiveEncryptionKey(tenantId)                 │
│  - encryptEmail/Phone/Address()                     │
│  - decryptEmail/Phone/Address()                     │
│  - rotateEncryptionKey()                            │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│   AesGcmCryptoUtil (저수준 암호화 유틸)             │
│  - encrypt(plainText, secretKey)                    │
│  - decrypt(encryptedText, secretKey)                │
│  - generateKey() / keyToString() / stringToKey()    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│      EncryptionKeyMapper (DB 접근)                  │
│  - selectActiveKeyByTenant(tenantId)                │
│  - insertEncryptionKey()                            │
│  - updateKeyActiveStatus()                          │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│   tb_encryption_key 테이블 (암호화 키 저장)         │
│  ├─ key_id (UUID)                                   │
│  ├─ tenant_id (고객사 ID)                           │
│  ├─ encrypted_key (Base64 인코딩된 키)              │
│  ├─ algorithm (AES-256-GCM)                         │
│  ├─ is_active (Y/N)                                 │
│  └─ rotated_dt (로테이션 일시)                      │
└─────────────────────────────────────────────────────┘
```

---

## 📁 구현 파일 구조

```
LMS/
├── src/main/java/kr/co/lms/
│   ├── crypto/
│   │   └── AesGcmCryptoUtil.java          ✅ 저수준 암호화 유틸 (152줄)
│   │
│   ├── vo/
│   │   ├── UserVO.java                    ✅ 수정됨 (tenantId 추가)
│   │   └── EncryptionKeyVO.java           ✅ 암호화 키 VO
│   │
│   ├── mapper/
│   │   ├── UserMapper.java                ✅ 기존 파일
│   │   └── EncryptionKeyMapper.java       ✅ 암호화 키 Mapper
│   │
│   ├── service/
│   │   ├── EncryptionService.java         ✅ 암호화 서비스 인터페이스
│   │   └── impl/
│   │       ├── UserServiceImpl.java        ✅ 수정됨 (암호화 통합)
│   │       └── EncryptionServiceImpl.java  ✅ 암호화 서비스 구현 (320줄)
│   │
│   └── web/controller/
│       └── UserController.java             (기존 - 수정 불필요)
│
├── src/main/resources/
│   ├── egovframework/sqlmap/
│   │   ├── saas-schema-final.sql          ✅ 수정됨 (tb_encryption_key 추가)
│   │   ├── user-mapper.xml                ✅ 수정됨 (tenantId 추가)
│   │   ├── encryption-key-mapper.xml      ✅ 암호화 키 MyBatis 매핑
│   │   └── saas-initial-data-final.sql    ⏳ 초기 데이터 추가 필요
│   │
│   └── application.properties              ✅ 수정됨 (암호화 설정 추가)
│
└── ENCRYPTION_IMPLEMENTATION_GUIDE.md     ✅ 이 문서
```

---

## 🔄 데이터 플로우

### A. 회원 등록 (insertUser)

```
클라이언트 요청
  ↓
  ├─ UserVO (평문 포함)
  │   ├─ email: "test@example.com"
  │   ├─ phone: "010-1234-5678"
  │   └─ address: "서울시 강남구..."
  ↓
UserServiceImpl.insertUser()
  ↓
  ├─ tenantId 검증 (필수)
  ├─ encryptionService.encryptEmail(tenantId, email)
  │   └─ 암호화된 값: "GKqP2+8Xm9vZ..."
  ├─ encryptionService.encryptPhone(tenantId, phone)
  │   └─ 암호화된 값: "aB1cD2eF3gH4..."
  └─ encryptionService.encryptAddress(tenantId, address)
       └─ 암호화된 값: "xY9zW8vU7tS6..."
  ↓
UserMapper.insertUser()
  ↓
데이터베이스 저장 (암호화된 값)
  ↓
tb_user 테이블
  ├─ email: "GKqP2+8Xm9vZ..."  (암호화됨)
  ├─ phone: "aB1cD2eF3gH4..."  (암호화됨)
  └─ address: "xY9zW8vU7tS6..." (암호화됨)
```

### B. 회원 조회 (selectUser)

```
클라이언트 요청
  ↓
UserServiceImpl.selectUser(userId)
  ↓
UserMapper.selectUser(userId)
  ↓
데이터베이스에서 조회
  ↓
tb_user 테이블 (암호화된 데이터)
  ├─ email: "GKqP2+8Xm9vZ..."
  ├─ phone: "aB1cD2eF3gH4..."
  └─ address: "xY9zW8vU7tS6..."
  ↓
UserServiceImpl 복호화 처리
  ├─ encryptionService.decryptEmail(tenantId, encrypted)
  │   └─ 복호화: "test@example.com"
  ├─ encryptionService.decryptPhone(tenantId, encrypted)
  │   └─ 복호화: "010-1234-5678"
  └─ encryptionService.decryptAddress(tenantId, encrypted)
       └─ 복호화: "서울시 강남구..."
  ↓
UserVO (평문 포함)
  ├─ email: "test@example.com"
  ├─ phone: "010-1234-5678"
  └─ address: "서울시 강남구..."
  ↓
클라이언트 응답
```

### C. 회원 수정 (updateUser)

```
클라이언트 요청 (수정할 데이터)
  ↓
UserVO
  ├─ userId: "user123"
  ├─ email: "newemail@example.com" (변경됨)
  ├─ phone: "010-9876-5432" (변경됨)
  └─ address: "부산시..." (변경됨)
  ↓
UserServiceImpl.updateUser()
  ↓
  ├─ encryptionService.encryptEmail()
  ├─ encryptionService.encryptPhone()
  └─ encryptionService.encryptAddress()
  ↓
암호화된 값으로 변환
  ├─ email: "qW2eR3tY4uI5..." (암호화됨)
  ├─ phone: "oP6sD7fG8hJ9..." (암호화됨)
  └─ address: "kL0zX1cV2bN3..." (암호화됨)
  ↓
UserMapper.updateUser()
  ↓
데이터베이스 업데이트 (암호화된 값)
```

---

## 🔑 키 관리 전략

### 1. 테넌트별 독립 키

**구조**:
```
테넌트 A (TENANT001)
  ├─ 활성 키: AES-256-GCM-v1 (현재)
  ├─ 이전 키: AES-256-GCM-v0 (로테이션됨)
  └─ 사용자 데이터 (모두 AES-256-GCM-v1로 암호화)

테넌트 B (TENANT002)
  ├─ 활성 키: AES-256-GCM-v1 (현재)
  └─ 사용자 데이터 (모두 AES-256-GCM-v1로 암호화)
```

**장점**:
- ✅ 테넌트 간 완벽한 데이터 격리
- ✅ 한 테넌트 키 유출 → 다른 테넌트 영향 없음
- ✅ 각 테넌트별 보안 정책 적용 가능

### 2. 키 저장소: 데이터베이스

**저장 위치**: `tb_encryption_key` 테이블

```sql
CREATE TABLE tb_encryption_key (
    key_id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    key_name VARCHAR(100) NOT NULL,
    encrypted_key LONGTEXT NOT NULL,    -- Base64 인코딩
    algorithm VARCHAR(50),              -- AES-256-GCM
    key_size INT,                       -- 256
    is_active CHAR(1),                  -- Y/N
    rotation_policy VARCHAR(50),        -- manual/automatic
    rotated_dt DATETIME,
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP,
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id),
    UNIQUE KEY uk_tenant_active (tenant_id, is_active)
);
```

**왜 DB 저장인가?**
- ✅ 운영 편의성: 파일 관리 불필요
- ✅ 확장성: 테넌트 추가 시 자동 관리
- ✅ 감시: DB 감사 로그로 접근 기록 추적 가능
- ❌ 보안 주의: 마스터 키로 추가 암호화 권장 (향후 개선)

### 3. 키 로테이션 (수동)

**프로세스**:
```
관리자: "테넌트 A의 암호화 키 로테이션 요청"
  ↓
EncryptionServiceImpl.rotateEncryptionKey()
  ├─ Step 1: 새로운 AES-256 키 생성
  ├─ Step 2: tb_encryption_key에 삽입 (is_active = Y)
  ├─ Step 3: 기존 활성 키 비활성화 (is_active = N)
  └─ Step 4: rotated_dt 기록
  ↓
향후 신규 데이터는 새 키로 암호화
기존 데이터는 이전 키로 복호화 (is_active = N인 키도 보관)
```

**주의**: 
- 기존 암호화 데이터를 새 키로 재암호화하려면 별도 마이그레이션 배치 필요
- 현재: 새 키로 앞으로의 암호화만 진행, 기존 데이터는 이전 키로 복호화

---

## ⚙️ 설정 (application.properties)

```properties
# ============================================
# 암호화 설정 (AES-256-GCM)
# ============================================
encryption.algorithm=AES/GCM/NoPadding
encryption.key-size=256
encryption.gcm-tag-length=128
encryption.gcm-iv-length=12
encryption.key-storage=database
encryption.policy=tenant-based
encryption.rotation-policy=manual

# ============================================
# 개인정보 암호화 대상
# ============================================
encryption.targets=email,phone,address

# ============================================
# 로깅 설정
# ============================================
logging.level.kr.co.lms.service.impl.EncryptionServiceImpl=DEBUG
logging.level.kr.co.lms.service.impl.UserServiceImpl=DEBUG
```

---

## 🔄 eGovFrame vs Spring Boot 비교

### 배포 및 초기화 방식

**eGovFrame (현재)**:
```bash
# Step 1: WAR 빌드
mvn clean compile war:war
# → target/LMS.war 생성

# Step 2: Tomcat에 배포
# → LMS.war를 $CATALINA_HOME/webapps에 복사
# → Tomcat 시작

# Step 3: 자동 초기화 (EncryptionInitializer @PostConstruct)
# [ENCRYPTION] 암호화 키 초기화 시작...
# [ENCRYPTION] 테넌트 'TENANT001' 새로운 암호화 키 생성 완료
# [ENCRYPTION] 암호화 키 초기화 완료
```

**Spring Boot (이전 예시 - 사용하지 마세요)**:
```bash
# mvn spring-boot:run  # ❌ eGovFrame에서 지원 안 됨
# java -jar lms.jar   # ❌ WAR 파일이므로 작동 안 함
```

---

## 📝 사용 예시

### 1. 회원 등록

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserVO userVO) {
        // tenantId는 요청에서 추출 (예: JWT 토큰 또는 헤더)
        userVO.setTenantId("TENANT001");
        
        // email, phone, address는 평문으로 요청
        // UserServiceImpl에서 자동으로 암호화되어 저장됨
        int result = userService.insertUser(userVO);
        
        return result > 0 ? ResponseEntity.ok("등록 성공") : 
               ResponseEntity.status(400).body("등록 실패");
    }
}
```

**흐름**:
```
요청: {
  "userId": "user123",
  "userName": "김철수",
  "email": "kim@example.com",        ← 평문
  "phone": "010-1234-5678",          ← 평문
  "address": "서울시 강남구 테헤란로 123"  ← 평문
}
  ↓
UserServiceImpl.insertUser()에서 암호화
  ↓
저장: {
  "userId": "user123",
  "userName": "김철수",
  "email": "GKqP2+8Xm9vZ...",        ← 암호화됨
  "phone": "aB1cD2eF3gH4...",        ← 암호화됨
  "address": "xY9zW8vU7tS6..."       ← 암호화됨
}
```

### 2. 회원 조회

```java
@GetMapping("/users/{userId}")
public ResponseEntity<?> getUser(@PathVariable String userId) {
    UserVO user = userService.selectUser(userId);
    
    // user 객체의 email, phone, address는 자동으로 복호화됨
    return ResponseEntity.ok(user);
}
```

**흐름**:
```
조회 요청: userId = "user123"
  ↓
DB에서 조회 (암호화된 데이터)
  {
    "userId": "user123",
    "email": "GKqP2+8Xm9vZ...",    ← 암호화됨
    "phone": "aB1cD2eF3gH4...",    ← 암호화됨
    "address": "xY9zW8vU7tS6..."   ← 암호화됨
  }
  ↓
UserServiceImpl.selectUser()에서 복호화
  ↓
응답: {
  "userId": "user123",
  "userName": "김철수",
  "email": "kim@example.com",         ← 복호화됨
  "phone": "010-1234-5678",           ← 복호화됨
  "address": "서울시 강남구 테헤란로 123"   ← 복호화됨
}
```

---

## 🧪 테스트 및 검증

### A. 암호화/복호화 기본 테스트

```java
@Test
void testEncryptionAndDecryption() throws Exception {
    // 키 생성
    SecretKey key = AesGcmCryptoUtil.generateKey();
    
    // 암호화
    String plainText = "test@example.com";
    String encrypted = AesGcmCryptoUtil.encrypt(plainText, key);
    
    // 검증 1: 평문과 암호문이 다른가?
    assertNotEquals(plainText, encrypted);
    
    // 복호화
    String decrypted = AesGcmCryptoUtil.decrypt(encrypted, key);
    
    // 검증 2: 복호화 결과가 원본과 같은가?
    assertEquals(plainText, decrypted);
}
```

### B. 테넌트별 키 격리 테스트

```java
@Test
void testTenantKeyIsolation() throws Exception {
    // TENANT001의 키로 암호화
    EncryptionKeyVO key001 = encryptionService.getActiveEncryptionKey("TENANT001");
    String encrypted001 = encryptionService.encryptEmail("TENANT001", "test@example.com");
    
    // TENANT002의 키로 복호화 시도
    assertThrows(Exception.class, () -> {
        encryptionService.decryptEmail("TENANT002", encrypted001);
    });
    // → 예외 발생 (다른 테넌트 키로 복호화 불가)
}
```

### C. DB 데이터 확인

```sql
-- 암호화된 데이터 확인
SELECT user_id, email, phone, address
FROM tb_user
WHERE user_id = 'user123';

-- 결과 (암호화됨):
-- user_id: user123
-- email: GKqP2+8Xm9vZ1a3bC4dE5fG6hI7jK8lM9nO0pQ1rS2tU3vW4xY5zA6bC7dE8fG
-- phone: aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1wX2yZ3aA4bB5cC6dD7eE8fF9gG0h
-- address: xY9zW8vU7tS6rQ5pO4nM3lK2jI1hG0fE9dD8cC7bB6aA5zZ4yY3xX2wW1vV0u

-- 암호화 키 확인
SELECT tenant_id, key_name, is_active, rotated_dt
FROM tb_encryption_key
WHERE tenant_id IN ('TENANT001', 'TENANT002');

-- 결과:
-- tenant_id: TENANT001, key_name: AES-256-GCM-v1, is_active: Y, rotated_dt: 2025-03-17
-- tenant_id: TENANT002, key_name: AES-256-GCM-v1, is_active: Y, rotated_dt: 2025-03-17
```

---

## 🚀 배포 및 운영

### 1. 초기 설정

**Step 1**: 데이터베이스 스키마 생성
```bash
# MySQL 클라이언트에서 실행
mysql> source src/main/resources/egovframework/sqlmap/saas-schema-final.sql;
```

**Step 2**: 초기 데이터 로드
```bash
mysql> source src/main/resources/egovframework/sqlmap/saas-initial-data-final.sql;
```

**Step 3**: eGovFrame 애플리케이션 시작 (암호화 키 자동 생성)
```bash
# Tomcat에 배포 후 시작 (WAR 파일)
# 또는 Maven으로 직접 실행
mvn clean compile war:war

# 그 후 Tomcat에 배포
```

**자동 초기화 과정**:
```
eGovFrame 애플리케이션 시작 (WAR 배포)
  ↓
Spring Context 초기화
  ↓
EncryptionInitializer 빈 생성
  ↓
@PostConstruct 어노테이션 메서드 실행 (@로 표시됨)
  ↓
initializeEncryptionKeys() 메서드 실행
  ↓
각 테넌트별 활성 키 확인
  ├─ TENANT001: 키 없음 → 자동 생성 (AES-256-GCM-v1)
  ├─ TENANT002: 키 없음 → 자동 생성 (AES-256-GCM-v1)
  └─ 이미 있음: 스킵
  ↓
로그: [ENCRYPTION] 암호화 키 초기화 완료
  ↓
eGovFrame 애플리케이션 준비 완료
```

**로그 확인**:
```
[ENCRYPTION] 암호화 키 초기화 시작...
[ENCRYPTION] 테넌트 암호화 키 초기화 - tenantId: TENANT001
[ENCRYPTION] 테넌트 'TENANT001' 새로운 암호화 키 생성 완료 - keyId: xxx, keyName: AES-256-GCM-v1
[ENCRYPTION] 테넌트 암호화 키 초기화 - tenantId: TENANT002
[ENCRYPTION] 테넌트 'TENANT002' 새로운 암호화 키 생성 완료 - keyId: yyy, keyName: AES-256-GCM-v1
[ENCRYPTION] 암호화 키 초기화 완료
```

### 2. 운영 중 모니터링

**로깅**:
```
# application.properties 설정
logging.level.kr.co.lms.service.impl.EncryptionServiceImpl=DEBUG
```

**로그 예시**:
```
[DEBUG] [ENCRYPTION] 활성 암호화 키 조회 - tenantId: TENANT001
[DEBUG] [ENCRYPTION] 이메일 암호화 성공 - tenantId: TENANT001
[DEBUG] [ENCRYPTION] 이메일 복호화 성공 - tenantId: TENANT001
[WARN] [ENCRYPTION] 테넌트의 활성 암호화 키 없음 - tenantId: TENANT999
```

### 3. 키 로테이션

**관리자 작업**:
```java
// 기존 키 대체
EncryptionKeyVO newKey = encryptionService.rotateEncryptionKey(
    "TENANT001", 
    "AES-256-GCM-v2"
);

// 로그 확인
// [WARN] [ENCRYPTION] 암호화 키 로테이션 시작 - tenantId: TENANT001, newKeyName: AES-256-GCM-v2
// [WARN] [ENCRYPTION] 암호화 키 로테이션 완료 - 새 keyId: xxx, 기존 keyId: yyy
```

**효과**:
- ✅ 새로운 회원 가입 → 새 키(v2)로 암호화
- ✅ 기존 회원 조회 → 이전 키(v1)로 자동 복호화
- ✅ 데이터 무결성 보장

---

## 🛡️ 보안 고려사항

### ✅ 구현된 보안 조치

1. **무결성 인증**: GCM 모드는 자동으로 인증 태그 생성/검증
2. **난수 IV**: 매번 새로운 96비트 IV 생성 (같은 평문도 다르게 암호화)
3. **테넌트 격리**: 각 테넌트별 독립 키 관리
4. **키 버전 관리**: 기존 키 보관으로 모든 암호화 데이터 복호화 가능

### ⚠️ 향후 개선 사항

1. **마스터 키 암호화**: DB 저장 키를 마스터 키로 추가 암호화
   ```
   tb_encryption_key.encrypted_key = MasterKey.encrypt(AES256Key)
   ```

2. **HSM (Hardware Security Module) 연동**: 키 저장소를 HSM으로 이동

3. **자동 키 로테이션**: 설정된 주기로 자동 로테이션

4. **감사 로깅**: 암호화/복호화 모든 작업 기록 (별도 감사 테이블)

5. **데이터 마이그레이션**: 기존 암호화 데이터를 새 키로 재암호화

---

## 📚 관련 파일 상세

### 1. AesGcmCryptoUtil.java (152줄)

**책임**: 저수준 AES-256-GCM 암호화/복호화

**주요 메서드**:
```java
// 키 생성
SecretKey generateKey();

// 암호화 (평문 → Base64 암호문)
String encrypt(String plainText, SecretKey secretKey);

// 복호화 (Base64 암호문 → 평문)
String decrypt(String encryptedText, SecretKey secretKey);

// 키 직렬화
String keyToString(SecretKey key);
SecretKey stringToKey(String keyString);

// 키 길이 확인
int getKeySize(SecretKey key);
```

### 2. EncryptionService.java (105줄)

**책임**: 비즈니스 로직 수준의 암호화 서비스 인터페이스

**주요 메서드**:
```java
// 테넌트별 활성 키 조회
EncryptionKeyVO getActiveEncryptionKey(String tenantId);

// 버전별 키 조회 (복호화용)
EncryptionKeyVO getEncryptionKeyByVersion(String tenantId, String keyName);

// 새 키 생성
EncryptionKeyVO createNewEncryptionKey(String tenantId, String keyName);

// 개인정보 암호화/복호화
String encryptEmail/Phone/Address(String tenantId, String plainText);
String decryptEmail/Phone/Address(String tenantId, String encryptedText);

// 키 로테이션
EncryptionKeyVO rotateEncryptionKey(String tenantId, String newKeyName);
```

### 3. EncryptionServiceImpl.java (320줄)

**책임**: EncryptionService 구현체

**로직**:
- DB에서 테넌트별 활성 키 조회
- 없으면 예외 던짐
- AES-256-GCM으로 암호화/복호화
- 모든 작업 [ENCRYPTION] 로그 기록
- 키 로테이션 시 기존 키 비활성화, 새 키 활성화

### 4. UserServiceImpl.java (299줄, 수정됨)

**책임**: 회원 관리 비즈니스 로직 + 암호화 통합

**수정 사항**:
- EncryptionService 의존성 주입
- insertUser(): email, phone, address 암호화 후 저장
- selectUser(): DB에서 조회 후 복호화
- selectUserList(): 각 사용자별 복호화
- updateUser(): 변경 필드 암호화 후 수정
- selectUserForLogin(): 로그인 시 복호화

### 5. user-mapper.xml (수정됨)

**수정 사항**:
- resultMap에 tenantId 추가
- SELECT 쿼리에 tenant_id 필드 포함
- INSERT 쿼리에 tenant_id 추가
- UPDATE 쿼리에 tenant_id 조건 추가
- selectUserList에 tenantId 필터링 추가

### 6. saas-schema-final.sql (수정됨)

**수정 사항**:
- tb_encryption_key 테이블 추가
- tb_user에 tenant_id 복합 PK 추가
- 외래키 제약 조건 추가

### 7. EncryptionInitializer.java (신규, eGovFrame 호환)

**책임**: eGovFrame 애플리케이션 시작 시 자동으로 각 테넌트의 암호화 키 생성

**구현 방식**:
- `@PostConstruct` 어노테이션 사용 (Spring Framework 표준)
- eGovFrame WAR 패키징 호환
- Spring Context 초기화 후 자동 실행
- 각 테넌트별 활성 키 존재 여부 확인
- 키가 없으면 자동 생성

**동작 과정**:
```
1. eGovFrame 애플리케이션 시작 (WAR 배포)
   ↓
2. Spring Context 초기화
   ↓
3. EncryptionInitializer 빈 생성
   ↓
4. 의존성 주입 완료
   (encryptionService, encryptionKeyMapper)
   ↓
5. @PostConstruct 메서드 자동 호출
   initializeEncryptionKeys()
   ↓
6. 테넌트 리스트 순회 (TENANT001, TENANT002, ...)
   ↓
7. 각 테넌트별:
   ├─ tb_encryption_key에서 활성 키(is_active = Y) 조회
   ├─ 있음: 스킵 (로그만 출력)
   └─ 없음: AesGcmCryptoUtil로 새 키 생성 + DB 저장
   ↓
8. 초기화 완료 로그
```

**eGovFrame vs Spring Boot 비교**:

| 항목 | eGovFrame | Spring Boot |
|------|----------|------------|
| **어노테이션** | @PostConstruct (javax.annotation) | ApplicationRunner |
| **패키징** | WAR (전통 서블릿 컨테이너) | JAR (내장 톰캣) |
| **시작 방식** | Tomcat 배포 | java -jar 또는 mvn spring-boot:run |
| **빈 생명주기** | InitializingBean.afterPropertiesSet() 또는 @PostConstruct | ApplicationRunner.run() |
| **호환성** | Spring Framework 3.1+ | Spring Boot 1.0+ |

**로그 예시**:
```
[ENCRYPTION] 암호화 키 초기화 시작...
[DEBUG] [ENCRYPTION] 테넌트 암호화 키 초기화 - tenantId: TENANT001
[INFO] [ENCRYPTION] 테넌트 'TENANT001' 새로운 암호화 키 생성 완료 - keyId: 550e8400-e29b-41d4-a716-446655440000, keyName: AES-256-GCM-v1
[DEBUG] [ENCRYPTION] 테넌트 암호화 키 초기화 - tenantId: TENANT002
[INFO] [ENCRYPTION] 테넌트 'TENANT002' 새로운 암호화 키 생성 완료 - keyId: 6ba7b810-9dad-11d1-80b4-00c04fd430c8, keyName: AES-256-GCM-v1
[INFO] [ENCRYPTION] 암호화 키 초기화 완료
```

**장점**:
- ✅ 자동화: 수동 초기화 불필요
- ✅ 안전: 키를 SQL에 하드코딩하지 않음
- ✅ 유연: 테넌트 추가 시 자동 대응
- ✅ 추적: 초기화 과정 로깅
- ✅ 멱등성: 여러 번 실행해도 기존 키 보존

---

## 📞 문제 해결

### Q1: "테넌트의 활성 암호화 키가 없습니다" 예외 발생

**원인**: tb_encryption_key 테이블에 해당 테넌트의 활성 키가 없음

**해결**:
```sql
-- 1. 테넌트 확인
SELECT DISTINCT tenant_id FROM tb_user;

-- 2. 각 테넌트별 키 생성
INSERT INTO tb_encryption_key (...)
VALUES (UUID(), 'TENANT001', 'AES-256-GCM-v1', ...);

-- 3. 또는 Java에서 생성
encryptionService.createNewEncryptionKey("TENANT001", "AES-256-GCM-v1");
```

### Q2: 복호화 후 깨진 문자가 나옴

**원인**: 잘못된 키로 복호화됨 (인증 태그 검증 실패)

**해결**:
```java
// 올바른 tenantId 확인
String tenantId = user.getTenantId();
if (tenantId == null) {
    throw new RuntimeException("tenantId가 필수입니다");
}

// 활성 키 확인
EncryptionKeyVO key = encryptionService.getActiveEncryptionKey(tenantId);
if (key == null) {
    throw new RuntimeException("암호화 키가 없습니다");
}
```

### Q3: 다른 테넌트의 데이터가 조회됨

**원인**: user-mapper.xml에서 WHERE 절에 tenant_id 조건 누락

**해결**:
```xml
<!-- ❌ 잘못된 쿼리 -->
<select id="selectUser">
    SELECT * FROM tb_user WHERE user_id = #{userId}
</select>

<!-- ✅ 올바른 쿼리 -->
<select id="selectUser">
    SELECT * FROM tb_user 
    WHERE user_id = #{userId} AND tenant_id = #{tenantId}
</select>
```

---

## 📖 참고 문서

- **KISA 개인정보 보호법 안전성 확보조치 기준 제7조**
  - 암호화 필수 항목: 비밀번호, 주민등록번호, 여권번호, 운전면허번호 등
  - 권장 암호화: 이메일, 전화번호, 주소 등 개인정보

- **NIST FIPS 197** - Advanced Encryption Standard (AES)
  
- **NIST SP 800-38D** - Recommendation for Block Cipher Modes of Operation: Galois/Counter Mode (GCM)

---

## ✅ 체크리스트

### 배포 전 확인 사항 (eGovFrame)

**1️⃣ 데이터베이스 준비**:
- [ ] DB 스키마 생성됨 (saas-schema-final.sql 실행)
- [ ] 초기 데이터 로드됨 (saas-initial-data-final.sql 실행)

**2️⃣ 코드 준비**:
- [ ] UserServiceImpl 암호화 로직 통합됨
- [ ] user-mapper.xml tenantId 포함됨
- [ ] application.properties 설정됨 (암호화 설정 포함)
- [ ] EncryptionInitializer 클래스 존재함 (@PostConstruct 사용)

**3️⃣ eGovFrame 애플리케이션 시작**:
- [ ] WAR 파일 빌드됨 (`mvn clean compile war:war`)
- [ ] Tomcat에 배포됨 (`$CATALINA_HOME/webapps/LMS.war`)
- [ ] Tomcat 시작됨 (`$CATALINA_HOME/bin/startup.sh`)
- [ ] EncryptionInitializer @PostConstruct 자동 실행됨
  - [ ] TENANT001 암호화 키 자동 생성됨
  - [ ] TENANT002 암호화 키 자동 생성됨

**4️⃣ 검증**:
- [ ] Tomcat 로그에서 [ENCRYPTION] 초기화 메시지 확인됨
- [ ] 로깅 레벨 설정됨 (DEBUG for EncryptionServiceImpl)
- [ ] 테스트 환경에서 암호화/복호화 동작 확인됨
- [ ] DB에 저장된 암호화된 데이터 확인됨
- [ ] 조회 시 복호화 정상 동작 확인됨

---

## 📝 변경 이력

| 일시 | 작업 | 상태 |
|------|------|------|
| 2025-03-17 | 암호화 시스템 전체 구현 | ✅ 완료 |
| 2025-03-17 | UserServiceImpl 암호화 통합 | ✅ 완료 |
| 2025-03-17 | user-mapper.xml tenantId 추가 | ✅ 완료 |
| 2025-03-17 | application.properties 설정 | ✅ 완료 |
| 2025-03-17 | 이 가이드 문서 작성 | ✅ 완료 |
| 2025-03-17 | EncryptionInitializer (자동 키 생성) - @PostConstruct 사용 | ✅ 완료 |
| 2025-03-17 | UserInitializer (초기 사용자 생성 + 자동 암호화) | ✅ 완료 |
| 2025-03-17 | saas-initial-data-final.sql 수정 (tb_user INSERT 제거) | ✅ 완료 |
| 2025-03-17 | 회원 정보 암호화/복호화 로직 완성 | ✅ 완료 |
| TBD | JUnit 테스트 코드 추가 | ⏳ 대기 |
| TBD | 초기 데이터 마이그레이션 (기존 평문 암호화) | ⏳ 대기 |
| TBD | 자동 키 로테이션 배치 | ⏳ 대기 |
| TBD | HSM 연동 (선택사항) | ⏳ 대기 |

---

**문서 작성자**: Claude Code (Sisyphus)  
**작성일**: 2025-03-17  
**프로젝트**: LMS (Learning Management System)  
**버전**: 1.0
