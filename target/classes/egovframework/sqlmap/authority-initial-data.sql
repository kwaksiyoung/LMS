-- ============================================
-- 권한 관리 시스템 초기 데이터
-- ============================================

-- ============================================
-- 1. 기본 역할 데이터 (3개)
-- ============================================

INSERT INTO tb_role (role_id, role_nm, role_desc, role_level, use_yn) VALUES
('ROLE_ADMIN', '시스템 관리자', '전체 시스템 관리 권한', 0, 'Y'),
('ROLE_INSTRUCTOR', '강사', '과정 및 콘텐츠 관리 권한', 1, 'Y'),
('ROLE_STUDENT', '학습자', '학습 콘텐츠 접근 권한', 2, 'Y');

-- ============================================
-- 2. 기본 권한 데이터 (10개)
-- ============================================

INSERT INTO tb_permission (perm_id, perm_nm, perm_type, perm_desc, use_yn) VALUES
-- 메뉴 권한
('MENU_ADMIN', '관리자 메뉴', 'MENU', '관리자 전용 메뉴 접근', 'Y'),
('MENU_COURSE_MGMT', '과정 관리 메뉴', 'MENU', '과정 관리 메뉴 접근', 'Y'),
('MENU_LEARNING', '학습 메뉴', 'MENU', '학습 메뉴 접근', 'Y'),

-- 기능 권한
('COURSE_CREATE', '과정 생성', 'FUNCTION', '새 과정 생성', 'Y'),
('COURSE_EDIT', '과정 수정', 'FUNCTION', '과정 정보 수정', 'Y'),
('COURSE_DELETE', '과정 삭제', 'FUNCTION', '과정 삭제', 'Y'),
('CONTENT_CREATE', '콘텐츠 생성', 'FUNCTION', '콘텐츠 등록', 'Y'),
('CONTENT_EDIT', '콘텐츠 수정', 'FUNCTION', '콘텐츠 수정', 'Y'),
('CONTENT_VIEW', '콘텐츠 조회', 'FUNCTION', '콘텐츠 조회', 'Y'),
('USER_MGMT', '사용자 관리', 'FUNCTION', '사용자 관리', 'Y');

-- ============================================
-- 3. 역할-권한 매핑 (RBAC)
-- ============================================

-- 관리자 권한: 모든 권한 부여
INSERT INTO tb_role_permission (role_id, perm_id) VALUES
('ROLE_ADMIN', 'MENU_ADMIN'),
('ROLE_ADMIN', 'MENU_COURSE_MGMT'),
('ROLE_ADMIN', 'MENU_LEARNING'),
('ROLE_ADMIN', 'COURSE_CREATE'),
('ROLE_ADMIN', 'COURSE_EDIT'),
('ROLE_ADMIN', 'COURSE_DELETE'),
('ROLE_ADMIN', 'CONTENT_CREATE'),
('ROLE_ADMIN', 'CONTENT_EDIT'),
('ROLE_ADMIN', 'CONTENT_VIEW'),
('ROLE_ADMIN', 'USER_MGMT'),

-- 강사 권한: 과정 및 콘텐츠 관리
('ROLE_INSTRUCTOR', 'MENU_COURSE_MGMT'),
('ROLE_INSTRUCTOR', 'MENU_LEARNING'),
('ROLE_INSTRUCTOR', 'COURSE_CREATE'),
('ROLE_INSTRUCTOR', 'COURSE_EDIT'),
('ROLE_INSTRUCTOR', 'CONTENT_CREATE'),
('ROLE_INSTRUCTOR', 'CONTENT_EDIT'),
('ROLE_INSTRUCTOR', 'CONTENT_VIEW'),

-- 학습자 권한: 학습만 가능
('ROLE_STUDENT', 'MENU_LEARNING'),
('ROLE_STUDENT', 'CONTENT_VIEW');

-- ============================================
-- 4. 기본 메뉴 데이터 (9개)
-- ============================================

INSERT INTO tb_menu (menu_id, menu_nm, parent_menu_id, menu_level, menu_url, menu_order, use_yn) VALUES
-- 관리자 메뉴
('M001', '관리자', NULL, 0, NULL, 1, 'Y'),
('M001001', '사용자 관리', 'M001', 1, '/admin/users', 1, 'Y'),
('M001002', '역할 관리', 'M001', 1, '/admin/roles', 2, 'Y'),
('M001003', '권한 관리', 'M001', 1, '/admin/permissions', 3, 'Y'),

-- 과정 관리 메뉴
('M002', '과정 관리', NULL, 0, NULL, 2, 'Y'),
('M002001', '과정 목록', 'M002', 1, '/course/list', 1, 'Y'),
('M002002', '과정 등록', 'M002', 1, '/course/create', 2, 'Y'),

-- 학습 메뉴
('M003', '학습', NULL, 0, NULL, 3, 'Y'),
('M003001', '내 학습', 'M003', 1, '/learning/my', 1, 'Y'),
('M003002', '과정 검색', 'M003', 1, '/learning/search', 2, 'Y');

-- ============================================
-- 5. 메뉴-권한 매핑
-- ============================================

INSERT INTO tb_menu_permission (menu_id, perm_id) VALUES
('M001', 'MENU_ADMIN'),
('M001001', 'USER_MGMT'),
('M001002', 'USER_MGMT'),
('M001003', 'USER_MGMT'),
('M002', 'MENU_COURSE_MGMT'),
('M002001', 'MENU_COURSE_MGMT'),
('M002002', 'COURSE_CREATE'),
('M003', 'MENU_LEARNING'),
('M003001', 'CONTENT_VIEW'),
('M003002', 'CONTENT_VIEW');

-- ============================================
-- 6. 테스트 사용자 데이터
-- ============================================

-- 관리자 계정 (비밀번호: admin123! - BCrypt로 암호화 필요)
-- 실제 운영 환경에서는 보안 가이드라인에 따라 강화된 비밀번호 사용
INSERT INTO tb_user (user_id, user_nm, password, email, use_yn) VALUES
('admin', '시스템 관리자', '$2a$10$slYQmyNdGzin7olVN3p5be0kJcNNCUEmklomp.WBi5nLdQXQfkdxm', 'admin@lms.com', 'Y');

-- 강사 계정
INSERT INTO tb_user (user_id, user_nm, password, email, use_yn) VALUES
('instructor', '홍길동(강사)', '$2a$10$slYQmyNdGzin7olVN3p5be0kJcNNCUEmklomp.WBi5nLdQXQfkdxm', 'hong@lms.com', 'Y');

-- 학습자 계정
INSERT INTO tb_user (user_id, user_nm, password, email, use_yn) VALUES
('student', '김영희(학습자)', '$2a$10$slYQmyNdGzin7olVN3p5be0kJcNNCUEmklomp.WBi5nLdQXQfkdxm', 'kim@lms.com', 'Y');

-- ============================================
-- 7. 사용자-역할 매핑
-- ============================================

-- admin 사용자에 ROLE_ADMIN 할당
INSERT INTO tb_user_role (user_id, role_id) VALUES
('admin', 'ROLE_ADMIN');

-- instructor 사용자에 ROLE_INSTRUCTOR 할당
INSERT INTO tb_user_role (user_id, role_id) VALUES
('instructor', 'ROLE_INSTRUCTOR');

-- student 사용자에 ROLE_STUDENT 할당
INSERT INTO tb_user_role (user_id, role_id) VALUES
('student', 'ROLE_STUDENT');

-- ============================================
-- 주의사항:
-- 1. 테스트 사용자 비밀번호는 모두 'password123' (BCrypt로 암호화됨)
-- 2. 운영 환경에서는 초기 데이터 제거 및 강화된 보안 설정 필수
-- 3. HTTPS 적용 권장
-- ============================================
