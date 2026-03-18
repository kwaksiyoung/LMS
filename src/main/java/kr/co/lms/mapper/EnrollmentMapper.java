package kr.co.lms.mapper;

import kr.co.lms.vo.EnrollmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 수강 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 */
@Mapper
public interface EnrollmentMapper {
    
    /**
     * 수강 조회 (ID로)
     */
    EnrollmentVO selectEnrollment(@Param("enrollmentId") String enrollmentId);
    
    /**
     * 수강 목록 조회
     */
    List<EnrollmentVO> selectEnrollmentList(EnrollmentVO enrollmentVO);
    
    /**
     * 수강 등록
     */
    int insertEnrollment(EnrollmentVO enrollmentVO);
    
    /**
     * 수강 수정
     */
    int updateEnrollment(EnrollmentVO enrollmentVO);
    
    /**
     * 수강 삭제
     */
    int deleteEnrollment(@Param("enrollmentId") String enrollmentId);
    
    /**
     * 수강 수 조회
     */
    int selectEnrollmentCount(EnrollmentVO enrollmentVO);
    
    /**
     * 사용자별 수강 과정 조회
     */
    List<EnrollmentVO> selectEnrollmentsByUserId(@Param("userId") String userId);
    
    /**
     * 과정별 수강 현황 조회
     */
    List<EnrollmentVO> selectEnrollmentsByCourseId(@Param("courseId") String courseId);
}
