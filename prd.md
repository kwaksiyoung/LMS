# LMS (Learning Management System) PRD
## Product Requirements Document v1.0

**작성일:** 2026-03-18
**프레임워크:** 전자정부프레임워크 4.2.0
**데이터베이스:** MariaDB
**배포 방식:** WAR

---

## 1. 프로젝트 개요

온라인/오프라인 통합 학습 관리 시스템(LMS)으로, 관리자는 과정을 개설하고 수강생을 관리하며, 학습자는 과정을 검색·수강신청·학습할 수 있는 플랫폼입니다.

---

## 2. 기술 스택

| 항목 | 기술 |
|------|------|
| 백엔드 프레임워크 | 전자정부프레임워크 4.2.0 (Spring Framework 5.3.x) |
| 프론트엔드 (현재) | JSP + jQuery |
| 프론트엔드 (추후) | React / Next.js (API 분리 구조로 전환 가능) |
| 데이터베이스 | MariaDB |
| ORM | MyBatis |
| 세션 클러스터링 | Redis (Spring Session) |
| 빌드/배포 | Maven, WAR |
| API 구조 | REST API (`/api/v1/**`) + JSP MVC 병렬 운영 |
| 인증 | 세션 기반 (JSP) + JWT 토큰 (REST API) |
| 개인정보 암호화 | 비밀번호: BCrypt (단방향), 이메일/연락처/주소: AES-256-GCM (양방향) |

---

## 3. 공통 기능 요구사항

### 3.1 개인정보 암호화

| 항목 | 암호화 방식 | 비고 |
|------|------------|------|
| 비밀번호 | BCrypt (단방향) | strength=12 |
| 이메일 | AES-256-GCM (양방향) | KISA 권장 표준 |
| 연락처 | AES-256-GCM (양방향) | |
| 주소 | AES-256-GCM (양방향) | |
| 암호화 키 관리 | DB 저장 (테넌트별 독립 키) | 수동 로테이션 정책 |

### 3.2 세션 관리 (WAS 다중화 지원)

- **Redis 기반 세션 클러스터링** (Spring Session Data Redis)
- WAS 서버별 별도 설정 없이 세션 공유
- 세션 만료 시간: 30분
- 개발 환경: Redis 미연결 시 로컬 세션 사용 (주석 처리로 전환)

### 3.3 반응형 웹

- 모바일/태블릿/데스크톱 대응
- CSS Grid / Flexbox 기반 레이아웃
- 최소 지원 해상도: 320px (모바일)

### 3.4 프론트엔드/백엔드 분리 구조

```
현재 구조:
  JSP + jQuery → Spring MVC Controller → Service → Mapper → MariaDB

REST API (병렬 운영):
  클라이언트 → /api/v1/** → RestController → Service → Mapper → MariaDB

추후 전환:
  React/Next.js → REST API → Service → Mapper → MariaDB
```

- REST API: `/api/v1/**` 경로로 JSON 응답
- 공통 응답 포맷: `ApiResponse<T>` (success, data, message, timestamp)
- CORS 설정: 허용 오리진 지정
- JSP 화면과 REST API 병렬 운영 → 추후 JSP 제거

### 3.4.1 컨트롤러 구현 규칙 (개발 필수 규칙)

모든 컨트롤러 작업 시 **JSP MVC Controller와 REST API Controller를 항상 함께 구현**한다.

| 구분 | 클래스명 예시 | URL 패턴 | 응답 형식 |
|------|-------------|----------|----------|
| JSP MVC Controller | `CourseController` | `/course/**` | ModelAndView (JSP 렌더링) |
| REST API Controller | `CourseApiController` | `/api/v1/courses/**` | `ApiResponse<T>` (JSON) |

- 동일한 Service 레이어(`CourseService`)를 공유하여 비즈니스 로직 중복 방지
- JSP Controller는 화면 렌더링 담당, REST Controller는 데이터 처리 담당
- 추후 JSP 제거 시 REST API Controller만 유지하는 구조로 자연스럽게 전환 가능

```
예시 구조:
  CourseController.java        → /course/list, /course/detail/{id}, ...
  CourseApiController.java     → /api/v1/courses, /api/v1/courses/{id}, ...
  CourseService.java           → 공통 비즈니스 로직 (두 컨트롤러가 공유)
```

### 3.5 파일 업로드

- **드래그 앤 드롭** 지원 (HTML5 File API)
- 지원 형식: 동영상(mp4, avi, mov, wmv), 문서(pdf, doc, docx, ppt, pptx), 이미지(jpg, png, gif)
- 최대 업로드 크기: 100MB
- 저장 방식: 내부 파일 시스템 (추후 외부 스토리지 연계 가능하도록 추상화 인터페이스 설계)
- 파일 저장 경로: `/data/uploads/{tenantId}/{year}/{month}/`
- 파일명: UUID 기반으로 변경 저장 (원본 파일명 별도 보관)

### 3.6 웹 보안 (행정안전부 / KISA 시큐어코딩 준수)

| 보안 항목 | 대응 방안 |
|----------|----------|
| SQL 인젝션 | MyBatis `#{}` 사용 (PreparedStatement), `${}` 사용 금지 |
| XSS (크로스사이트 스크립팅) | Lucy-XSS 필터 적용, 출력 시 HTML 인코딩 |
| CSRF | CSRF 토큰 적용 (폼 요청), SameSite 쿠키 설정 |
| 취약한 인증 | 로그인 실패 횟수 제한 (5회 초과 시 30분 잠금) |
| 민감정보 노출 | 응답에서 password 필드 제거, HTTPS 적용 |
| 파일 업로드 취약점 | 확장자 화이트리스트, MIME 타입 검증, 저장 경로 외부 노출 방지 |
| 세션 고정 | 로그인 성공 시 새 세션 발급 |
| 컴포넌트 취약점 | 의존성 정기 업데이트, CVE 점검 |
| 에러 처리 | 상세 오류 메시지 사용자에게 노출 금지 |
| 접근 제어 | URL 기반 권한 체크 (역할 기반 접근 제어) |

---

## 4. 권한 관리 설계 (RBAC - Role Based Access Control)

### 4.1 설계 개요

일반적인 권한 관리는 **역할(Role) + 권한(Permission) + 메뉴** 3단계 구조로 설계합니다.

```
사용자(User) → 역할(Role) → 권한(Permission) → 메뉴/기능(Menu/Resource)
```

### 4.2 역할 구조

| 역할 코드 | 역할명 | 설명 |
|----------|--------|------|
| `ROLE_SUPER_ADMIN` | 슈퍼 관리자 | 전체 시스템 관리 (테넌트 관리 포함) |
| `ROLE_ADMIN` | 관리자 | 테넌트 내 전체 관리 (과정, 수강생, 결제 등) |
| `ROLE_INSTRUCTOR` | 강사 | 담당 과정 관리, 수강생 진도 확인 |
| `ROLE_USER` | 일반 학습자 | 과정 수강신청, 학습, 마이페이지 |

### 4.3 권한 구조

```
메뉴 기반 권한 체크:
  - 메뉴(tb_menu) ↔ 역할(tb_role) 매핑 테이블로 접근 제어
  - URL 패턴 기반으로 요청 시 권한 검사

기능 기반 권한 체크:
  - 읽기(READ), 쓰기(WRITE), 수정(UPDATE), 삭제(DELETE) 단위 권한
  - 예: 강사는 본인 과정만 수정 가능
```

### 4.4 DB 테이블 설계 (권한 관련)

```sql
-- 역할
tb_role (role_cd, role_nm, role_desc, use_yn)

-- 사용자-역할 매핑 (다대다)
tb_user_role (user_id, tenant_id, role_cd, reg_dt)

-- 메뉴
tb_menu (menu_id, tenant_id, menu_nm, menu_url, parent_menu_id,
         sort_order, icon, use_yn, reg_dt)

-- 메뉴-역할 매핑 (메뉴별 접근 가능 역할)
tb_menu_role (menu_id, role_cd)

-- 권한
tb_permission (permission_id, permission_nm, resource_url,
               http_method, use_yn)

-- 역할-권한 매핑
tb_role_permission (role_cd, permission_id)
```

### 4.5 접근 제어 흐름

```
HTTP 요청
    ↓
AuthorizationInterceptor (URL 패턴 기반 권한 체크)
    ↓
세션에서 사용자 역할 조회
    ↓
해당 URL에 필요한 역할과 비교
    ↓
허용 → 컨트롤러 진입
거부 → 403 또는 로그인 페이지 리다이렉트
```

---

## 5. 관리자 기능 요구사항

### 5.1 메뉴 관리

- 메뉴 등록 / 수정 / 삭제
- 상위/하위 메뉴 계층 구조 (최대 3단계)
- 메뉴 순서 변경 (드래그 앤 드롭)
- 역할별 메뉴 노출 설정

### 5.2 콘텐츠 관리

| 항목 | 내용 |
|------|------|
| 동영상 업로드 | 드래그 앤 드롭, 내부 저장소 저장 |
| 외부 연계 구조 | FileStorageService 인터페이스 추상화 (추후 S3, CDN 등 교체 가능) |
| 메타데이터 | 제목, 설명, 재생시간, 파일크기, 형식 |
| 미리보기 | 업로드 완료 후 동영상 플레이어 미리보기 |

### 5.3 강의 관리

- 강의 등록 시 업로드된 동영상 콘텐츠 지정
- **차시(강의 회차) 구성**
  - 차시 유형: **필수** (수료 필수 조건) / **선택**
  - 차시별 순서 설정
- **차시별 퀴즈 생성**
  - 문제 유형: 객관식, OX, 주관식
  - 합격 기준 점수 설정
  - 재시도 횟수 제한 설정

### 5.4 과정 개설

#### 기본 정보
| 항목 | 내용 |
|------|------|
| 과정명 | 필수 |
| 과정 소개 | 텍스트 에디터 (Rich Text) |
| 썸네일 | 이미지 업로드 |
| 강사 | 강사 역할 사용자 선택 |
| 가격 | 유료/무료 설정 |
| 과정 유형 | 온라인 / 오프라인 / 혼합(온+오프) / Zoom(비대면) |

#### 수강 기간
| 항목 | 내용 |
|------|------|
| 수강신청 기간 | 시작일 ~ 종료일 |
| 수강 기간 | 시작일 ~ 종료일 |

#### 강의 구성
- 등록된 강의(차시) 선택하여 과정에 추가
- 차시 순서 변경

#### 수료 요건 설정
| 요건 항목 | 설명 |
|----------|------|
| 진도율 | 전체 필수 차시 대비 수강 완료 비율 (예: 80% 이상) |
| 퀴즈 | 퀴즈 합격 여부 (필수 차시 퀴즈 모두 통과) |
| 설문조사 | 만족도 설문 제출 여부 |

#### 설문조사 (만족도)
- 과정에 만족도 설문 연결
- 문항 구성: 5점 척도, 객관식, 주관식
- 수료 요건에 설문 응답 포함 여부 설정

#### 오프라인/혼합 과정 추가 설정
| 항목 | 내용 |
|------|------|
| 강의실 주소 | 오프라인 강의 장소 |
| 강의 시간 | 날짜별 강의 시작/종료 시간 |
| 출석 QR/바코드 | 차시별 출석 체크용 QR코드 또는 바코드 생성 |
| 출석 처리 | QR/바코드 스캔으로 출석 자동 기록 |

### 5.5 수강생 관리

- 과정별 수강생 목록 조회
- 수강생별 진도율 확인
- 수동 수료 처리 (관리자 강제 수료)
- 수강 취소 처리
- 수강생 엑셀 다운로드

### 5.6 결제 관리

| 항목 | 내용 |
|------|------|
| 매출 현황 | 결제 완료 / 취소 / 대기 목록 |
| 기간별 매출 조회 | 일/월/년 단위 매출 집계 |
| 결제 상세 | 결제 수단, 금액, 일시, 결제자 |
| 환불 처리 | PG 연동 취소 요청 |
| 엑셀 다운로드 | 매출 데이터 내보내기 |

---

## 6. 학습자 기능 요구사항

### 6.1 회원가입

- 아이디 중복 확인
- 비밀번호 정책: 10자 이상, 대/소문자 + 숫자 + 특수문자 포함
- 이메일 인증 (선택)
- 약관 동의

### 6.2 로그인

- 아이디/비밀번호 로그인
- 로그인 실패 5회 시 30분 계정 잠금
- 자동 로그인 (Remember Me, 선택)

### 6.3 과정 검색 및 목록

- 과정명 키워드 검색
- 카테고리/유형별 필터링
- 가격(무료/유료) 필터
- 정렬: 최신순, 인기순, 낮은가격순
- 과정 상세 페이지: 소개, 강의 구성, 강사 정보, 수강 후기, 수료 요건

### 6.4 수강신청

- 무료 과정: 즉시 수강신청
- 유료 과정: PG 모듈 연계 결제
  - PG사 연동: KG이니시스 또는 NHN KCP (인터페이스 추상화로 교체 가능)
  - 결제 수단: 신용카드, 계좌이체, 카카오페이, 네이버페이
  - 결제 완료 후 자동 수강 등록

### 6.5 학습 (동영상 플레이어)

| 항목 | 내용 |
|------|------|
| 플레이어 | HTML5 Video Player (video.js 또는 플럭스플레이어) |
| 진도 기록 | 시청 시간(초) 단위로 DB 저장 |
| 진도율 계산 | (시청완료 시간 / 전체 동영상 시간) × 100 |
| 이어보기 | 마지막 시청 위치 저장 및 재생 |
| 배속 조절 | 0.5x ~ 2.0x |
| 자막 | WebVTT 형식 지원 (선택) |
| 챕터 | 동영상 내 챕터 마크 표시 (선택) |

### 6.6 퀴즈

- 차시 시청 완료 후 퀴즈 활성화
- 합격 기준 점수 미달 시 재시도 (제한 횟수 내)
- 퀴즈 결과 저장

### 6.7 설문조사

- 수료 요건 충족 시 만족도 설문 노출
- 설문 제출 후 수료 처리

### 6.8 수료 자동 처리

```
수료 요건 체크 트리거:
  - 진도 업데이트 시
  - 퀴즈 완료 시
  - 설문 제출 시

수료 조건 모두 충족 → 자동 수료 처리
  - tb_enrollment.enrollment_status = 'COMPLETE'
  - 수료일 기록
  - 수료증 발급 (선택)
```

### 6.9 마이페이지

- 수강 중인 과정 목록 (진도율 표시)
- 수료 완료 과정 목록
- 결제 내역
- 개인정보 수정
- 비밀번호 변경
- 수료증 출력

---

## 7. 데이터베이스 테이블 설계 (주요)

```sql
-- 테넌트
tb_tenant (tenant_id, tenant_nm, subscription_status, max_users)

-- 사용자
tb_user (user_id, tenant_id, user_nm, password, email, phone,
         address, dept_cd, use_yn, login_fail_cnt, lock_yn,
         lock_dt, reg_dt, upd_dt)

-- 역할
tb_role (role_cd, role_nm, role_desc, use_yn)

-- 사용자-역할 매핑
tb_user_role (user_id, tenant_id, role_cd, reg_dt)

-- 메뉴
tb_menu (menu_id, tenant_id, menu_nm, menu_url, parent_menu_id,
         sort_order, icon, use_yn, reg_dt)

-- 메뉴-역할 매핑
tb_menu_role (menu_id, role_cd)

-- 콘텐츠 (동영상 파일)
tb_content (content_id, tenant_id, content_title, content_type,
            file_path, original_file_nm, file_size,
            duration_seconds, use_yn, reg_dt)

-- 강의 (차시)
tb_lecture (lecture_id, tenant_id, lecture_nm, content_id,
            lecture_order, lecture_type, -- REQUIRED/OPTIONAL
            use_yn, reg_dt)

-- 퀴즈
tb_quiz (quiz_id, lecture_id, quiz_title, pass_score,
         max_retry_cnt, use_yn, reg_dt)

-- 퀴즈 문항
tb_quiz_question (question_id, quiz_id, question_nm,
                  question_type, -- MULTIPLE/OX/SHORT
                  sort_order)

-- 퀴즈 보기
tb_quiz_option (option_id, question_id, option_nm, is_correct, sort_order)

-- 과정
tb_course (course_id, tenant_id, course_nm, course_desc,
           thumbnail_path, instructor_id, price,
           course_type, -- ONLINE/OFFLINE/BLENDED/ZOOM
           apply_start_dt, apply_end_dt,
           start_dt, end_dt, max_students,
           completion_progress_rate, -- 수료 필요 진도율
           completion_quiz_yn,       -- 퀴즈 수료 요건
           completion_survey_yn,     -- 설문 수료 요건
           status, use_yn, reg_dt)

-- 과정-강의 매핑
tb_course_lecture (course_id, lecture_id, sort_order)

-- 오프라인 강의 정보
tb_offline_schedule (schedule_id, course_id, location_address,
                     lecture_dt, start_time, end_time,
                     qr_code, barcode)

-- 설문조사
tb_survey (survey_id, tenant_id, survey_nm, use_yn, reg_dt)

-- 설문 문항
tb_survey_question (question_id, survey_id, question_nm,
                    question_type, sort_order)

-- 수강 신청
tb_enrollment (enrollment_id, user_id, course_id, tenant_id,
               enrollment_status, -- ENROLL/COMPLETE/CANCEL
               completion_rate, enrollment_dt, completion_dt,
               reg_dt, upd_dt)

-- 학습 진도
tb_learning_progress (progress_id, enrollment_id, lecture_id,
                      watch_seconds, total_seconds,
                      progress_rate, last_position_seconds,
                      complete_yn, complete_dt, reg_dt, upd_dt)

-- 퀴즈 응시 결과
tb_quiz_result (result_id, enrollment_id, quiz_id,
                score, pass_yn, attempt_cnt, reg_dt)

-- 설문 응답
tb_survey_answer (answer_id, enrollment_id, survey_id,
                  question_id, answer_val, reg_dt)

-- 결제
tb_payment (payment_id, user_id, course_id, tenant_id,
            amount, payment_status, -- PAID/CANCEL/PENDING
            payment_method, pg_transaction_id,
            paid_dt, cancel_dt, reg_dt)

-- 암호화 키
tb_encryption_key (key_id, tenant_id, key_value, key_status,
                   created_dt, expired_dt)
```

---

## 8. API 설계 (REST API 엔드포인트)

### 인증
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/v1/auth/login | 로그인 (JWT 발급) | 불필요 |
| POST | /api/v1/auth/logout | 로그아웃 | 필요 |
| GET | /api/v1/auth/me | 현재 사용자 정보 | 필요 |
| POST | /api/v1/auth/register | 회원가입 | 불필요 |

### 과정
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/v1/courses | 과정 목록 | 불필요 |
| GET | /api/v1/courses/{courseId} | 과정 상세 | 불필요 |
| POST | /api/v1/courses | 과정 등록 | 관리자 |
| PUT | /api/v1/courses/{courseId} | 과정 수정 | 관리자 |
| DELETE | /api/v1/courses/{courseId} | 과정 삭제 | 관리자 |

### 수강
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| GET | /api/v1/enrollments | 내 수강 목록 | 필요 |
| POST | /api/v1/enrollments | 수강 신청 | 필요 |
| DELETE | /api/v1/enrollments/{id} | 수강 취소 | 필요 |

### 학습 진도
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/v1/progress | 진도 업데이트 | 필요 |
| GET | /api/v1/progress/{enrollmentId} | 진도 조회 | 필요 |

### 결제
| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/v1/payments | 결제 요청 | 필요 |
| POST | /api/v1/payments/{id}/cancel | 결제 취소 | 필요 |
| GET | /api/v1/payments | 결제 내역 | 필요 |

---

## 9. 비기능 요구사항

| 항목 | 요구사항 |
|------|----------|
| 성능 | 페이지 로딩 3초 이내, 동영상 스트리밍 지연 2초 이내 |
| 가용성 | WAS 다중화 (Redis 세션 클러스터링) |
| 확장성 | 파일 저장소 추상화 인터페이스 (내부 → 외부 스토리지 전환 가능) |
| 보안 | KISA 시큐어코딩 가이드 준수, 개인정보보호법 준수 |
| 유지보수 | JSP → React/Next.js 전환 가능한 REST API 설계 |

---

## 10. 개발 우선순위 (Phase)

### Phase 1 - 기반 구조 (현재)
- [x] 프로젝트 기본 구조 (eGovFrame 4.2.0)
- [x] 사용자 인증 (로그인/로그아웃/회원가입)
- [x] 개인정보 암호화 (BCrypt, AES-256-GCM)
- [x] REST API 계층 추가 (JWT 인증)
- [x] Redis 세션 클러스터링 설정
- [ ] 권한 관리 (RBAC) 구현
- [ ] 메뉴 관리

### Phase 2 - 핵심 기능
- [ ] 콘텐츠 관리 (동영상 업로드)
- [ ] 강의/차시 관리 (퀴즈 포함)
- [ ] 과정 개설 (수료 요건 설정)
- [ ] 수강신청 (무료)
- [ ] 동영상 플레이어 + 진도 기록
- [ ] 수료 자동 처리

### Phase 3 - 부가 기능
- [ ] PG 결제 모듈 연동
- [ ] 오프라인 출석 (QR/바코드)
- [ ] 설문조사 (만족도)
- [ ] 결제 관리 대시보드
- [ ] 수료증 발급

### Phase 4 - 고도화
- [ ] React/Next.js 프론트엔드 전환
- [ ] 외부 스토리지 연계 (S3 등)
- [ ] 실시간 알림 (WebSocket)
- [ ] 통계/리포트 대시보드
