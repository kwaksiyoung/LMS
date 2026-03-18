-- ============================================================================
-- LMS SaaS 멀티테넌시 초기 데이터 로드 (최종 - M:N 구조로 완벽 수정)
-- 콘텐츠(독립) → 강의(독립) → 과정(독립) → 강의-콘텐츠 매핑 → 과정-강의 매핑
-- ============================================================================

-- ============================================================================
-- 1. 고객사(테넌트) 초기 데이터
-- ============================================================================
INSERT INTO tb_tenant (tenant_id, tenant_nm, tenant_desc, contact_name, contact_email, contact_phone, 
                        subscription_status, max_users, current_users, subscription_start_dt, subscription_end_dt) 
VALUES 
('TENANT001', 'ABC 컨설팅', 'ABC 컨설팅 회사 LMS', 'Kim Manager', 'kim@abc.com', '02-1234-5678',
 'ACTIVE', 100, 0, '2024-01-01', '2024-12-31'),
('TENANT002', 'XYZ 교육원', 'XYZ 교육원 온라인 LMS', 'Lee Director', 'lee@xyz.com', '02-9876-5432',
 'ACTIVE', 150, 0, '2024-03-01', '2024-12-31');

-- ============================================================================
-- 2. 사용자 초기 데이터
-- ============================================================================
-- ⚠️ 주의: tb_user INSERT는 제거됨!
-- 이유: UserInitializer (@PostConstruct)에서 자동으로 사용자 생성됨
-- 흐름:
-- 1. UserInitializer.initializeUsers() 실행
-- 2. UserVO 객체 생성 (평문 email, phone, address 포함)
-- 3. UserService.insertUser() 호출
-- 4. UserServiceImpl.insertUser()에서 자동 암호화
-- 5. 암호화된 데이터 DB 저장
--
-- 따라서 여기서 평문 데이터를 INSERT하면 안 됨!
-- (조회할 때 복호화 실패 → GCM 태그 검증 실패)
--
-- 만약 수동으로 테스트 데이터를 추가하고 싶다면:
-- INSERT INTO tb_user를 여기 아래에 작성하되,
-- email, phone, address 값을 암호화된 값으로 교체할 것!
-- (현재는 UserInitializer가 모든 초기 데이터 생성하므로 불필요)

-- ============================================================================
-- 3. 역할(Role) 초기 데이터
-- ============================================================================

-- TENANT001 역할들
INSERT INTO tb_role (role_cd, tenant_id, role_nm, role_desc, use_yn) 
VALUES 
('ROLE_ADMIN', 'TENANT001', '관리자', '시스템 전체 관리자', 'Y'),
('ROLE_MANAGER', 'TENANT001', '담당자', '과정 및 사용자 관리자', 'Y'),
('ROLE_INSTRUCTOR', 'TENANT001', '강사', '강의 담당 강사', 'Y'),
('ROLE_STUDENT', 'TENANT001', '학생', '과정 수강 학생', 'Y');

-- TENANT002 역할들
INSERT INTO tb_role (role_cd, tenant_id, role_nm, role_desc, use_yn) 
VALUES 
('ROLE_ADMIN', 'TENANT002', '시스템관리자', '전체 시스템 관리', 'Y'),
('ROLE_MANAGER', 'TENANT002', '과정관리자', '과정 및 콘텐츠 관리', 'Y'),
('ROLE_INSTRUCTOR', 'TENANT002', '교강사', '온라인 강의 진행', 'Y'),
('ROLE_STUDENT', 'TENANT002', '수강자', '온라인 과정 수강', 'Y');

-- ============================================================================
-- 4. 권한(Permission) 초기 데이터
-- ============================================================================

-- TENANT001 권한들
INSERT INTO tb_permission (perm_cd, tenant_id, perm_nm, perm_desc, resource_url, http_method, use_yn) 
VALUES 
('PERM_USER_CREATE', 'TENANT001', '사용자 등록', '사용자 등록 권한', '/user', 'POST', 'Y'),
('PERM_USER_READ', 'TENANT001', '사용자 조회', '사용자 조회 권한', '/user', 'GET', 'Y'),
('PERM_USER_UPDATE', 'TENANT001', '사용자 수정', '사용자 정보 수정 권한', '/user', 'PUT', 'Y'),
('PERM_USER_DELETE', 'TENANT001', '사용자 삭제', '사용자 삭제 권한', '/user', 'DELETE', 'Y'),
('PERM_COURSE_CREATE', 'TENANT001', '과정 등록', '과정 등록 권한', '/course', 'POST', 'Y'),
('PERM_COURSE_READ', 'TENANT001', '과정 조회', '과정 조회 권한', '/course', 'GET', 'Y'),
('PERM_COURSE_UPDATE', 'TENANT001', '과정 수정', '과정 정보 수정 권한', '/course', 'PUT', 'Y'),
('PERM_COURSE_DELETE', 'TENANT001', '과정 삭제', '과정 삭제 권한', '/course', 'DELETE', 'Y'),
('PERM_ENROLLMENT_READ', 'TENANT001', '수강 조회', '수강 현황 조회 권한', '/enrollment', 'GET', 'Y'),
('PERM_ENROLLMENT_UPDATE', 'TENANT001', '수강 수정', '수강 정보 수정 권한', '/enrollment', 'PUT', 'Y'),
('PERM_ROLE_ADMIN', 'TENANT001', '역할 관리', '역할 관리 권한', '/role', 'POST', 'Y'),
('PERM_REPORT_VIEW', 'TENANT001', '보고서 조회', '학습 현황 보고서 조회', '/report', 'GET', 'Y');

-- TENANT002 권한들
INSERT INTO tb_permission (perm_cd, tenant_id, perm_nm, perm_desc, resource_url, http_method, use_yn) 
VALUES 
('PERM_USER_CREATE', 'TENANT002', '사용자 등록', '사용자 신규 가입', '/user', 'POST', 'Y'),
('PERM_USER_READ', 'TENANT002', '사용자 조회', '사용자 프로필 조회', '/user', 'GET', 'Y'),
('PERM_USER_UPDATE', 'TENANT002', '사용자 수정', '프로필 수정', '/user', 'PUT', 'Y'),
('PERM_USER_DELETE', 'TENANT002', '사용자 삭제', '계정 삭제', '/user', 'DELETE', 'Y'),
('PERM_COURSE_CREATE', 'TENANT002', '강의 등록', '신규 강의 개설', '/course', 'POST', 'Y'),
('PERM_COURSE_READ', 'TENANT002', '강의 조회', '강의 목록 조회', '/course', 'GET', 'Y'),
('PERM_COURSE_UPDATE', 'TENANT002', '강의 수정', '강의 정보 변경', '/course', 'PUT', 'Y'),
('PERM_COURSE_DELETE', 'TENANT002', '강의 삭제', '강의 폐강', '/course', 'DELETE', 'Y'),
('PERM_ENROLLMENT_READ', 'TENANT002', '수강신청 조회', '수강신청 현황 확인', '/enrollment', 'GET', 'Y'),
('PERM_ENROLLMENT_UPDATE', 'TENANT002', '수강신청 수정', '수강신청 정보 수정', '/enrollment', 'PUT', 'Y'),
('PERM_CONTENT_UPLOAD', 'TENANT002', '콘텐츠 업로드', '강의 자료 업로드', '/content', 'POST', 'Y'),
('PERM_REPORT_ANALYTICS', 'TENANT002', '통계 분석', '학습 분석 보고서', '/report', 'GET', 'Y');

-- ============================================================================
-- 5. 역할-권한 매핑 (tb_role_permission)
-- ============================================================================

-- TENANT001 - 관리자(ROLE_ADMIN) 역할에 모든 권한 할당
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
SELECT 'ROLE_ADMIN' as role_cd, perm_cd, 'TENANT001' as tenant_id 
FROM tb_permission WHERE tenant_id = 'TENANT001';

-- TENANT001 - 담당자(ROLE_MANAGER) 역할에 제한된 권한 할당
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_MANAGER', 'PERM_USER_READ', 'TENANT001'),
('ROLE_MANAGER', 'PERM_USER_UPDATE', 'TENANT001'),
('ROLE_MANAGER', 'PERM_COURSE_CREATE', 'TENANT001'),
('ROLE_MANAGER', 'PERM_COURSE_READ', 'TENANT001'),
('ROLE_MANAGER', 'PERM_COURSE_UPDATE', 'TENANT001'),
('ROLE_MANAGER', 'PERM_ENROLLMENT_READ', 'TENANT001'),
('ROLE_MANAGER', 'PERM_REPORT_VIEW', 'TENANT001');

-- TENANT001 - 강사(ROLE_INSTRUCTOR) 역할 권한
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_INSTRUCTOR', 'PERM_USER_READ', 'TENANT001'),
('ROLE_INSTRUCTOR', 'PERM_COURSE_READ', 'TENANT001'),
('ROLE_INSTRUCTOR', 'PERM_ENROLLMENT_READ', 'TENANT001'),
('ROLE_INSTRUCTOR', 'PERM_ENROLLMENT_UPDATE', 'TENANT001');

-- TENANT001 - 학생(ROLE_STUDENT) 역할 권한
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_STUDENT', 'PERM_COURSE_READ', 'TENANT001'),
('ROLE_STUDENT', 'PERM_ENROLLMENT_READ', 'TENANT001');

-- TENANT002 - 시스템관리자 권한들
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
SELECT 'ROLE_ADMIN' as role_cd, perm_cd, 'TENANT002' as tenant_id 
FROM tb_permission WHERE tenant_id = 'TENANT002';

-- TENANT002 - 과정관리자 권한들
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_MANAGER', 'PERM_COURSE_CREATE', 'TENANT002'),
('ROLE_MANAGER', 'PERM_COURSE_READ', 'TENANT002'),
('ROLE_MANAGER', 'PERM_COURSE_UPDATE', 'TENANT002'),
('ROLE_MANAGER', 'PERM_CONTENT_UPLOAD', 'TENANT002'),
('ROLE_MANAGER', 'PERM_ENROLLMENT_READ', 'TENANT002');

-- TENANT002 - 강사 권한들
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_INSTRUCTOR', 'PERM_COURSE_READ', 'TENANT002'),
('ROLE_INSTRUCTOR', 'PERM_CONTENT_UPLOAD', 'TENANT002'),
('ROLE_INSTRUCTOR', 'PERM_ENROLLMENT_READ', 'TENANT002');

-- TENANT002 - 수강자 권한들
INSERT INTO tb_role_permission (role_cd, perm_cd, tenant_id) 
VALUES 
('ROLE_STUDENT', 'PERM_COURSE_READ', 'TENANT002'),
('ROLE_STUDENT', 'PERM_ENROLLMENT_READ', 'TENANT002');

-- ============================================================================
-- 6. 사용자-역할 매핑 (tb_user_role)
-- ============================================================================
-- ⚠️ 주의: tb_user_role INSERT는 제거됨!
-- 이유: tb_user가 UserInitializer에서 자동으로 생성되므로,
--      SQL 실행 시점에는 아직 사용자가 없음 (외래키 제약 위반)
--
-- 향후 처리 방법:
-- 1. RoleInitializer 생성 (UserInitializer 이후 실행)
-- 2. 또는 UserInitializer에서 사용자 생성 후 역할 할당
-- 3. 또는 수동으로 애플리케이션 시작 후 API로 역할 할당

-- -- TENANT001 사용자 역할 매핑 (예시)
-- INSERT INTO tb_user_role (user_id, role_cd, tenant_id) 
-- VALUES 
-- ('admin001', 'ROLE_ADMIN', 'TENANT001'),
-- ('user001', 'ROLE_MANAGER', 'TENANT001'),
-- ('user002', 'ROLE_STUDENT', 'TENANT001'),
-- ('instructor001', 'ROLE_INSTRUCTOR', 'TENANT001');
--
-- -- TENANT002 사용자 역할 매핑 (예시)
-- INSERT INTO tb_user_role (user_id, role_cd, tenant_id) 
-- VALUES 
-- ('admin002', 'ROLE_ADMIN', 'TENANT002'),
-- ('user003', 'ROLE_MANAGER', 'TENANT002'),
-- ('user004', 'ROLE_STUDENT', 'TENANT002'),
-- ('instructor002', 'ROLE_INSTRUCTOR', 'TENANT002');

-- ============================================================================
-- 7. 메뉴 초기 데이터
-- ============================================================================

-- TENANT001 메뉴들
INSERT INTO tb_menu (menu_id, tenant_id, menu_nm, menu_url, menu_icon, sort_order, parent_menu_id, use_yn) 
VALUES 
('MENU001', 'TENANT001', '대시보드', '/dashboard', 'fas fa-chart-line', 10, NULL, 'Y'),
('MENU002', 'TENANT001', '사용자 관리', NULL, 'fas fa-users', 20, NULL, 'Y'),
('MENU002_01', 'TENANT001', '사용자 목록', '/user/list', 'fas fa-list', 21, 'MENU002', 'Y'),
('MENU002_02', 'TENANT001', '부서 관리', '/department/list', 'fas fa-building', 22, 'MENU002', 'Y'),
('MENU003', 'TENANT001', '과정 관리', NULL, 'fas fa-book', 30, NULL, 'Y'),
('MENU003_01', 'TENANT001', '과정 목록', '/course/list', 'fas fa-list', 31, 'MENU003', 'Y'),
('MENU003_02', 'TENANT001', '강의 관리', '/lecture/list', 'fas fa-graduation-cap', 32, 'MENU003', 'Y'),
('MENU003_03', 'TENANT001', '콘텐츠 관리', '/content/list', 'fas fa-file-video', 33, 'MENU003', 'Y'),
('MENU004', 'TENANT001', '수강 관리', '/enrollment/list', 'fas fa-graduation-cap', 40, NULL, 'Y'),
('MENU005', 'TENANT001', '보고서', '/report/list', 'fas fa-file-alt', 50, NULL, 'Y'),
('MENU006', 'TENANT001', '설정', NULL, 'fas fa-cog', 60, NULL, 'Y'),
('MENU006_01', 'TENANT001', '역할 관리', '/role/list', 'fas fa-tasks', 61, 'MENU006', 'Y'),
('MENU006_02', 'TENANT001', '권한 관리', '/permission/list', 'fas fa-lock', 62, 'MENU006', 'Y');

-- TENANT002 메뉴들
INSERT INTO tb_menu (menu_id, tenant_id, menu_nm, menu_url, menu_icon, sort_order, parent_menu_id, use_yn) 
VALUES 
('MENU001', 'TENANT002', '대시보드', '/dashboard', 'fas fa-home', 10, NULL, 'Y'),
('MENU002', 'TENANT002', '강의 관리', NULL, 'fas fa-chalkboard-teacher', 20, NULL, 'Y'),
('MENU002_01', 'TENANT002', '강의 개설', '/course/create', 'fas fa-plus-circle', 21, 'MENU002', 'Y'),
('MENU002_02', 'TENANT002', '강의 목록', '/course/list', 'fas fa-list-ul', 22, 'MENU002', 'Y'),
('MENU002_03', 'TENANT002', '강의(단원)', '/lecture/list', 'fas fa-book', 23, 'MENU002', 'Y'),
('MENU003', 'TENANT002', '콘텐츠 관리', '/content/list', 'fas fa-video', 30, NULL, 'Y'),
('MENU004', 'TENANT002', '수강신청', NULL, 'fas fa-graduation-cap', 40, NULL, 'Y'),
('MENU004_01', 'TENANT002', '신청 현황', '/enrollment/list', 'fas fa-calendar-check', 41, 'MENU004', 'Y'),
('MENU005', 'TENANT002', '학습 분석', '/report/analytics', 'fas fa-chart-bar', 50, NULL, 'Y'),
('MENU006', 'TENANT002', '시스템', NULL, 'fas fa-sliders-h', 60, NULL, 'Y'),
('MENU006_01', 'TENANT002', '사용자 관리', '/user/list', 'fas fa-user-tie', 61, 'MENU006', 'Y'),
('MENU006_02', 'TENANT002', '권한 설정', '/role/list', 'fas fa-shield-alt', 62, 'MENU006', 'Y');

-- ============================================================================
-- 8. 역할-메뉴 매핑 (tb_role_menu)
-- ============================================================================

-- TENANT001 - 관리자는 모든 메뉴 접근 가능
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
SELECT 'ROLE_ADMIN' as role_cd, menu_id, 'TENANT001' as tenant_id 
FROM tb_menu WHERE tenant_id = 'TENANT001';

-- TENANT001 - 담당자 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_MANAGER', 'MENU001', 'TENANT001'),
('ROLE_MANAGER', 'MENU002_01', 'TENANT001'),
('ROLE_MANAGER', 'MENU003', 'TENANT001'),
('ROLE_MANAGER', 'MENU003_01', 'TENANT001'),
('ROLE_MANAGER', 'MENU003_02', 'TENANT001'),
('ROLE_MANAGER', 'MENU003_03', 'TENANT001'),
('ROLE_MANAGER', 'MENU004', 'TENANT001'),
('ROLE_MANAGER', 'MENU005', 'TENANT001');

-- TENANT001 - 강사 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_INSTRUCTOR', 'MENU001', 'TENANT001'),
('ROLE_INSTRUCTOR', 'MENU003_01', 'TENANT001'),
('ROLE_INSTRUCTOR', 'MENU003_02', 'TENANT001'),
('ROLE_INSTRUCTOR', 'MENU003_03', 'TENANT001'),
('ROLE_INSTRUCTOR', 'MENU004', 'TENANT001');

-- TENANT001 - 학생 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_STUDENT', 'MENU001', 'TENANT001'),
('ROLE_STUDENT', 'MENU003_01', 'TENANT001'),
('ROLE_STUDENT', 'MENU004', 'TENANT001');

-- TENANT002 - 시스템관리자는 모든 메뉴 접근
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
SELECT 'ROLE_ADMIN' as role_cd, menu_id, 'TENANT002' as tenant_id 
FROM tb_menu WHERE tenant_id = 'TENANT002';

-- TENANT002 - 과정관리자 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_MANAGER', 'MENU001', 'TENANT002'),
('ROLE_MANAGER', 'MENU002', 'TENANT002'),
('ROLE_MANAGER', 'MENU002_01', 'TENANT002'),
('ROLE_MANAGER', 'MENU002_02', 'TENANT002'),
('ROLE_MANAGER', 'MENU002_03', 'TENANT002'),
('ROLE_MANAGER', 'MENU003', 'TENANT002'),
('ROLE_MANAGER', 'MENU004_01', 'TENANT002'),
('ROLE_MANAGER', 'MENU005', 'TENANT002');

-- TENANT002 - 강사 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_INSTRUCTOR', 'MENU001', 'TENANT002'),
('ROLE_INSTRUCTOR', 'MENU002_02', 'TENANT002'),
('ROLE_INSTRUCTOR', 'MENU002_03', 'TENANT002'),
('ROLE_INSTRUCTOR', 'MENU003', 'TENANT002');

-- TENANT002 - 수강자 메뉴
INSERT INTO tb_role_menu (role_cd, menu_id, tenant_id) 
VALUES 
('ROLE_STUDENT', 'MENU001', 'TENANT002'),
('ROLE_STUDENT', 'MENU002_02', 'TENANT002'),
('ROLE_STUDENT', 'MENU004', 'TENANT002'),
('ROLE_STUDENT', 'MENU004_01', 'TENANT002');

-- ============================================================================
-- 9. 콘텐츠(차시) 초기 데이터 ★ 독립적으로 먼저 생성!
-- ============================================================================

-- TENANT001 콘텐츠들
INSERT INTO tb_content (content_id, tenant_id, content_nm, content_type, content_url, 
                        content_desc, duration_minutes, use_yn) 
VALUES 
('CONTENT001', 'TENANT001', '회사 소개 영상', 'VIDEO', '/videos/intro.mp4', 
 '회사 역사 및 비전 소개', 15, 'Y'),
('CONTENT002', 'TENANT001', '조직도 및 직무', 'DOCUMENT', '/docs/organization.pdf', 
 '조직도 및 직무 설명서', 30, 'Y'),
('CONTENT003', 'TENANT001', '부서별 역할', 'DOCUMENT', '/docs/department_roles.pdf', 
 '각 부서의 역할과 책임', 30, 'Y'),
('CONTENT004', 'TENANT001', 'Spring Boot 설치', 'VIDEO', '/videos/springboot_install.mp4', 
 'Spring Boot 환경 설정', 20, 'Y'),
('CONTENT005', 'TENANT001', '첫 프로젝트 생성', 'VIDEO', '/videos/first_project.mp4', 
 '첫 번째 Spring Boot 프로젝트', 25, 'Y'),
('CONTENT006', 'TENANT001', 'REST API 개념', 'VIDEO', '/videos/rest_api_concept.mp4', 
 'REST 아키텍처 이해', 30, 'Y'),
('CONTENT007', 'TENANT001', 'HTTP 메서드', 'DOCUMENT', '/docs/http_methods.pdf', 
 'GET, POST, PUT, DELETE 이해', 20, 'Y'),
('CONTENT008', 'TENANT001', 'API 구현 실습', 'QUIZ', '/quizzes/api_quiz.json', 
 'REST API 구현 확인 퀴즈', 15, 'Y');

-- TENANT002 콘텐츠들
INSERT INTO tb_content (content_id, tenant_id, content_nm, content_type, content_url, 
                        content_desc, duration_minutes, use_yn) 
VALUES 
('CONTENT001', 'TENANT002', 'Python 설치 및 설정', 'VIDEO', '/videos/python_setup.mp4', 
 'Python 환경 설정', 25, 'Y'),
('CONTENT002', 'TENANT002', '기본 문법', 'DOCUMENT', '/docs/python_basic.pdf', 
 'Python 기본 문법 학습', 40, 'Y'),
('CONTENT003', 'TENANT002', '자료구조', 'VIDEO', '/videos/data_structure.mp4', 
 '리스트, 딕셔너리, 튜플 학습', 45, 'Y'),
('CONTENT004', 'TENANT002', '조건문 (if-else)', 'VIDEO', '/videos/conditional.mp4', 
 'if, elif, else 문법', 30, 'Y'),
('CONTENT005', 'TENANT002', '반복문 (for, while)', 'VIDEO', '/videos/loop.mp4', 
 'for와 while 반복문', 40, 'Y'),
('CONTENT006', 'TENANT002', '함수 작성', 'DOCUMENT', '/docs/function.pdf', 
 '함수 정의와 호출', 30, 'Y'),
('CONTENT007', 'TENANT002', 'Class와 객체', 'VIDEO', '/videos/class_object.mp4', 
 'OOP 기초 개념', 50, 'Y'),
('CONTENT008', 'TENANT002', '상속과 다형성', 'VIDEO', '/videos/inheritance.mp4', 
 'Class 상속 및 메서드 오버라이딩', 45, 'Y');

-- ============================================================================
-- 10. 강의(단원) 초기 데이터 ★ 과정과 독립적으로 생성!
-- ============================================================================

-- TENANT001 강의들
INSERT INTO tb_lecture (lecture_id, tenant_id, lecture_nm, lecture_desc, duration_minutes, use_yn) 
VALUES 
('LECTURE001', 'TENANT001', '회사 기초 이해', '회사 역사, 비전, 조직 구조 학습', 45, 'Y'),
('LECTURE002', 'TENANT001', '직무 이해', '각 부서별 역할과 책임 이해', 60, 'Y'),
('LECTURE003', 'TENANT001', 'Spring Boot 기초', 'Spring Boot 프로젝트 설정 및 기초', 90, 'Y'),
('LECTURE004', 'TENANT001', 'REST API 구현', 'RESTful API 설계 및 구현 방법', 120, 'Y');

-- TENANT002 강의들
INSERT INTO tb_lecture (lecture_id, tenant_id, lecture_nm, lecture_desc, duration_minutes, use_yn) 
VALUES 
('LECTURE001', 'TENANT002', 'Python 기초 문법', 'Python 설치, 기본 문법, 자료구조', 180, 'Y'),
('LECTURE002', 'TENANT002', 'Python 제어문과 함수', '조건문, 반복문, 함수 작성', 150, 'Y'),
('LECTURE003', 'TENANT002', 'Python 객체지향', 'Class와 객체, 상속, 다형성', 150, 'Y'),
('LECTURE004', 'TENANT002', '데이터 전처리', 'Pandas를 이용한 데이터 정제', 120, 'Y'),
('LECTURE005', 'TENANT002', '데이터 분석 기초', '통계 분석, 시각화', 120, 'Y');

-- ============================================================================
-- 11. 과정 초기 데이터 ★ 독립적으로 생성!
-- ============================================================================

-- TENANT001 과정들
INSERT INTO tb_course (course_id, tenant_id, course_nm, course_desc, instructor_nm,
                       start_dt, end_dt, max_students, current_students, status, use_yn) 
VALUES 
('COURSE001', 'TENANT001', '신입사원 교육', '신입사원을 위한 기초 교육 과정', '박교수',
 '2024-03-01', '2024-04-30', 50, 3, 'OPEN', 'Y'),
('COURSE002', 'TENANT001', 'Spring Boot 심화', 'Spring Boot 프레임워크 심화 학습', '박교수',
 '2024-04-01', '2024-06-30', 30, 2, 'OPEN', 'Y');

-- TENANT002 과정들
INSERT INTO tb_course (course_id, tenant_id, course_nm, course_desc, instructor_nm,
                       start_dt, end_dt, max_students, current_students, status, use_yn) 
VALUES 
('COURSE001', 'TENANT002', '파이썬 기초', 'Python 프로그래밍 입문 과정', '최강강사',
 '2024-03-01', '2024-05-31', 100, 5, 'OPEN', 'Y'),
('COURSE002', 'TENANT002', '데이터 분석 실무', '데이터 분석 기초부터 실무까지', '최강강사',
 '2024-04-15', '2024-07-15', 50, 2, 'OPEN', 'Y');

-- ============================================================================
-- 12. 강의-콘텐츠 매핑 (tb_lecture_content) ★ M:N 매핑 1
-- ============================================================================

-- TENANT001 - 강의별 콘텐츠 매핑
INSERT INTO tb_lecture_content (lecture_id, content_id, tenant_id, content_order) 
VALUES 
('LECTURE001', 'CONTENT001', 'TENANT001', 10),
('LECTURE001', 'CONTENT002', 'TENANT001', 20),
('LECTURE002', 'CONTENT003', 'TENANT001', 10),
('LECTURE003', 'CONTENT004', 'TENANT001', 10),
('LECTURE003', 'CONTENT005', 'TENANT001', 20),
('LECTURE004', 'CONTENT006', 'TENANT001', 10),
('LECTURE004', 'CONTENT007', 'TENANT001', 20),
('LECTURE004', 'CONTENT008', 'TENANT001', 30);

-- TENANT002 - 강의별 콘텐츠 매핑
INSERT INTO tb_lecture_content (lecture_id, content_id, tenant_id, content_order) 
VALUES 
('LECTURE001', 'CONTENT001', 'TENANT002', 10),
('LECTURE001', 'CONTENT002', 'TENANT002', 20),
('LECTURE001', 'CONTENT003', 'TENANT002', 30),
('LECTURE002', 'CONTENT004', 'TENANT002', 10),
('LECTURE002', 'CONTENT005', 'TENANT002', 20),
('LECTURE002', 'CONTENT006', 'TENANT002', 30),
('LECTURE003', 'CONTENT007', 'TENANT002', 10),
('LECTURE003', 'CONTENT008', 'TENANT002', 20);

-- ============================================================================
-- 13. 과정-강의 매핑 (tb_course_lecture) ★ M:N 매핑 2
-- ============================================================================

-- TENANT001 - 과정별 강의 매핑
INSERT INTO tb_course_lecture (course_id, lecture_id, tenant_id, lecture_order) 
VALUES 
('COURSE001', 'LECTURE001', 'TENANT001', 10),
('COURSE001', 'LECTURE002', 'TENANT001', 20),
('COURSE002', 'LECTURE003', 'TENANT001', 10),
('COURSE002', 'LECTURE004', 'TENANT001', 20);

-- TENANT002 - 과정별 강의 매핑
INSERT INTO tb_course_lecture (course_id, lecture_id, tenant_id, lecture_order) 
VALUES 
('COURSE001', 'LECTURE001', 'TENANT002', 10),
('COURSE001', 'LECTURE002', 'TENANT002', 20),
('COURSE001', 'LECTURE003', 'TENANT002', 30),
('COURSE002', 'LECTURE004', 'TENANT002', 10),
('COURSE002', 'LECTURE005', 'TENANT002', 20);

-- ============================================================================
-- 14. 수강 정보 초기 데이터
-- ============================================================================
-- ⚠️ 주의: tb_enrollment INSERT는 제거됨!
-- 이유: tb_user가 UserInitializer에서 생성되므로,
--      SQL 실행 시점에는 아직 사용자가 없음 (외래키 제약 위반)
--
-- 향후 처리 방법:
-- 1. EnrollmentInitializer 생성 (UserInitializer 이후 실행)
-- 2. 또는 수강신청 API를 통해 직접 등록
-- 3. 또는 테스트 환경에서만 수동으로 추가

-- -- TENANT001 수강 정보 (예시)
-- INSERT INTO tb_enrollment (enrollment_id, tenant_id, user_id, course_id, enrollment_status, 
--                            completion_rate, enrollment_dt) 
-- VALUES 
-- ('ENR001', 'TENANT001', 'user001', 'COURSE001', 'PROGRESS', 50.0, '2024-03-10'),
-- ('ENR002', 'TENANT001', 'user002', 'COURSE001', 'PROGRESS', 30.0, '2024-03-15'),
-- ('ENR003', 'TENANT001', 'user002', 'COURSE002', 'ENROLL', 0.0, '2024-04-05');
--
-- -- TENANT002 수강 정보 (예시)
-- INSERT INTO tb_enrollment (enrollment_id, tenant_id, user_id, course_id, enrollment_status, 
--                            completion_rate, enrollment_dt) 
-- VALUES 
-- ('ENR001', 'TENANT002', 'user003', 'COURSE001', 'PROGRESS', 40.0, '2024-03-05'),
-- ('ENR002', 'TENANT002', 'user004', 'COURSE001', 'PROGRESS', 20.0, '2024-03-10'),
-- ('ENR003', 'TENANT002', 'user003', 'COURSE002', 'ENROLL', 0.0, '2024-04-20');

-- ============================================================================
-- 완료
-- ============================================================================
-- 모든 초기 데이터 로드 완료
