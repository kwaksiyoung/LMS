-- ============================================================================
-- LMS SaaS 멀티테넌시 데이터베이스 스키마 (최종 - 계층 구조 수정)
-- 과정 → 강의(단원) → 콘텐츠(차시) 의 올바른 계층 구조 적용
-- ============================================================================

SET FOREIGN_KEY_CHECKS=0;

-- ============================================================================
-- 1. 고객사(테넌트) 정보 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_tenant (
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 ID (예: TENANT001)',
    tenant_nm VARCHAR(100) NOT NULL COMMENT '고객사명',
    tenant_desc VARCHAR(500) COMMENT '고객사 설명',
    contact_name VARCHAR(100) COMMENT '담당자명',
    contact_email VARCHAR(100) COMMENT '담당자 이메일',
    contact_phone VARCHAR(20) COMMENT '담당자 전화번호',
    subscription_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '구독 상태 (ACTIVE/INACTIVE/SUSPENDED)',
    max_users INT DEFAULT 100 COMMENT '최대 사용자 수',
    current_users INT DEFAULT 0 COMMENT '현재 사용자 수',
    storage_limit BIGINT DEFAULT 1099511627776 COMMENT '최대 저장소 용량 (1TB)',
    used_storage BIGINT DEFAULT 0 COMMENT '사용 중인 저장소 용량',
    subscription_start_dt DATE COMMENT '구독 시작일',
    subscription_end_dt DATE COMMENT '구독 종료일',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='고객사(테넌트) 정보';

-- ============================================================================
-- 2. 사용자 테이블 (모든 고객사의 사용자 통합 관리)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_user (
    user_id VARCHAR(50) NOT NULL COMMENT '사용자 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드 (테넌트)',
    user_nm VARCHAR(100) NOT NULL COMMENT '사용자명',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (암호화)',
    email VARCHAR(255) NOT NULL COMMENT '이메일 (암호화)',
    phone VARCHAR(255) COMMENT '전화번호 (암호화)',
    address VARCHAR(255) COMMENT '주소 (암호화)',
    dept_cd VARCHAR(50) COMMENT '부서코드',
    dept_nm VARCHAR(100) COMMENT '부서명',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부 (Y/N)',
    last_login_dt DATETIME COMMENT '마지막 로그인 일시',
    password_change_dt DATETIME COMMENT '비밀번호 변경일',
    login_fail_count INT DEFAULT 0 COMMENT '로그인 실패 횟수',
    account_locked_yn CHAR(1) DEFAULT 'N' COMMENT '계정 잠금 여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (user_id, tenant_id),
    UNIQUE KEY uk_email_tenant (email, tenant_id),
    CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_use_yn (use_yn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정보';

-- ============================================================================
-- 3. 역할(권한 그룹) 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_role (
    role_cd VARCHAR(50) NOT NULL COMMENT '역할 코드',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    role_nm VARCHAR(100) NOT NULL COMMENT '역할명',
    role_desc VARCHAR(500) COMMENT '역할 설명',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (role_cd, tenant_id),
    CONSTRAINT fk_role_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할 정보';

-- ============================================================================
-- 4. 권한 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_permission (
    perm_cd VARCHAR(50) NOT NULL COMMENT '권한 코드',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    perm_nm VARCHAR(100) NOT NULL COMMENT '권한명',
    perm_desc VARCHAR(500) COMMENT '권한 설명',
    resource_url VARCHAR(255) COMMENT '리소스 URL 패턴',
    http_method VARCHAR(10) COMMENT 'HTTP 메소드 (GET, POST, PUT, DELETE)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (perm_cd, tenant_id),
    CONSTRAINT fk_permission_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_resource_url (resource_url)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 정보';

-- ============================================================================
-- 5. 역할-권한 매핑 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_role_permission (
    role_cd VARCHAR(50) NOT NULL COMMENT '역할 코드',
    perm_cd VARCHAR(50) NOT NULL COMMENT '권한 코드',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    PRIMARY KEY (role_cd, perm_cd, tenant_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_cd, tenant_id) REFERENCES tb_role(role_cd, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permission_perm FOREIGN KEY (perm_cd, tenant_id) REFERENCES tb_permission(perm_cd, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할-권한 매핑';

-- ============================================================================
-- 6. 사용자-역할 매핑 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_user_role (
    user_id VARCHAR(50) NOT NULL COMMENT '사용자 ID',
    role_cd VARCHAR(50) NOT NULL COMMENT '역할 코드',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    PRIMARY KEY (user_id, role_cd, tenant_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id, tenant_id) REFERENCES tb_user(user_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_cd, tenant_id) REFERENCES tb_role(role_cd, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자-역할 매핑';

-- ============================================================================
-- 7. 메뉴 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_menu (
    menu_id VARCHAR(50) NOT NULL COMMENT '메뉴 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    menu_nm VARCHAR(100) NOT NULL COMMENT '메뉴명',
    menu_url VARCHAR(255) COMMENT '메뉴 URL',
    menu_icon VARCHAR(100) COMMENT '메뉴 아이콘 (Font Awesome)',
    sort_order INT DEFAULT 0 COMMENT '정렬 순서',
    parent_menu_id VARCHAR(50) COMMENT '부모 메뉴 ID (계층 구조)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (menu_id, tenant_id),
    CONSTRAINT fk_menu_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='메뉴 정보';

-- ============================================================================
-- 8. 역할-메뉴 매핑 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_role_menu (
    role_cd VARCHAR(50) NOT NULL COMMENT '역할 코드',
    menu_id VARCHAR(50) NOT NULL COMMENT '메뉴 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    PRIMARY KEY (role_cd, menu_id, tenant_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_cd, tenant_id) REFERENCES tb_role(role_cd, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id, tenant_id) REFERENCES tb_menu(menu_id, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할-메뉴 매핑';

-- ============================================================================
-- 8-1. 암호화 키 저장 테이블 ★ NEW! (개인정보 암호화용)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_encryption_key (
    key_id VARCHAR(50) NOT NULL COMMENT '암호화 키 ID (자동 생성)',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    key_name VARCHAR(100) NOT NULL COMMENT '키 이름 (버전 관리용, ex: AES-256-GCM-v1)',
    encrypted_key LONGTEXT NOT NULL COMMENT 'Base64 인코딩된 암호화 키 (마스터 키로 암호화됨)',
    algorithm VARCHAR(50) NOT NULL COMMENT '암호화 알고리즘 (AES-256-GCM)',
    key_size INT NOT NULL COMMENT '키 길이 (비트, ex: 256)',
    is_active CHAR(1) DEFAULT 'Y' COMMENT '활성 여부 (Y/N) - 로테이션 시 N으로 변경',
    rotation_policy VARCHAR(100) COMMENT '키 로테이션 정책 (예: 90days, manual)',
    rotated_dt DATETIME COMMENT '마지막 로테이션 일시',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (key_id),
    UNIQUE KEY uk_tenant_active (tenant_id, is_active),
    CONSTRAINT fk_encryption_key_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_is_active (is_active),
    INDEX idx_algorithm (algorithm)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='암호화 키 저장 테이블 (테넌트별 개인정보 암호화용)';

-- ============================================================================
-- 9. 과정 테이블 (Level 1 - Course)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_course (
    course_id VARCHAR(50) NOT NULL COMMENT '과정 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    course_nm VARCHAR(200) NOT NULL COMMENT '과정명',
    course_desc VARCHAR(1000) COMMENT '과정 설명',
    instructor_nm VARCHAR(100) COMMENT '강사명',
    start_dt DATE COMMENT '시작일',
    end_dt DATE COMMENT '종료일',
    max_students INT DEFAULT 50 COMMENT '최대 수강 인원',
    current_students INT DEFAULT 0 COMMENT '현재 수강 인원',
    status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '상태 (DRAFT/OPEN/CLOSED/COMPLETED)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (course_id, tenant_id),
    CONSTRAINT fk_course_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='과정 정보 (전체 교육 과정)';

-- ============================================================================
-- 10. 강의(단원) 테이블 (독립적 - 과정과 분리) ★ 수정
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_lecture (
    lecture_id VARCHAR(50) NOT NULL COMMENT '강의(단원) ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    lecture_nm VARCHAR(200) NOT NULL COMMENT '강의명 (단원명)',
    lecture_desc VARCHAR(1000) COMMENT '강의 설명',
    duration_minutes INT COMMENT '강의 총 시간 (분)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (lecture_id, tenant_id),
    CONSTRAINT fk_lecture_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='강의(단원) 정보 (과정과 독립적으로 관리)';

-- ============================================================================
-- 10-1. 과정-강의 매핑 테이블 (M:N 관계) ★ NEW!
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_course_lecture (
    course_id VARCHAR(50) NOT NULL COMMENT '과정 ID',
    lecture_id VARCHAR(50) NOT NULL COMMENT '강의(단원) ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    lecture_order INT DEFAULT 0 COMMENT '과정 내 강의 순서',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (course_id, lecture_id, tenant_id),
    CONSTRAINT fk_course_lecture_course FOREIGN KEY (course_id, tenant_id) REFERENCES tb_course(course_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_course_lecture_lecture FOREIGN KEY (lecture_id, tenant_id) REFERENCES tb_lecture(lecture_id, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_course_id (course_id),
    INDEX idx_lecture_id (lecture_id),
    INDEX idx_lecture_order (lecture_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='과정-강의 매핑 (과정 내 강의 구성)';

-- ============================================================================
-- 11. 콘텐츠(차시) 테이블 (독립적 - 강의에서 분리)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_content (
    content_id VARCHAR(50) NOT NULL COMMENT '콘텐츠(차시) ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    content_nm VARCHAR(200) NOT NULL COMMENT '콘텐츠 제목 (차시 제목)',
    content_type VARCHAR(20) COMMENT '콘텐츠 타입 (VIDEO/DOCUMENT/QUIZ/ASSIGNMENT)',
    content_url VARCHAR(500) COMMENT '콘텐츠 URL 또는 경로',
    content_desc VARCHAR(1000) COMMENT '콘텐츠 설명',
    duration_minutes INT COMMENT '학습 시간 (분)',
    use_yn CHAR(1) DEFAULT 'Y' COMMENT '사용여부',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (content_id, tenant_id),
    CONSTRAINT fk_content_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='콘텐츠(차시) 정보 (강의와 독립적으로 관리)';

-- ============================================================================
-- 11-1. 강의-콘텐츠 매핑 테이블 (M:N 관계) ★ NEW!
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_lecture_content (
    lecture_id VARCHAR(50) NOT NULL COMMENT '강의(단원) ID',
    content_id VARCHAR(50) NOT NULL COMMENT '콘텐츠(차시) ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    content_order INT DEFAULT 0 COMMENT '강의 내 차시 순서',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (lecture_id, content_id, tenant_id),
    CONSTRAINT fk_lecture_content_lecture FOREIGN KEY (lecture_id, tenant_id) REFERENCES tb_lecture(lecture_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_lecture_content_content FOREIGN KEY (content_id, tenant_id) REFERENCES tb_content(content_id, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_lecture_id (lecture_id),
    INDEX idx_content_id (content_id),
    INDEX idx_content_order (content_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='강의-콘텐츠 매핑 (강의 내 차시 구성)';

-- ============================================================================
-- 12. 수강 정보 테이블
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_enrollment (
    enrollment_id VARCHAR(50) NOT NULL COMMENT '수강 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    user_id VARCHAR(50) NOT NULL COMMENT '사용자 ID',
    course_id VARCHAR(50) NOT NULL COMMENT '과정 ID',
    enrollment_status VARCHAR(20) DEFAULT 'ENROLL' COMMENT '수강 상태 (ENROLL/PROGRESS/COMPLETED/DROPOUT)',
    completion_rate FLOAT DEFAULT 0.0 COMMENT '수강률 (0~100)',
    enrollment_dt DATETIME COMMENT '수강 시작일',
    completion_dt DATETIME COMMENT '수강 완료일',
    score FLOAT COMMENT '성적',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (enrollment_id, tenant_id),
    UNIQUE KEY uk_user_course_tenant (user_id, course_id, tenant_id),
    CONSTRAINT fk_enrollment_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id, tenant_id) REFERENCES tb_user(user_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id, tenant_id) REFERENCES tb_course(course_id, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_course_id (course_id),
    INDEX idx_enrollment_status (enrollment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='수강 정보';

-- ============================================================================
-- 13. 학습 진행 현황 테이블 (콘텐츠 별 진도)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_learning_progress (
    progress_id VARCHAR(50) NOT NULL COMMENT '학습 진행 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    enrollment_id VARCHAR(50) NOT NULL COMMENT '수강 ID',
    user_id VARCHAR(50) NOT NULL COMMENT '사용자 ID',
    course_id VARCHAR(50) NOT NULL COMMENT '과정 ID',
    lecture_id VARCHAR(50) NOT NULL COMMENT '강의(단원) ID (추가됨)',
    content_id VARCHAR(50) NOT NULL COMMENT '콘텐츠(차시) ID',
    completion_status VARCHAR(20) DEFAULT 'NOT_STARTED' COMMENT '완료 상태 (NOT_STARTED/IN_PROGRESS/COMPLETED)',
    viewing_time INT DEFAULT 0 COMMENT '시청 시간 (초)',
    completion_rate FLOAT DEFAULT 0.0 COMMENT '콘텐츠 완료율',
    start_dt DATETIME COMMENT '학습 시작일',
    complete_dt DATETIME COMMENT '학습 완료일',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    upd_dt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    PRIMARY KEY (progress_id, tenant_id),
    UNIQUE KEY uk_user_content_tenant (user_id, content_id, tenant_id),
    CONSTRAINT fk_progress_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_enrollment FOREIGN KEY (enrollment_id, tenant_id) REFERENCES tb_enrollment(enrollment_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_lecture FOREIGN KEY (lecture_id, tenant_id) REFERENCES tb_lecture(lecture_id, tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_progress_content FOREIGN KEY (content_id, tenant_id) REFERENCES tb_content(content_id, tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_lecture_id (lecture_id),
    INDEX idx_content_id (content_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습 진행 현황 (콘텐츠별)';

-- ============================================================================
-- 14. 감사 로그 테이블 (모든 중요 변경사항 기록)
-- ============================================================================
CREATE TABLE IF NOT EXISTS tb_audit_log (
    audit_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '감사 로그 ID',
    tenant_id VARCHAR(50) NOT NULL COMMENT '고객사 고유 코드',
    user_id VARCHAR(50) COMMENT '사용자 ID',
    action_type VARCHAR(50) NOT NULL COMMENT '동작 타입 (CREATE/READ/UPDATE/DELETE/LOGIN)',
    target_entity VARCHAR(100) COMMENT '대상 엔티티 (User/Course/Lecture/Content/Enrollment)',
    target_id VARCHAR(50) COMMENT '대상 ID',
    old_value LONGTEXT COMMENT '변경 전 값',
    new_value LONGTEXT COMMENT '변경 후 값',
    description VARCHAR(500) COMMENT '설명',
    ip_address VARCHAR(50) COMMENT '접근 IP 주소',
    user_agent VARCHAR(255) COMMENT '사용자 에이전트',
    reg_dt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일',
    CONSTRAINT fk_audit_log_tenant FOREIGN KEY (tenant_id) REFERENCES tb_tenant(tenant_id) ON DELETE CASCADE,
    INDEX idx_tenant (tenant_id),
    INDEX idx_user_id (user_id),
    INDEX idx_action_type (action_type),
    INDEX idx_reg_dt (reg_dt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='감사 로그';

SET FOREIGN_KEY_CHECKS=1;

-- ============================================================================
-- 인덱스 정리 및 통계 업데이트
-- ============================================================================
ANALYZE TABLE tb_tenant;
ANALYZE TABLE tb_user;
ANALYZE TABLE tb_role;
ANALYZE TABLE tb_permission;
ANALYZE TABLE tb_role_permission;
ANALYZE TABLE tb_user_role;
ANALYZE TABLE tb_menu;
ANALYZE TABLE tb_role_menu;
ANALYZE TABLE tb_encryption_key;
ANALYZE TABLE tb_course;
ANALYZE TABLE tb_course_lecture;
ANALYZE TABLE tb_lecture;
ANALYZE TABLE tb_content;
ANALYZE TABLE tb_lecture_content;
ANALYZE TABLE tb_enrollment;
ANALYZE TABLE tb_learning_progress;
ANALYZE TABLE tb_audit_log;
