package kr.co.lms.service.impl;

import kr.co.lms.mapper.EnrollmentMapper;
import kr.co.lms.service.EnrollmentService;
import kr.co.lms.vo.EnrollmentVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 수강 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentMapper enrollmentMapper;

    /**
     * 수강 조회 (ID로)
     */
    @Override
    @Transactional(readOnly = true)
    public EnrollmentVO selectEnrollment(String enrollmentId) {
        logger.debug("수강 조회: enrollmentId={}", enrollmentId);
        return enrollmentMapper.selectEnrollment(enrollmentId);
    }

    /**
     * 수강 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentVO> selectEnrollmentList(EnrollmentVO enrollmentVO) {
        logger.debug("수강 목록 조회: {}", enrollmentVO);
        return enrollmentMapper.selectEnrollmentList(enrollmentVO);
    }

    /**
     * 수강 등록
     */
    @Override
    public int insertEnrollment(EnrollmentVO enrollmentVO) {
        logger.info("수강 등록: enrollmentId={}", enrollmentVO.getEnrollmentId());
        int result = enrollmentMapper.insertEnrollment(enrollmentVO);
        if (result > 0) {
            logger.info("수강 등록 성공: enrollmentId={}", enrollmentVO.getEnrollmentId());
        } else {
            logger.warn("수강 등록 실패: enrollmentId={}", enrollmentVO.getEnrollmentId());
        }
        return result;
    }

    /**
     * 수강 수정
     */
    @Override
    public int updateEnrollment(EnrollmentVO enrollmentVO) {
        logger.info("수강 수정: enrollmentId={}", enrollmentVO.getEnrollmentId());
        int result = enrollmentMapper.updateEnrollment(enrollmentVO);
        if (result > 0) {
            logger.info("수강 수정 성공: enrollmentId={}", enrollmentVO.getEnrollmentId());
        } else {
            logger.warn("수강 수정 실패: enrollmentId={}", enrollmentVO.getEnrollmentId());
        }
        return result;
    }

    /**
     * 수강 삭제
     */
    @Override
    public int deleteEnrollment(String enrollmentId) {
        logger.info("수강 삭제: enrollmentId={}", enrollmentId);
        int result = enrollmentMapper.deleteEnrollment(enrollmentId);
        if (result > 0) {
            logger.info("수강 삭제 성공: enrollmentId={}", enrollmentId);
        } else {
            logger.warn("수강 삭제 실패: enrollmentId={}", enrollmentId);
        }
        return result;
    }

    /**
     * 수강 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectEnrollmentCount(EnrollmentVO enrollmentVO) {
        logger.debug("수강 수 조회");
        return enrollmentMapper.selectEnrollmentCount(enrollmentVO);
    }

    /**
     * 사용자별 수강 과정 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentVO> selectEnrollmentsByUserId(String userId) {
        logger.debug("사용자별 수강 과정 조회: userId={}", userId);
        return enrollmentMapper.selectEnrollmentsByUserId(userId);
    }

    /**
     * 과정별 수강 현황 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentVO> selectEnrollmentsByCourseId(String courseId) {
        logger.debug("과정별 수강 현황 조회: courseId={}", courseId);
        return enrollmentMapper.selectEnrollmentsByCourseId(courseId);
    }
}
