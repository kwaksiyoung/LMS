-- ============================================================================
-- tb_lecture 테이블 구조 확인 및 lecture_type 컬럼 추가
-- ============================================================================

-- 1. 현재 테이블 구조 확인
DESCRIBE tb_lecture;

-- 2. lecture_type 컬럼 존재 여부 확인
SELECT COLUMN_NAME 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'tb_lecture' AND COLUMN_NAME = 'lecture_type';

-- 3. lecture_type 컬럼이 없으면 추가
ALTER TABLE tb_lecture 
ADD COLUMN IF NOT EXISTS lecture_type VARCHAR(20) DEFAULT 'REQUIRED' 
COMMENT '차시 유형 (REQUIRED: 필수, OPTIONAL: 선택)' 
AFTER duration_minutes;

-- 4. 인덱스 추가 (성능 최적화)
ALTER TABLE tb_lecture 
ADD INDEX IF NOT EXISTS idx_lecture_type (lecture_type);

-- 5. 기존 데이터 업데이트 (NULL 값을 REQUIRED로)
UPDATE tb_lecture 
SET lecture_type = 'REQUIRED' 
WHERE lecture_type IS NULL OR lecture_type = '';

-- 6. 확인
DESCRIBE tb_lecture;

-- 7. 데이터 확인
SELECT lecture_id, lecture_nm, lecture_type, use_yn FROM tb_lecture LIMIT 5;
