package kr.co.lms.service;

import kr.co.lms.vo.ContentVO;
import kr.co.lms.vo.LectureVO;

import java.util.List;

/**
 * 강의(단원) Service
 *
 * 강의 관련 비즈니스 로직 처리
 */
public interface LectureService {

  /**
   * 강의 조회 (ID로)
   */
  LectureVO selectLecture(String lectureId);

  /**
   * 강의 목록 조회
   */
  List<LectureVO> selectLectureList(LectureVO lectureVO);

  /**
   * 강의와 콘텐츠 함께 조회
   */
  LectureVO selectLectureWithContents(String lectureId, String tenantId);

  /**
   * 강의별 콘텐츠 목록 조회
   */
  List<ContentVO> selectContentsByLectureId(String lectureId, String tenantId);

  /**
   * 강의 등록
   */
  int insertLecture(LectureVO lectureVO);

  /**
   * 강의 수정
   */
  int updateLecture(LectureVO lectureVO);

  /**
   * 강의 삭제
   */
  int deleteLecture(String lectureId);

  /**
   * 강의-콘텐츠 매핑 추가
   */
  int addContentToLecture(String lectureId, String contentId, String tenantId, Integer contentOrder);

  /**
   * 강의-콘텐츠 매핑 추가 (차시 제목/설명 포함)
   */
  int addContentToLecture(String lectureId, String contentId, String tenantId, Integer contentOrder, 
                          String lectureContentTitle, String lectureContentDesc);

  /**
   * 강의-콘텐츠 매핑 제거
   */
  int removeContentFromLecture(String lectureId, String contentId, String tenantId);

  /**
   * 강의의 콘텐츠 순서 변경
   */
  int reorderContent(String lectureId, String contentId, String tenantId, Integer newOrder);

  /**
   * 강의에 다중 콘텐츠 추가 (배치 작업)
   */
  int batchAddContents(String lectureId, String tenantId, List<String> contentIds);

  /**
   * 콘텐츠 개수를 포함한 강의 목록 조회
   */
  List<LectureVO> selectLectureListWithContentCount(LectureVO lectureVO);

  /**
   * 최적화된 강의 + 콘텐츠 조회 (LEFT JOIN - N+1 쿼리 해결)
   */
  LectureVO selectLectureWithContentsOptimized(String lectureId);

  /**
   * 검색/필터링/페이징을 포함한 강의 목록 조회
   */
  List<LectureVO> selectLectureListWithSearch(LectureVO searchVO);

  /**
   * 검색/필터링 조건에 맞는 강의 총 개수 조회 (페이징용)
   */
  int selectLectureListWithSearchCount(LectureVO searchVO);
}
