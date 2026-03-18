package kr.co.lms.service;

import kr.co.lms.vo.EnrollmentVO;
import java.util.List;

/**
 * 수강 관리 Service 인터페이스
 */
public interface EnrollmentService {
    
    /**
     * 수강 조회 (ID로)
     */
    EnrollmentVO selectEnrollment(String enrollmentId);
    
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
    int deleteEnrollment(String enrollmentId);
    
    /**
     * 수강 수 조회
     */
    int selectEnrollmentCount(EnrollmentVO enrollmentVO);
    
    /**
     * 사용자별 수강 과정 조회
     */
    List<EnrollmentVO> selectEnrollmentsByUserId(String userId);
    
    /**
     * 과정별 수강 현황 조회
     */
    List<EnrollmentVO> selectEnrollmentsByCourseId(String courseId);
}
