package kr.co.lms.service.impl;

import kr.co.lms.mapper.CourseMapper;
import kr.co.lms.service.CourseService;
import kr.co.lms.vo.CourseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 과정 관리 Service 구현
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseMapper courseMapper;

    /**
     * 과정 조회 (ID로)
     */
    @Override
    @Transactional(readOnly = true)
    public CourseVO selectCourse(String courseId) {
        logger.debug("과정 조회: courseId={}", courseId);
        return courseMapper.selectCourse(courseId);
    }

    /**
     * 과정 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseVO> selectCourseList(CourseVO courseVO) {
        logger.debug("과정 목록 조회");
        return courseMapper.selectCourseList(courseVO);
    }

    /**
     * 과정 등록
     */
    @Override
    public int insertCourse(CourseVO courseVO) {
        logger.info("과정 등록: courseId={}, courseNm={}", courseVO.getCourseId(), courseVO.getCourseNm());
        int result = courseMapper.insertCourse(courseVO);
        if (result > 0) {
            logger.info("과정 등록 성공: courseId={}", courseVO.getCourseId());
        } else {
            logger.warn("과정 등록 실패: courseId={}", courseVO.getCourseId());
        }
        return result;
    }

    /**
     * 과정 수정
     */
    @Override
    public int updateCourse(CourseVO courseVO) {
        logger.info("과정 수정: courseId={}", courseVO.getCourseId());
        int result = courseMapper.updateCourse(courseVO);
        if (result > 0) {
            logger.info("과정 수정 성공: courseId={}", courseVO.getCourseId());
        } else {
            logger.warn("과정 수정 실패: courseId={}", courseVO.getCourseId());
        }
        return result;
    }

    /**
     * 과정 삭제
     */
    @Override
    public int deleteCourse(String courseId) {
        logger.info("과정 삭제: courseId={}", courseId);
        int result = courseMapper.deleteCourse(courseId);
        if (result > 0) {
            logger.info("과정 삭제 성공: courseId={}", courseId);
        } else {
            logger.warn("과정 삭제 실패: courseId={}", courseId);
        }
        return result;
    }

    /**
     * 과정 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectCourseCount(CourseVO courseVO) {
        logger.debug("과정 수 조회");
        return courseMapper.selectCourseCount(courseVO);
    }

    /**
     * 강사별 과정 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<CourseVO> selectCourseListByInstructor(String instructorId) {
        logger.debug("강사별 과정 목록 조회: instructorId={}", instructorId);
        return courseMapper.selectCourseListByInstructor(instructorId);
    }

    /**
     * 과정 상태 업데이트
     */
    @Override
    public int updateCourseStatus(String courseId, String status) {
        logger.info("과정 상태 업데이트: courseId={}, status={}", courseId, status);
        return courseMapper.updateCourseStatus(courseId, status);
    }
}
