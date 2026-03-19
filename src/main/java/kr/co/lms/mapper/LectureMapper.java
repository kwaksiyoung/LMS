package kr.co.lms.mapper;

import kr.co.lms.vo.LectureVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 강의(단원) Mapper
 *
 * tb_lecture 테이블에 대한 데이터 접근 인터페이스
 */
@Mapper
public interface LectureMapper {

  /**
   * 강의 조회 (ID로)
   */
  LectureVO selectLecture(@Param("lectureId") String lectureId);

  /**
   * 강의 목록 조회
   */
  List<LectureVO> selectLectureList(LectureVO lectureVO);

  /**
   * 강의 등록
   */
  int insertLecture(LectureVO lectureVO);

  /**
   * 강의 수정
   */
  int updateLecture(LectureVO lectureVO);

  /**
   * 강의 삭제 (논리적 삭제)
   */
  int deleteLecture(@Param("lectureId") String lectureId);

  /**
   * 강의 수 조회
   */
  int selectLectureCount(LectureVO lectureVO);

  /**
   * 최적화된 강의 + 콘텐츠 조회 (LEFT JOIN - N+1 쿼리 해결)
   */
  LectureVO selectLectureWithContentsOptimized(@Param("lectureId") String lectureId);

  /**
   * 검색/필터링/페이징을 포함한 강의 목록 조회
   */
  List<LectureVO> selectLectureListWithSearch(LectureVO lectureVO);

  /**
   * 검색/필터링된 강의 수 조회 (페이징용)
   */
  int selectLectureListWithSearchCount(LectureVO lectureVO);
}
