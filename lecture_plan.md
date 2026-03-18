# 강의 관리 메뉴 구현 계획

## Context
prd.md 5.3절 "강의 관리" 요구사항에 따라 관리자용 강의 CRUD(목록·등록·수정·삭제) 기능을 구현한다.
현재 tb_lecture 테이블 DDL과 매핑 테이블(Mapper/VO)은 존재하지만, 실제 CRUD 레이어가 전혀 없는 상태다.
PRD 규칙에 따라 JSP MVC Controller + REST API Controller 쌍으로 구현하며, JSP는 외부 CSS 파일을 참조한다.

---

## 사전 작업: DB 스키마 보완 (ALTER TABLE)

현재 tb_lecture에 누락된 PRD 컬럼 3개를 추가한다.

**파일:** `src/main/resources/egovframework/sqlmap/saas-schema-final.sql`

```sql
-- tb_lecture 누락 컬럼 추가
ALTER TABLE tb_lecture
    ADD COLUMN lecture_order  INT         DEFAULT 0          COMMENT '차시 순서'
                              AFTER lecture_desc,
    ADD COLUMN lecture_type   VARCHAR(20) DEFAULT 'REQUIRED' COMMENT '차시 유형(REQUIRED/OPTIONAL)'
                              AFTER lecture_order,
    ADD COLUMN content_id     VARCHAR(50) DEFAULT NULL       COMMENT '연결 콘텐츠 ID (tb_content 참조)'
                              AFTER lecture_type;

-- content_id FK (ON DELETE RESTRICT: 멀티테넌시 복합 FK 안전 처리)
ALTER TABLE tb_lecture
    ADD CONSTRAINT fk_lecture_content
        FOREIGN KEY (content_id, tenant_id)
        REFERENCES tb_content(content_id, tenant_id)
        ON DELETE RESTRICT ON UPDATE CASCADE;

-- 인덱스 추가
ALTER TABLE tb_lecture
    ADD INDEX idx_tenant_use_yn   (tenant_id, use_yn),
    ADD INDEX idx_tenant_type_use (tenant_id, lecture_type, use_yn),
    ADD INDEX idx_content_tenant  (content_id, tenant_id),
    ADD INDEX idx_tenant_order    (tenant_id, lecture_order);
```

---

## 생성할 파일 목록 (11개)

| # | 파일 경로 | 설명 |
|---|----------|------|
| 1 | `kr/co/lms/vo/LectureVO.java` | tb_lecture 매핑 VO |
| 2 | `kr/co/lms/mapper/LectureMapper.java` | MyBatis @Mapper |
| 3 | `egovframework/sqlmap/lecture-mapper.xml` | SQL Mapper XML |
| 4 | `kr/co/lms/service/LectureService.java` | 서비스 인터페이스 |
| 5 | `kr/co/lms/service/impl/LectureServiceImpl.java` | 서비스 구현체 |
| 6 | `kr/co/lms/web/controller/LectureController.java` | JSP MVC Controller |
| 7 | `kr/co/lms/web/api/v1/LectureApiController.java` | REST API Controller |
| 8 | `css/lecture.css` | 강의 페이지 CSS |
| 9 | `WEB-INF/jsp/lecture/list.jsp` | 강의 목록 |
| 10 | `WEB-INF/jsp/lecture/create.jsp` | 강의 등록 |
| 11 | `WEB-INF/jsp/lecture/edit.jsp` | 강의 수정 |

---

## 구현 상세

### 1. LectureVO (필드 구성)
- DB 컬럼: `lectureId`, `tenantId`, `contentId`, `lectureNm`, `lectureDesc`, `lectureOrder`, `lectureType`, `durationMinutes`, `useYn`, `regDt`, `updDt`
- 검색용 필드: `lectureNmKeyword` (LIKE 쿼리)
- 조인 필드: `contentCount` (tb_lecture_content COUNT)

### 2. LectureMapper 메서드
- `selectLecture(lectureId, tenantId)` — 단건 조회
- `selectLectureList(LectureVO)` — 목록 (동적 SQL)
- `selectLectureListWithContentCount(LectureVO)` — 콘텐츠 수 포함 목록 (LEFT JOIN)
- `insertLecture(LectureVO)` — 등록
- `updateLecture(LectureVO)` — 수정
- `deleteLecture(LectureVO)` — 논리 삭제 (use_yn='N')
- `selectLectureCount(LectureVO)` — 건수

### 3. lecture-mapper.xml 핵심
- `content-mapper.xml` 패턴 동일 적용
- `<delete>` 태그 내부에 UPDATE 문 (use_yn='N') — 기존 관례 준수
- `selectLectureListWithContentCount`: tb_lecture_content LEFT JOIN GROUP BY

### 4. LectureService/LectureServiceImpl
- `ContentServiceImpl` 패턴 동일 적용
- `@Service`, `@Transactional`, `@RequiredArgsConstructor`
- 조회 메서드: `@Transactional(readOnly = true)`

### 5. LectureController (JSP MVC)
재사용: `ContentController` 패턴 + `AuthorizationUtil`

| HTTP | URL | 권한 | 설명 |
|------|-----|------|------|
| GET | `/lecture/list` | 전체 | 목록 + contentCount |
| GET | `/lecture/create` | ADMIN | 등록 폼 |
| POST | `/lecture` | ADMIN | 등록 처리 |
| GET | `/lecture/{lectureId}/edit` | ADMIN | 수정 폼 |
| PUT | `/lecture/{lectureId}` | ADMIN | 수정 처리 |
| DELETE | `/lecture/{lectureId}` | ADMIN | 논리 삭제 |

- tenantId: `authorizationUtil.getTenantId(session)` 강제 주입
- 비관리자 접근 → `redirect:/`

### 6. LectureApiController (REST API)
재사용: `ContentApiController` 패턴 + `ApiAuthUtil`

| HTTP | URL | 권한 |
|------|-----|------|
| GET | `/api/v1/lectures` | ADMIN |
| GET | `/api/v1/lectures/{lectureId}` | 공개 |
| POST | `/api/v1/lectures` | ADMIN |
| PUT | `/api/v1/lectures/{lectureId}` | ADMIN |
| DELETE | `/api/v1/lectures/{lectureId}` | ADMIN |

- tenantId: `ApiAuthUtil.getCurrentTenantId(request)` 강제 주입

### 7. lecture.css
- `content.css` 구조 기반
- 추가 클래스: `.badge-active`, `.badge-inactive`, `.search-bar`, `.lecture-title`

### 8. JSP 파일 공통 규칙
- 헤더: `common.css` + `lecture.css` 링크
- 인라인 `<style>` 금지
- 권한 체크: `session.getAttribute("isAdmin")`
- PUT/DELETE: `<input type="hidden" name="_method" value="PUT/DELETE">`

### 9. list.jsp 테이블 컬럼
강의ID | 강의명 | 콘텐츠 수 | 차시유형 | 시간(분) | 사용여부 | 등록일 | 관리(수정/삭제)

---

## 재사용 유틸리티 (기존 코드 활용)

| 재사용 대상 | 경로 |
|------------|------|
| AuthorizationUtil | `kr/co/lms/util/AuthorizationUtil.java` |
| ApiAuthUtil | `kr/co/lms/web/api/common/ApiAuthUtil.java` |
| ApiResponse | `kr/co/lms/web/api/common/ApiResponse.java` |
| ContentService | `kr/co/lms/service/ContentService.java` (드롭다운용) |

---

## 검증 방법

1. 서버 기동 후 `/lecture/list` 접근 → 목록 화면 노출
2. 관리자 로그인 후 `/lecture/create` → 등록 폼 정상 표시
3. 강의 등록 후 목록에 표시되는지 확인
4. 수정 폼에서 기존 데이터 바인딩 확인
5. 삭제 후 목록에서 제거 (use_yn='N') 확인
6. 비관리자 계정으로 `/lecture/create` 접근 → `redirect:/` 동작 확인
7. REST API: `POST /api/v1/lectures` 등록 후 `GET /api/v1/lectures` 조회 확인
8. REST API: JWT 없이 `/api/v1/lectures` 요청 → 403 응답 확인
