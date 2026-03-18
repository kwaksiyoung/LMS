package kr.co.lms.service;

import kr.co.lms.vo.CourseVO;
import java.util.List;

/**
 * 과정 관리 Service 인터페이스
 */
public interface CourseService {

    /**
     * 과정 조회 (ID로)
     */
    CourseVO selectCourse(String courseId);

    /**
     * 과정 목록 조회
     */
    List<CourseVO> selectCourseList(CourseVO courseVO);

    /**
     * 과정 등록
     */
    int insertCourse(CourseVO courseVO);

    /**
     * 과정 수정
     */
    int updateCourse(CourseVO courseVO);

    /**
     * 과정 삭제
     */
    int deleteCourse(String courseId);

    /**
     * 과정 수 조회
     */
    int selectCourseCount(CourseVO courseVO);

    /**
     * 강사별 과정 목록 조회
     */
    List<CourseVO> selectCourseListByInstructor(String instructorId);

    /**
     * 과정 상태 업데이트
     */
    int updateCourseStatus(String courseId, String status);
}
