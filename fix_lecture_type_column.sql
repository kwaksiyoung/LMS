-- ============================================
-- 강의 테이블 lecture_type 컬럼 추가 스크립트
-- ============================================

-- 1. lecture_type 컬럼이 없으면 추가
ALTER TABLE tb_lecture 
ADD COLUMN IF NOT EXISTS lecture_type VARCHAR(20) DEFAULT 'REQUIRED' 
COMMENT '차시 유형 (REQUIRED: 필수, OPTIONAL: 선택)' 
AFTER duration_minutes;

-- 2. 인덱스 생성 (성능 최적화)
ALTER TABLE tb_lecture 
ADD INDEX IF NOT EXISTS idx_lecture_type (lecture_type);

-- 3. 현재 모든 강의에 기본값 'REQUIRED' 설정
UPDATE tb_lecture 
SET lecture_type = 'REQUIRED' 
WHERE lecture_type IS NULL OR lecture_type = '';

-- 4. 결과 확인
SELECT COUNT(*) as total_lectures, 
       SUM(CASE WHEN lecture_type = 'REQUIRED' THEN 1 ELSE 0 END) as required_count,
       SUM(CASE WHEN lecture_type = 'OPTIONAL' THEN 1 ELSE 0 END) as optional_count
FROM tb_lecture;

-- 5. 테이블 구조 확인
DESC tb_lecture;
