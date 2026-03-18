# 회원가입 페이지 구현 가이드

**작성일**: 2025-03-18  
**상태**: ✅ 완료  
**담당자**: Claude Code (Sisyphus)

---

## 📋 개요

LMS 프로젝트에 완전한 회원가입 시스템을 구현했습니다.

**핵심 요구사항 충족:**
- ✅ 가입 시, 임시적으로 테넌트를 선택 가능
- ✅ 설정된 암호화 대상 컬럼(email, phone, address) 암호화 적용
- ✅ 아이디, 비밀번호는 일반적으로 널리 사용되는 패턴 적용
- ✅ 보안상 문제 없는 수준의 강력한 구현

---

## 🏗️ 아키텍처 구성도

```
┌─────────────────────────────────────────────────┐
│      회원가입 프론트엔드                           │
│  (register.html - HTML5 + Vanilla JavaScript)   │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      회원가입 REST API 엔드포인트                  │
│  (AuthApiController)                            │
│  - POST /api/v1/auth/register                  │
│  - POST /api/v1/auth/check-userid              │
│  - GET  /api/v1/tenants                        │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      회원가입 비즈니스 로직                        │
│  (UserService.registerUser)                    │
│  - 입력값 검증                                   │
│  - ID 중복 확인                                  │
│  - 비밀번호 BCrypt 암호화                        │
│  - 사용자 정보 저장                              │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      암호화/복호화 레이어                          │
│  (EncryptionService)                           │
│  - email, phone, address                       │
│  - AES-256-GCM 양방향 암호화                    │
└────────────────────┬────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────┐
│      데이터베이스                                 │
│  (tb_user, tb_tenant)                          │
│  - 암호화된 개인정보 저장                         │
│  - 테넌트별 격리                                 │
└─────────────────────────────────────────────────┘
```

---

## 📁 구현 파일 목록

### 1. **Backend - Value Object (VO)**

#### 📄 `RegisterRequestVO.java`
**경로**: `src/main/java/kr/co/lms/vo/RegisterRequestVO.java`

회원가입 요청 데이터를 담는 VO

**주요 필드:**
```java
@NotBlank
@Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$")
private String userId;                    // 사용자 ID (4-20자, 영문/숫자/언더스코어)

@NotBlank
@Size(min = 8, max = 100)
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])...")
private String password;                  // 비밀번호 (8자 이상, 복합 문자)

@NotBlank
private String passwordConfirm;           // 비밀번호 확인

@NotBlank
@Email
private String email;                     // 이메일 (유효한 형식)

@NotBlank
private String userName;                  // 사용자명 (1-50자)

@Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$|^\\d{10,11}$")
private String phone;                     // 전화번호

@Size(max = 255)
private String address;                   // 주소 (선택사항)

@NotBlank
private String tenantId;                  // 테넌트 ID (필수)
```

**검증 규칙:**
- **userId**: 4-20자, 영문 대소문자, 숫자, 언더스코어만 허용
- **password**: 
  - 8자 이상
  - 영문 소문자, 대문자, 숫자, 특수문자(@$!%*?&) **모두** 포함 필수
  - OWASP 권장 규칙 준수
- **email**: RFC 5322 표준 이메일 형식
- **phone**: `010-1234-5678` 형식 또는 10-11자 숫자
- **tenantId**: 필수 (회원이 소속할 조직)

#### 📄 `RegisterResponseVO.java`
**경로**: `src/main/java/kr/co/lms/vo/RegisterResponseVO.java`

회원가입 응답 데이터

```java
private boolean success;      // 성공 여부
private String message;       // 메시지
private String userId;        // 생성된 사용자 ID (성공 시만)
private String email;         // 이메일
private String userName;      // 사용자명
private String tenantId;      // 테넌트 ID
```

---

### 2. **Backend - Configuration**

#### 📄 `SecurityConfig.java`
**경로**: `src/main/java/kr/co/lms/config/SecurityConfig.java`

Spring Security 비밀번호 암호화 설정

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);  // strength = 12
}
```

**BCryptPasswordEncoder 선택 이유:**
- ✅ OWASP 권장 알고리즘
- ✅ 단방향 암호화 (복호화 불가능 - 비밀번호 유출 시에도 안전)
- ✅ 자동 salt 생성으로 rainbow table 공격 방어
- ✅ Spring Security에 내장 (별도 의존성 불필요)
- ✅ 시간이 지남에 따라 strength 증가 가능

---

### 3. **Backend - Service**

#### 📄 `UserService.java` (인터페이스)
**경로**: `src/main/java/kr/co/lms/service/UserService.java`

**신규 메서드:**

```java
/**
 * 회원가입 처리
 * @param registerRequest 회원가입 요청 정보
 * @return 회원가입 응답
 */
RegisterResponseVO registerUser(RegisterRequestVO registerRequest);

/**
 * 사용자 ID 중복 확인
 * @param userId 확인할 사용자 ID
 * @param tenantId 테넌트 ID
 * @return true: 중복, false: 사용 가능
 */
boolean isUserIdDuplicate(String userId, String tenantId);
```

#### 📄 `UserServiceImpl.java` (구현체)
**경로**: `src/main/java/kr/co/lms/service/impl/UserServiceImpl.java`

**회원가입 처리 프로세스:**

```
1. 입력값 검증
   └─ 비밀번호 일치 확인 (password === passwordConfirm)

2. ID 중복 확인
   └─ 같은 테넌트 내에서 존재하는지 확인

3. 비밀번호 암호화
   └─ BCryptPasswordEncoder.encode() 적용
   └─ 단방향 암호화 (복호화 불가)

4. UserVO 생성 및 데이터 설정
   └─ userId, tenantId, userName, email, phone, address, etc.

5. insertUser() 호출
   └─ email, phone, address → AES-256-GCM으로 암호화
   └─ 암호화된 데이터 DB 저장

6. 응답 반환
   └─ 성공/실패 메시지 포함
```

**ID 중복 확인 로직:**

```java
public boolean isUserIdDuplicate(String userId, String tenantId) {
    UserVO existingUser = userMapper.selectUser(userId);
    
    // 같은 테넌트 내에서 존재하는지 확인
    if (existingUser != null && tenantId.equals(existingUser.getTenantId())) {
        return true;  // 중복
    }
    return false;     // 사용 가능
}
```

---

### 4. **Backend - REST API Controller**

#### 📄 `AuthApiController.java`
**경로**: `src/main/java/kr/co/lms/web/api/v1/AuthApiController.java`

**엔드포인트:**

##### ① 회원가입
```
POST /api/v1/auth/register

요청:
{
  "userId": "john_doe",
  "password": "SecurePass@123",
  "passwordConfirm": "SecurePass@123",
  "userName": "John Doe",
  "email": "john@example.com",
  "phone": "010-1234-5678",
  "address": "서울시 강남구",
  "tenantId": "TENANT001"
}

응답 (성공):
{
  "success": true,
  "message": "회원가입이 완료되었습니다.",
  "data": {
    "success": true,
    "message": "회원가입이 완료되었습니다.",
    "userId": "john_doe",
    "email": "john@example.com",
    "userName": "John Doe",
    "tenantId": "TENANT001"
  },
  "timestamp": "2025-03-18T10:30:45"
}

응답 (실패):
{
  "success": false,
  "message": "이미 사용 중인 아이디입니다.",
  "data": null,
  "timestamp": "2025-03-18T10:30:45"
}
```

**검증:**
- Bean Validation (@Valid)으로 자동 검증
- 검증 실패 시 상세 오류 메시지 반환
- HTTP 400 Bad Request

##### ② ID 중복 확인
```
POST /api/v1/auth/check-userid

요청:
{
  "userId": "john_doe",
  "tenantId": "TENANT001"
}

응답:
{
  "success": true,
  "message": "사용 가능한 아이디입니다.",
  "data": {
    "isDuplicate": false,
    "userId": "john_doe"
  },
  "timestamp": "2025-03-18T10:30:45"
}
```

#### 📄 `TenantApiController.java`
**경로**: `src/main/java/kr/co/lms/web/api/v1/TenantApiController.java`

**엔드포인트:**

##### 테넌트 목록 조회
```
GET /api/v1/tenants

응답:
{
  "success": true,
  "message": "테넌트 목록 조회 성공",
  "data": [
    {
      "tenantId": "TENANT001",
      "tenantName": "한국 교육 센터",
      "tenantDesc": "한국의 주요 교육 기관"
    },
    {
      "tenantId": "TENANT002",
      "tenantName": "글로벌 러닝 센터",
      "tenantDesc": "국제 온라인 학습 플랫폼"
    }
  ],
  "timestamp": "2025-03-18T10:30:45"
}
```

---

### 5. **Frontend**

#### 📄 `register.html`
**경로**: `src/main/webapp/html/register.html`

완전한 회원가입 페이지 구현

**특징:**

**1. 반응형 디자인**
- 모바일, 태블릿, 데스크톱 모두 지원
- 최대 너비 500px (모바일 우선)
- 부드러운 그래디언트 배경

**2. 폼 필드**
- 테넌트 선택 (드롭다운)
- 사용자 ID (중복확인 버튼 포함)
- 비밀번호 (강도 표시기 포함)
- 비밀번호 확인
- 사용자명
- 이메일
- 전화번호
- 주소 (선택사항)

**3. 클라이언트 검증**
```javascript
// 실시간 검증
- ID 형식: /^[a-zA-Z0-9_]{4,20}$/
- 비밀번호: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])...$/
- 이메일: RFC 5322 표준
- 전화번호: /^(\d{3}-\d{3,4}-\d{4}|\d{10,11})$/

// 비밀번호 강도 표시
- 약함 (< 50점)
- 중간 (50-80점)
- 강함 (> 80점)
```

**4. 사용자 경험 개선**
- 리얼타임 필드 검증
- ID 중복확인 버튼 (별도 요청)
- 비밀번호 강도 표시기
- 오류 메시지 실시간 표시
- 성공 메시지 표시 후 자동 로그인 페이지 이동

**5. JavaScript 함수**

```javascript
// 초기화
loadTenants()                      // 테넌트 목록 로드

// ID 검증
checkUserIdDuplicate()             // 중복확인

// 비밀번호 검증
checkPasswordStrength()            // 강도 표시
validatePassword()                 // 형식/일치 확인

// 폼 검증
validateForm()                     // 전체 폼 검증

// 제출
handleRegister(event)              // 회원가입 처리

// 유틸리티
showValidationError()              // 오류 메시지 표시
clearValidationError()             // 오류 제거
showAlert()                        // 알림 표시
```

---

## 🔐 보안 사항

### 1. **비밀번호 보안**

**BCrypt 암호화:**
```
평문 비밀번호: SecurePass@123
BCrypt 해시: $2b$12$VE0E7T5xR7F.oY8wK8K9.eJfG4p8vN8K7mP4dX7xQ2lK8mQ9kR6sL.
```

**특징:**
- 단방향 암호화 (복호화 불가능)
- 매번 다른 salt 사용
- strength = 12 (OWASP 권장)
- Rainbow table 공격 방어

### 2. **개인정보 암호화**

**AES-256-GCM:**
```
email: test@example.com
→ 암호화: GKqP2+8Xm9vZ1a3bC4dE5fG6hI7jK8lM9nO0pQ1rS2tU3vW4xY5zA6bC7dE8fG
          (Base64 인코딩)

암호화 대상:
- email (필수)
- phone (필수)
- address (선택)

암호화 미적용:
- userId (식별자)
- userName (식별자)
- password (BCrypt)
```

### 3. **테넌트 격리**

```
TENANT001 암호화 키 ←→ TENANT001 사용자 데이터
TENANT002 암호화 키 ←→ TENANT002 사용자 데이터

한 테넌트 키로 다른 테넌트 데이터 복호화 불가
```

### 4. **입력 검증**

**서버 측 검증 (Bean Validation):**
```java
@NotBlank
@Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$")
private String userId;

@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])...")
private String password;

@Email
private String email;
```

**클라이언트 측 검증 (JavaScript):**
```javascript
// 폼 제출 전 검증
// 실시간 필드 검증
// 오류 메시지 즉시 표시
```

### 5. **SQL Injection 방지**

**MyBatis 파라미터화 쿼리:**
```xml
<select id="selectUser" resultMap="userResultMap">
    SELECT * FROM tb_user 
    WHERE user_id = #{userId} AND tenant_id = #{tenantId}
</select>
```

---

## 📊 데이터 흐름 예시

### 회원가입 완료 시 데이터 저장

**요청:**
```json
{
  "userId": "john_doe",
  "password": "SecurePass@123",
  "email": "john@example.com",
  "phone": "010-1234-5678",
  "address": "서울시 강남구",
  "tenantId": "TENANT001"
}
```

**처리:**
```
1. 검증 ✓
2. ID 중복 확인: john_doe (사용 가능)
3. BCrypt 암호화: 
   SecurePass@123 → $2b$12$VE0E7T5xR7F.oY8wK8K9...
4. AES-256-GCM 암호화:
   email: john@example.com → GKqP2+8Xm9vZ...
   phone: 010-1234-5678 → aB1cD2eF3gH4...
   address: 서울시 강남구 → xY9zW8vU7tS6...
```

**DB 저장 (tb_user):**
```
user_id: john_doe
tenant_id: TENANT001
user_nm: John Doe
password: $2b$12$VE0E7T5xR7F.oY8wK8K9...
email: GKqP2+8Xm9vZ1a3bC4dE5fG6hI7jK8lM...
phone: aB1cD2eF3gH4iJ5kL6mN7oP8qR9sT0uV1...
address: xY9zW8vU7tS6rQ5pO4nM3lK2jI1hG0fE9...
use_yn: Y
reg_dt: 2025-03-18 10:30:45
```

---

## 🚀 배포 및 사용 방법

### 1. 빌드

```bash
# Maven 빌드
mvn clean compile war:war

# 생성 파일
target/LMS.war
```

### 2. 배포

```bash
# Tomcat에 배포
cp target/LMS.war $CATALINA_HOME/webapps/

# Tomcat 시작
$CATALINA_HOME/bin/startup.sh
```

### 3. 회원가입 페이지 접속

```
URL: http://localhost:8080/LMS/html/register.html
```

### 4. API 테스트

**테넌트 목록 조회:**
```bash
curl http://localhost:8080/LMS/api/v1/tenants
```

**ID 중복 확인:**
```bash
curl -X POST http://localhost:8080/LMS/api/v1/auth/check-userid \
  -H "Content-Type: application/json" \
  -d '{"userId":"john_doe","tenantId":"TENANT001"}'
```

**회원가입:**
```bash
curl -X POST http://localhost:8080/LMS/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "userId":"john_doe",
    "password":"SecurePass@123",
    "passwordConfirm":"SecurePass@123",
    "userName":"John Doe",
    "email":"john@example.com",
    "phone":"010-1234-5678",
    "address":"서울시 강남구",
    "tenantId":"TENANT001"
  }'
```

---

## ✅ 체크리스트

### 배포 전 확인

- [x] DTO 클래스 생성 (RegisterRequestVO, RegisterResponseVO)
- [x] SecurityConfig 설정 (BCryptPasswordEncoder)
- [x] UserService 메서드 추가 (registerUser, isUserIdDuplicate)
- [x] UserServiceImpl 구현
- [x] AuthApiController 작성 (회원가입, ID 중복확인)
- [x] TenantApiController 작성 (테넌트 목록 조회)
- [x] register.html 프론트엔드 페이지 작성
- [ ] 테넌트 조회 API 데이터베이스 연동 (TODO)
- [ ] 로그인 페이지 연동 (TODO)
- [ ] 이메일 인증 추가 (선택사항)
- [ ] SMS 인증 추가 (선택사항)

---

## 📝 향후 개선 사항

### Phase 2 (예정)

1. **이메일 인증**
   - 회원가입 시 이메일 인증 요청
   - 인증 링크 클릭 후 계정 활성화

2. **로그인 통합**
   - 회원가입 후 로그인 페이지 자동 이동
   - JWT 토큰 기반 인증

3. **사회 로그인**
   - Google OAuth2
   - 카카오 로그인
   - Naver 로그인

4. **고급 검증**
   - 약한 비밀번호 방지
   - 동일 보안 질문

5. **모니터링**
   - 회원가입 통계
   - 실패 분석
   - 이상 탐지

---

## 📞 문제 해결

### Q1: "비밀번호 형식이 맞지 않는다"고 나옴

**원인**: 필수 문자 종류 누락

**해결**:
```
필수: 영문 소문자(a-z) + 영문 대문자(A-Z) + 숫자(0-9) + 특수문자(@$!%*?&)

❌ MyPass123           (특수문자 없음)
❌ MyPass@             (숫자 없음)
❌ pass@123           (대문자 없음)
✅ MyPass@123         (모두 포함)
```

### Q2: "ID가 이미 사용 중입니다" (다른 회사에서도)

**원인**: 같은 테넌트가 아님

**해결**: 올바른 테넌트를 선택했는지 확인

**구조:**
```
TENANT001 하에서만: john_doe 사용 불가 (이미 있음)
TENANT002 하에서는: john_doe 사용 가능 (다른 테넌트)
```

### Q3: 개인정보가 평문으로 저장됨

**원인**: AES-256-GCM 암호화 미적용

**해결**: 확인 사항:
1. EncryptionService 빈이 주입되었는가?
2. tenantId가 올바르게 설정되었는가?
3. 암호화 키가 생성되었는가?

```sql
-- 확인 쿼리
SELECT email, phone, address FROM tb_user WHERE user_id = 'john_doe';
-- 결과: Base64 인코딩된 암호화 데이터여야 함
```

---

## 📖 참고 자료

**비밀번호 정책:**
- OWASP: https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html
- NIST SP 800-63B: https://pages.nist.gov/800-63-3/sp800-63b.html

**암호화:**
- NIST FIPS 197 (AES): https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.197.pdf
- NIST SP 800-38D (GCM): https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38d.pdf

**Spring Security:**
- BCryptPasswordEncoder: https://spring.io/blog/2013/11/21/spring-security-3-2-0-rc1-released

---

**작성**: Claude Code (Sisyphus)  
**버전**: 1.0  
**라이센스**: Apache 2.0
