package kr.co.lms.service.impl;

import kr.co.lms.mapper.ContentMapper;
import kr.co.lms.mapper.LectureContentMapper;
import kr.co.lms.mapper.LectureMapper;
import kr.co.lms.service.LectureService;
import kr.co.lms.vo.ContentVO;
import kr.co.lms.vo.LectureContentVO;
import kr.co.lms.vo.LectureVO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 강의(단원) Service 구현
 *
 * 강의 관련 비즈니스 로직 처리
 */
@Service
@Transactional
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private static final Logger logger = LoggerFactory.getLogger(LectureServiceImpl.class);

  private final LectureMapper lectureMapper;
  private final LectureContentMapper lectureContentMapper;
  private final ContentMapper contentMapper;

  @Override
  @Transactional(readOnly = true)
  public LectureVO selectLecture(String lectureId) {
    logger.debug("강의 조회: lectureId={}", lectureId);
    return lectureMapper.selectLecture(lectureId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<LectureVO> selectLectureList(LectureVO lectureVO) {
    logger.debug("강의 목록 조회");
    return lectureMapper.selectLectureList(lectureVO);
  }

  @Override
  @Transactional(readOnly = true)
  public LectureVO selectLectureWithContents(String lectureId, String tenantId) {
    logger.debug("강의 상세 조회 (콘텐츠 포함): lectureId={}", lectureId);

    // 1. 강의 정보 조회
    LectureVO lecture = lectureMapper.selectLecture(lectureId);
    if (lecture == null) {
      logger.warn("강의를 찾을 수 없습니다: lectureId={}", lectureId);
      return null;
    }

    // 2. 강의-콘텐츠 매핑 조회
    List<LectureContentVO> lectureContents =
        lectureContentMapper.selectContentsByLectureId(lectureId, tenantId);

    // 3. 각 콘텐츠 상세 정보 조회
    List<ContentVO> contents = new ArrayList<>();
    for (LectureContentVO lc : lectureContents) {
      ContentVO content = contentMapper.selectContent(lc.getContentId());
      if (content != null) {
        contents.add(content);
      }
    }

    // 4. 강의에 콘텐츠 목록 설정
    lecture.setContents(contents);
    lecture.setContentCount(contents.size());

    logger.debug("강의 상세 조회 완료: lectureId={}, contentCount={}", lectureId, contents.size());
    return lecture;
  }

  @Override
  @Transactional(readOnly = true)
  public List<ContentVO> selectContentsByLectureId(String lectureId, String tenantId) {
    logger.debug("강의별 콘텐츠 조회: lectureId={}", lectureId);

    List<LectureContentVO> lectureContents =
        lectureContentMapper.selectContentsByLectureId(lectureId, tenantId);

    List<ContentVO> contents = new ArrayList<>();
    for (LectureContentVO lc : lectureContents) {
      ContentVO content = contentMapper.selectContent(lc.getContentId());
      if (content != null) {
        contents.add(content);
      }
    }

    logger.debug("강의별 콘텐츠 조회 완료: lectureId={}, count={}", lectureId, contents.size());
    return contents;
  }

  @Override
  public int insertLecture(LectureVO lectureVO) {
    logger.info("강의 등록: lectureId={}, lectureNm={}", lectureVO.getLectureId(), lectureVO.getLectureNm());

    int result = lectureMapper.insertLecture(lectureVO);
    if (result > 0) {
      logger.info("강의 등록 성공: lectureId={}", lectureVO.getLectureId());
    } else {
      logger.warn("강의 등록 실패: lectureId={}", lectureVO.getLectureId());
    }
    return result;
  }

  @Override
  public int updateLecture(LectureVO lectureVO) {
    logger.info("강의 수정: lectureId={}", lectureVO.getLectureId());

    int result = lectureMapper.updateLecture(lectureVO);
    if (result > 0) {
      logger.info("강의 수정 성공: lectureId={}", lectureVO.getLectureId());
    } else {
      logger.warn("강의 수정 실패: lectureId={}", lectureVO.getLectureId());
    }
    return result;
  }

  @Override
  public int deleteLecture(String lectureId) {
    logger.info("강의 삭제: lectureId={}", lectureId);

    int result = lectureMapper.deleteLecture(lectureId);
    if (result > 0) {
      logger.info("강의 삭제 성공: lectureId={}", lectureId);
    } else {
      logger.warn("강의 삭제 실패: lectureId={}", lectureId);
    }
    return result;
  }

  @Override
  public int addContentToLecture(String lectureId, String contentId, String tenantId, Integer contentOrder) {
    logger.info("강의에 콘텐츠 추가: lectureId={}, contentId={}", lectureId, contentId);

    LectureContentVO lectureContent = new LectureContentVO(lectureId, contentId, tenantId, contentOrder);
    int result = lectureContentMapper.insertLectureContent(lectureContent);

    if (result > 0) {
      logger.info("강의-콘텐츠 매핑 성공: lectureId={}, contentId={}", lectureId, contentId);
    } else {
      logger.warn("강의-콘텐츠 매핑 실패: lectureId={}, contentId={}", lectureId, contentId);
    }
    return result;
  }

  @Override
  public int addContentToLecture(String lectureId, String contentId, String tenantId, Integer contentOrder,
                                  String lectureContentTitle, String lectureContentDesc) {
    logger.info("강의에 콘텐츠 추가 (차시 제목 포함): lectureId={}, contentId={}, title={}", 
        lectureId, contentId, lectureContentTitle);

    LectureContentVO lectureContent = new LectureContentVO(lectureId, contentId, tenantId, contentOrder);
    lectureContent.setLectureContentTitle(lectureContentTitle);
    lectureContent.setLectureContentDesc(lectureContentDesc);
    
    int result = lectureContentMapper.insertLectureContent(lectureContent);

    if (result > 0) {
      logger.info("강의-콘텐츠 매핑 성공: lectureId={}, contentId={}, title={}", 
          lectureId, contentId, lectureContentTitle);
    } else {
      logger.warn("강의-콘텐츠 매핑 실패: lectureId={}, contentId={}", lectureId, contentId);
    }
    return result;
  }

  @Override
  public int removeContentFromLecture(String lectureId, String contentId, String tenantId) {
    logger.info("강의에서 콘텐츠 제거: lectureId={}, contentId={}", lectureId, contentId);

    int result = lectureContentMapper.deleteLectureContent(lectureId, contentId, tenantId);

    if (result > 0) {
      logger.info("강의-콘텐츠 매핑 삭제 성공: lectureId={}, contentId={}", lectureId, contentId);
    } else {
      logger.warn("강의-콘텐츠 매핑 삭제 실패: lectureId={}, contentId={}", lectureId, contentId);
    }
    return result;
  }

  /**
   * 강의의 콘텐츠 순서 변경
   * 특정 콘텐츠의 순서를 변경합니다.
   */
  @Override
  public int reorderContent(String lectureId, String contentId, String tenantId, Integer newOrder) {
    logger.info("콘텐츠 순서 변경: lectureId={}, contentId={}, newOrder={}", lectureId, contentId, newOrder);

    LectureContentVO lectureContent = new LectureContentVO(lectureId, contentId, tenantId, newOrder);
    int result = lectureContentMapper.updateLectureContent(lectureContent);

    if (result > 0) {
      logger.info("콘텐츠 순서 변경 성공: lectureId={}, contentId={}, newOrder={}", lectureId, contentId, newOrder);
    } else {
      logger.warn("콘텐츠 순서 변경 실패: lectureId={}, contentId={}", lectureId, contentId);
    }
    return result;
  }

  /**
   * 강의에 다중 콘텐츠 추가 (배치 작업)
   * 여러 개의 콘텐츠를 한 번에 추가합니다.
   */
  @Override
  public int batchAddContents(String lectureId, String tenantId, List<String> contentIds) {
    logger.info("강의에 콘텐츠 일괄 추가: lectureId={}, contentCount={}", lectureId, contentIds.size());

    int totalCount = 0;
    int order = 0;

    for (String contentId : contentIds) {
      int result = addContentToLecture(lectureId, contentId, tenantId, order);
      if (result > 0) {
        totalCount += result;
      }
      order++;
    }

    logger.info("콘텐츠 일괄 추가 완료: lectureId={}, addedCount={}", lectureId, totalCount);
    return totalCount;
  }

  /**
   * 콘텐츠 개수를 포함한 강의 목록 조회
   * 각 강의의 콘텐츠 개수를 함께 조회합니다.
   */
  @Override
  @Transactional(readOnly = true)
  public List<LectureVO> selectLectureListWithContentCount(LectureVO lectureVO) {
    logger.debug("콘텐츠 개수 포함 강의 목록 조회");

    List<LectureVO> lectureList = lectureMapper.selectLectureList(lectureVO);

    // 각 강의별 콘텐츠 개수 설정
    for (LectureVO lecture : lectureList) {
      List<LectureContentVO> contents = lectureContentMapper.selectContentsByLectureId(
          lecture.getLectureId(), lecture.getTenantId());
      lecture.setContentCount(contents.size());
    }

    logger.debug("콘텐츠 개수 포함 강의 목록 조회 완료: count={}", lectureList.size());
    return lectureList;
  }

  /**
   * 최적화된 강의 + 콘텐츠 조회 (LEFT JOIN - N+1 쿼리 해결)
   * 단일 LEFT JOIN 쿼리로 강의와 콘텐츠를 한 번에 조회합니다.
   * 기존: 강의 1회 + 매핑 1회 + 콘텐츠 N회 = N+2회
   * 최적화: 1회
   */
  @Override
  @Transactional(readOnly = true)
  public LectureVO selectLectureWithContentsOptimized(String lectureId) {
    logger.debug("최적화된 강의 조회 (LEFT JOIN): lectureId={}", lectureId);
    LectureVO lecture = lectureMapper.selectLectureWithContentsOptimized(lectureId);
    
    if (lecture != null && lecture.getContents() != null) {
      lecture.setContentCount(lecture.getContents().size());
      logger.debug("최적화된 강의 조회 완료: lectureId={}, contentCount={}", lectureId, lecture.getContents().size());
    }
    
    return lecture;
  }

  /**
   * 검색/필터링/페이징을 포함한 강의 목록 조회
   * 
   * 파라미터:
   * - searchKeyword: 검색어 (강의명, 설명)
   * - lectureType: 차시 유형 필터 (REQUIRED/OPTIONAL)
   * - useYn: 사용여부 필터
   * - pageSize: 페이지당 항목 수 (기본값: 10)
   * - currentPage: 현재 페이지 (1부터 시작)
   */
  @Override
  @Transactional(readOnly = true)
  public List<LectureVO> selectLectureListWithSearch(LectureVO searchVO) {
    logger.debug("검색/필터링/페이징 강의 목록 조회: keyword={}, lectureType={}, page={}",
        searchVO.getSearchKeyword(), searchVO.getLectureType(), searchVO.getCurrentPage());

    // lectureTypeFilter를 lectureType으로 매핑 (필터링용)
    if (searchVO.getLectureTypeFilter() != null) {
      searchVO.setLectureType(searchVO.getLectureTypeFilter());
    }

    // 페이징 계산
    if (searchVO.getCurrentPage() != null && searchVO.getPageSize() != null) {
      int startRow = (searchVO.getCurrentPage() - 1) * searchVO.getPageSize();
      searchVO.setStartRow(startRow);
    } else {
      // 기본값: 첫 페이지
      searchVO.setCurrentPage(1);
      searchVO.setPageSize(10);
      searchVO.setStartRow(0);
    }

    // 총 개수 조회 (페이징 정보 설정용)
    int totalCount = lectureMapper.selectLectureListWithSearchCount(searchVO);
    searchVO.setTotalCount(totalCount);

    // 페이징된 목록 조회
    List<LectureVO> lectureList = lectureMapper.selectLectureListWithSearch(searchVO);

    // 각 강의별 콘텐츠 개수 설정
    for (LectureVO lecture : lectureList) {
      List<LectureContentVO> contents = lectureContentMapper.selectContentsByLectureId(
          lecture.getLectureId(), lecture.getTenantId());
      lecture.setContentCount(contents.size());
    }

    logger.debug("검색/필터링/페이징 강의 목록 조회 완료: totalCount={}, pageCount={}, returnedCount={}",
        totalCount, searchVO.getTotalPages(), lectureList.size());

    return lectureList;
  }

  /**
   * 검색/필터링 조건에 맞는 강의 총 개수 조회 (페이징용)
   * 
   * 파라미터:
   * - searchKeyword: 검색어 (강의명, 설명)
   * - lectureType: 차시 유형 필터 (REQUIRED/OPTIONAL)
   * - useYn: 사용여부 필터
   */
  @Override
  @Transactional(readOnly = true)
  public int selectLectureListWithSearchCount(LectureVO searchVO) {
    logger.debug("검색/필터링 강의 총 개수 조회: keyword={}, lectureType={}",
        searchVO.getSearchKeyword(), searchVO.getLectureTypeFilter());
    
    // lectureTypeFilter를 lectureType으로 매핑 (필터링용)
    if (searchVO.getLectureTypeFilter() != null) {
      searchVO.setLectureType(searchVO.getLectureTypeFilter());
    }
    
    int totalCount = lectureMapper.selectLectureListWithSearchCount(searchVO);
    logger.debug("검색/필터링 강의 총 개수 조회 완료: totalCount={}", totalCount);
    
    return totalCount;
  }
}
