package kr.co.lms.mapper;

import kr.co.lms.vo.LectureContentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 강의-콘텐츠 매핑 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 * 강의와 콘텐츠 간의 M:N 관계 관리
 */
@Mapper
public interface LectureContentMapper {
    
    /**
     * 강의-콘텐츠 매핑 조회 (강의별)
     */
    List<LectureContentVO> selectContentsByLectureId(@Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
    
    /**
     * 강의-콘텐츠 매핑 조회 (콘텐츠별)
     */
    List<LectureContentVO> selectLecturesByContentId(@Param("contentId") String contentId, @Param("tenantId") String tenantId);
    
    /**
     * 강의-콘텐츠 매핑 목록 조회
     */
    List<LectureContentVO> selectLectureContentList(LectureContentVO lectureContentVO);
    
    /**
     * 강의-콘텐츠 매핑 등록
     */
    int insertLectureContent(LectureContentVO lectureContentVO);
    
    /**
     * 강의-콘텐츠 매핑 수정
     */
    int updateLectureContent(LectureContentVO lectureContentVO);
    
    /**
     * 강의-콘텐츠 매핑 삭제 (강의별)
     */
    int deleteByLectureId(@Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
    
    /**
     * 강의-콘텐츠 매핑 삭제 (콘텐츠별)
     */
    int deleteByContentId(@Param("contentId") String contentId, @Param("tenantId") String tenantId);
    
    /**
     * 강의-콘텐츠 단일 삭제
     */
    int deleteLectureContent(@Param("lectureId") String lectureId, @Param("contentId") String contentId, @Param("tenantId") String tenantId);
    
    /**
     * 강의별 콘텐츠 수 조회
     */
    int selectContentCountByLectureId(@Param("lectureId") String lectureId, @Param("tenantId") String tenantId);
}
