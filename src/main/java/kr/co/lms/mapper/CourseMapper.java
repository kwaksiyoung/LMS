package kr.co.lms.mapper;

import kr.co.lms.vo.CourseVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 과정 Mapper (MyBatis)
 */
@Mapper
public interface CourseMapper {

    /**
     * 과정 조회 (ID로)
     */
    CourseVO selectCourse(@Param("courseId") String courseId);

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
    int deleteCourse(@Param("courseId") String courseId);

    /**
     * 과정 수 조회
     */
    int selectCourseCount(CourseVO courseVO);

    /**
     * 강사별 과정 목록 조회
     */
    List<CourseVO> selectCourseListByInstructor(@Param("instructorId") String instructorId);

    /**
     * 과정 상태 업데이트
     */
    int updateCourseStatus(@Param("courseId") String courseId, @Param("status") String status);
}
