-- ============================================================================
-- tb_lecture_content 테이블에 lecture_content_title 필드 추가
-- ============================================================================

-- 1. 현재 테이블 구조 확인
DESCRIBE tb_lecture_content;

-- 2. lecture_content_title 컬럼이 없으면 추가 (차시별 제목)
ALTER TABLE tb_lecture_content 
ADD COLUMN IF NOT EXISTS lecture_content_title VARCHAR(200)
COMMENT '차시 제목 (예: 1. 환경 설정, 2. 기본 개념)' 
AFTER content_order;

-- 3. lecture_content_desc 컬럼 추가 (차시별 설명)
ALTER TABLE tb_lecture_content 
ADD COLUMN IF NOT EXISTS lecture_content_desc VARCHAR(1000)
COMMENT '차시 설명' 
AFTER lecture_content_title;

-- 4. 확인
DESCRIBE tb_lecture_content;

-- 5. 데이터 확인
SELECT lecture_id, content_id, content_order, lecture_content_title FROM tb_lecture_content LIMIT 5;
