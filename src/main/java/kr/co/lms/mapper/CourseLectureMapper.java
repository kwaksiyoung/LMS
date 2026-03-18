package kr.co.lms.mapper;

import kr.co.lms.vo.CourseLectureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 과정-강의 매핑 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 * 과정과 강의 간의 M:N 관계 관리
 */
@Mapper
public interface CourseLectureMapper {
    
    /**
     * 과정별 강의 목록 조회
     */
    List<CourseLectureVO> selectLecturesByCourseId(@Param("courseId") String courseId, @Param("tenantId") String tenantId);
    
    /**
     * 강의별 과정 목록 조회
     */
    List<CourseLectureVO> selectCoursesByLectureId(@Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
    
    /**
     * 과정-강의 매핑 목록 조회
     */
    List<CourseLectureVO> selectCourseLectureList(CourseLectureVO courseLectureVO);
    
    /**
     * 과정-강의 매핑 등록
     */
    int insertCourseLecture(CourseLectureVO courseLectureVO);
    
    /**
     * 과정-강의 매핑 수정
     */
    int updateCourseLecture(CourseLectureVO courseLectureVO);
    
    /**
     * 과정별 강의 매핑 삭제
     */
    int deleteByCourseId(@Param("courseId") String courseId, @Param("tenantId") String tenantId);
    
    /**
     * 강의별 과정 매핑 삭제
     */
    int deleteByLectureId(@Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
    
    /**
     * 과정-강의 단일 매핑 삭제
     */
    int deleteCourseLecture(@Param("courseId") String courseId, @Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
    
    /**
     * 과정별 강의 수 조회
     */
    int selectLectureCountByCourseId(@Param("courseId") String courseId, @Param("tenantId") String tenantId);
}
