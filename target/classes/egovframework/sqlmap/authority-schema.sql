-- ============================================
-- 권한 관리 시스템 데이터베이스 스키마
-- eGovFrame 4.2.0 LMS 프로젝트
-- ============================================

-- ============================================
-- 1. 사용자 관리
-- ============================================

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS tb_user (
    user_id VARCHAR(50) PRIMARY KEY COMMENT '사용자ID',
    user_nm VARCHAR(100) NOT NULL COMMENT '사용자명',
    password VARCHAR(200) NOT NULL COMMENT '비밀번호(암호화)',
    email VARCHAR(100) COMMENT '이메일(암호화)',
    phone VARCHAR(20) COMMENT '전화번호(암호화)',
    address VARCHAR(200) COMMENT '주소(암호화)',
    dept_cd VARCHAR(20) COMMENT '부서코드',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_dept (dept_cd),
    INDEX idx_use_yn (use_yn),
    INDEX idx_email (email),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='사용자';

-- ============================================
-- 2. 역할(권한) 관리
-- ============================================

-- 역할 테이블 (계층 구조 지원)
CREATE TABLE IF NOT EXISTS tb_role (
    role_id VARCHAR(50) PRIMARY KEY COMMENT '역할ID',
    role_nm VARCHAR(100) NOT NULL COMMENT '역할명',
    role_desc VARCHAR(500) COMMENT '역할설명',
    parent_role_id VARCHAR(50) COMMENT '상위역할ID',
    role_level INT DEFAULT 0 COMMENT '역할레벨(0:최상위)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (parent_role_id) REFERENCES tb_role(role_id) ON DELETE SET NULL,
    INDEX idx_parent (parent_role_id),
    INDEX idx_level (role_level),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='역할';

-- 사용자-역할 매핑 (다중 역할 지원)
CREATE TABLE IF NOT EXISTS tb_user_role (
    user_role_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자역할ID',
    user_id VARCHAR(50) NOT NULL COMMENT '사용자ID',
    role_id VARCHAR(50) NOT NULL COMMENT '역할ID',
    start_dt DATE COMMENT '역할유효시작일',
    end_dt DATE COMMENT '역할유효종료일',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES tb_role(role_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user (user_id),
    INDEX idx_role (role_id),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='사용자-역할 매핑';

-- ============================================
-- 3. 권한 및 메뉴 관리
-- ============================================

-- 권한 테이블
CREATE TABLE IF NOT EXISTS tb_permission (
    perm_id VARCHAR(50) PRIMARY KEY COMMENT '권한ID',
    perm_nm VARCHAR(100) NOT NULL COMMENT '권한명',
    perm_type VARCHAR(20) NOT NULL COMMENT '권한타입(MENU/FUNCTION/DATA)',
    perm_desc VARCHAR(500) COMMENT '권한설명',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    INDEX idx_type (perm_type),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='권한';

-- 메뉴 테이블 (계층 구조)
CREATE TABLE IF NOT EXISTS tb_menu (
    menu_id VARCHAR(50) PRIMARY KEY COMMENT '메뉴ID',
    menu_nm VARCHAR(100) NOT NULL COMMENT '메뉴명',
    parent_menu_id VARCHAR(50) COMMENT '상위메뉴ID',
    menu_level INT DEFAULT 0 COMMENT '메뉴레벨(0:최상위)',
    menu_url VARCHAR(200) COMMENT '메뉴URL',
    menu_order INT DEFAULT 0 COMMENT '메뉴순서',
    icon_class VARCHAR(50) COMMENT 'ICON CSS클래스',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (parent_menu_id) REFERENCES tb_menu(menu_id) ON DELETE SET NULL,
    INDEX idx_parent (parent_menu_id),
    INDEX idx_level (menu_level),
    INDEX idx_order (menu_order),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='메뉴';

-- 메뉴-권한 매핑
CREATE TABLE IF NOT EXISTS tb_menu_permission (
    menu_perm_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '메뉴권한ID',
    menu_id VARCHAR(50) NOT NULL COMMENT '메뉴ID',
    perm_id VARCHAR(50) NOT NULL COMMENT '권한ID',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (menu_id) REFERENCES tb_menu(menu_id) ON DELETE CASCADE,
    FOREIGN KEY (perm_id) REFERENCES tb_permission(perm_id) ON DELETE CASCADE,
    UNIQUE KEY uk_menu_perm (menu_id, perm_id),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='메뉴-권한 매핑';

-- 역할-권한 매핑
CREATE TABLE IF NOT EXISTS tb_role_permission (
    role_perm_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '역할권한ID',
    role_id VARCHAR(50) NOT NULL COMMENT '역할ID',
    perm_id VARCHAR(50) NOT NULL COMMENT '권한ID',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (role_id) REFERENCES tb_role(role_id) ON DELETE CASCADE,
    FOREIGN KEY (perm_id) REFERENCES tb_permission(perm_id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_perm (role_id, perm_id),
    INDEX idx_role (role_id),
    INDEX idx_perm (perm_id),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='역할-권한 매핑';

-- ============================================
-- 4. 과정 및 콘텐츠 관리
-- ============================================

-- 과정 테이블
CREATE TABLE IF NOT EXISTS tb_course (
    course_id VARCHAR(50) PRIMARY KEY COMMENT '과정ID',
    course_nm VARCHAR(200) NOT NULL COMMENT '과정명',
    course_desc TEXT COMMENT '과정설명',
    instructor_id VARCHAR(50) COMMENT '강사ID',
    start_dt DATE COMMENT '과정시작일',
    end_dt DATE COMMENT '과정종료일',
    max_students INT COMMENT '최대수강인원',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '과정상태(DRAFT/OPEN/CLOSED)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    FOREIGN KEY (instructor_id) REFERENCES tb_user(user_id) ON DELETE SET NULL,
    INDEX idx_instructor (instructor_id),
    INDEX idx_status (status),
    INDEX idx_date (start_dt, end_dt),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='과정';

-- 과정별 사용자 권한 (동적 권한)
CREATE TABLE IF NOT EXISTS tb_course_user_permission (
    course_user_perm_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '과정사용자권한ID',
    course_id VARCHAR(50) NOT NULL COMMENT '과정ID',
    user_id VARCHAR(50) NOT NULL COMMENT '사용자ID',
    perm_type VARCHAR(20) NOT NULL COMMENT '권한타입(OWNER/EDIT/VIEW/ENROLL)',
    start_dt DATE COMMENT '권한유효시작일',
    end_dt DATE COMMENT '권한유효종료일',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (course_id) REFERENCES tb_course(course_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tb_user(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_course_user_perm (course_id, user_id, perm_type),
    INDEX idx_course (course_id),
    INDEX idx_user (user_id),
    INDEX idx_perm_type (perm_type),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='과정별 사용자 권한';

-- 콘텐츠 테이블
CREATE TABLE IF NOT EXISTS tb_content (
    content_id VARCHAR(50) PRIMARY KEY COMMENT '콘텐츠ID',
    course_id VARCHAR(50) NOT NULL COMMENT '과정ID',
    content_nm VARCHAR(200) NOT NULL COMMENT '콘텐츠명',
    content_type VARCHAR(20) COMMENT '콘텐츠타입(VIDEO/DOCUMENT/QUIZ)',
    content_url VARCHAR(500) COMMENT '콘텐츠URL',
    content_order INT DEFAULT 0 COMMENT '콘텐츠순서',
    is_preview CHAR(1) DEFAULT 'N' COMMENT '미리보기허용여부',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
    
    FOREIGN KEY (course_id) REFERENCES tb_course(course_id) ON DELETE CASCADE,
    INDEX idx_course (course_id),
    INDEX idx_order (content_order),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='콘텐츠';

-- ============================================
-- 5. 감사 로그 (전자정부 보안 요구사항)
-- ============================================

CREATE TABLE IF NOT EXISTS tb_access_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '로그ID',
    user_id VARCHAR(50) COMMENT '사용자ID',
    access_type VARCHAR(20) COMMENT '접근타입(LOGIN/MENU/COURSE/CONTENT)',
    resource_id VARCHAR(50) COMMENT '리소스ID',
    resource_type VARCHAR(20) COMMENT '리소스타입',
    action VARCHAR(20) COMMENT '작업(VIEW/CREATE/UPDATE/DELETE)',
    ip_addr VARCHAR(50) COMMENT 'IP주소',
    user_agent VARCHAR(500) COMMENT 'USER AGENT',
    result VARCHAR(20) COMMENT '결과(SUCCESS/FAIL)',
    fail_reason VARCHAR(500) COMMENT '실패사유',
    access_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '접근일시',
    
    INDEX idx_user (user_id),
    INDEX idx_access_dt (access_dt),
    INDEX idx_resource (resource_type, resource_id),
    
    CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci
) COMMENT='접근 로그';
