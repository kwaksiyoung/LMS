package kr.co.lms.mapper;

import kr.co.lms.vo.ContentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 콘텐츠 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 */
@Mapper
public interface ContentMapper {
    
    /**
     * 콘텐츠 조회 (ID로)
     */
    ContentVO selectContent(@Param("contentId") String contentId);
    
    /**
     * 콘텐츠 목록 조회
     */
    List<ContentVO> selectContentList(ContentVO contentVO);
    
    /**
     * 콘텐츠 등록
     */
    int insertContent(ContentVO contentVO);
    
    /**
     * 콘텐츠 수정
     */
    int updateContent(ContentVO contentVO);
    
    /**
     * 콘텐츠 삭제 (테넌트ID 함께 전달 필수)
     */
    int deleteContent(ContentVO contentVO);
    
    /**
     * 콘텐츠 수 조회
     */
    int selectContentCount(ContentVO contentVO);
    
    /**
     * 과정별 콘텐츠 조회
     */
    List<ContentVO> selectContentsByCourseId(@Param("courseId") String courseId);
}
