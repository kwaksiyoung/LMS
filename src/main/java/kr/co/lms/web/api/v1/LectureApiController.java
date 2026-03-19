package kr.co.lms.web.api.v1;

import kr.co.lms.service.LectureService;
import kr.co.lms.vo.ContentVO;
import kr.co.lms.vo.LectureVO;
import kr.co.lms.web.api.common.ApiAuthUtil;
import kr.co.lms.web.api.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

/**
 * 강의(단원) REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/lectures              - 강의 목록 (전체 공개)
 * - GET    /api/v1/lectures/{lectureId}  - 강의 상세 (전체 공개)
 * - POST   /api/v1/lectures              - 강의 등록 (관리자)
 * - PUT    /api/v1/lectures/{lectureId}  - 강의 수정 (관리자)
 * - DELETE /api/v1/lectures/{lectureId}  - 강의 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/lectures")
@RequiredArgsConstructor
public class LectureApiController {

  private static final Logger logger = LoggerFactory.getLogger(LectureApiController.class);

  private final LectureService lectureService;

  /**
   * 강의 목록 조회 (검색/필터링/페이징 지원)
   * GET /api/v1/lectures?keyword=검색어&lectureType=REQUIRED&page=1&pageSize=10
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<LectureVO>>> getLectureList(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String lectureType,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer pageSize) {
    
    logger.info("강의 목록 조회 API: keyword={}, lectureType={}, page={}, pageSize={}", 
        keyword, lectureType, page, pageSize);

    LectureVO searchVO = new LectureVO();
    searchVO.setUseYn("Y");
    searchVO.setSearchKeyword(keyword);
    searchVO.setLectureTypeFilter(lectureType);
    searchVO.setCurrentPage(page);
    searchVO.setPageSize(pageSize);
    
    // startRow 계산 (0-based index)
    int startRow = (page - 1) * pageSize;
    searchVO.setStartRow(startRow);

    // 검색/필터링/페이징을 포함한 목록 조회
    List<LectureVO> lectures = lectureService.selectLectureListWithSearch(searchVO);
    
    return ResponseEntity.ok(ApiResponse.success(lectures));
  }

  /**
   * 강의 상세 조회 (최적화 - LEFT JOIN으로 N+1 해결)
   * GET /api/v1/lectures/{lectureId}
   */
  @GetMapping("/{lectureId}")
  public ResponseEntity<ApiResponse<LectureVO>> getLecture(
      @PathVariable String lectureId,
      HttpServletRequest request) {

    logger.info("강의 상세 조회 API: lectureId={}", lectureId);

    // LEFT JOIN으로 최적화된 조회 (N+1 문제 해결)
    LectureVO lecture = lectureService.selectLectureWithContentsOptimized(lectureId);

    if (lecture == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("강의를 찾을 수 없습니다: " + lectureId));
    }

    return ResponseEntity.ok(ApiResponse.success(lecture));
  }

  /**
   * 강의별 콘텐츠 조회
   * GET /api/v1/lectures/{lectureId}/contents
   */
  @GetMapping("/{lectureId}/contents")
  public ResponseEntity<ApiResponse<List<ContentVO>>> getLectureContents(
      @PathVariable String lectureId,
      HttpServletRequest request) {

    logger.info("강의별 콘텐츠 조회 API: lectureId={}", lectureId);

    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    List<ContentVO> contents = lectureService.selectContentsByLectureId(lectureId, tenantId);

    return ResponseEntity.ok(ApiResponse.success(contents));
  }

  /**
   * 강의 등록 (관리자)
   * POST /api/v1/lectures
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createLecture(
      @RequestBody LectureVO lectureVO,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    // 강의 ID 자동 생성 (없을 경우)
    if (lectureVO.getLectureId() == null || lectureVO.getLectureId().trim().isEmpty()) {
      lectureVO.setLectureId("LEC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    // 테넌트 ID 설정 (현재 사용자의 테넌트)
    if (lectureVO.getTenantId() == null || lectureVO.getTenantId().trim().isEmpty()) {
      lectureVO.setTenantId(ApiAuthUtil.getCurrentTenantId(request));
    }

    logger.info("강의 등록 API: lectureNm={}", lectureVO.getLectureNm());

    int result = lectureService.insertLecture(lectureVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("강의 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "강의가 등록되었습니다."));
  }

  /**
   * 강의 수정 (관리자)
   * PUT /api/v1/lectures/{lectureId}
   */
  @PutMapping("/{lectureId}")
  public ResponseEntity<ApiResponse<Void>> updateLecture(
      @PathVariable String lectureId,
      @RequestBody LectureVO lectureVO,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    lectureVO.setLectureId(lectureId);
    logger.info("강의 수정 API: lectureId={}", lectureId);

    int result = lectureService.updateLecture(lectureVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 강의를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "강의가 수정되었습니다."));
  }

  /**
   * 강의 삭제 (관리자)
   * DELETE /api/v1/lectures/{lectureId}
   */
  @DeleteMapping("/{lectureId}")
  public ResponseEntity<ApiResponse<Void>> deleteLecture(
      @PathVariable String lectureId,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("강의 삭제 API: lectureId={}", lectureId);

    int result = lectureService.deleteLecture(lectureId);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 강의를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "강의가 삭제되었습니다."));
  }

  /**
   * 강의에 콘텐츠 추가 (관리자)
   * POST /api/v1/lectures/{lectureId}/contents/{contentId}
   */
  @PostMapping("/{lectureId}/contents/{contentId}")
  public ResponseEntity<ApiResponse<Void>> addContentToLecture(
      @PathVariable String lectureId,
      @PathVariable String contentId,
      @RequestBody(required = false) java.util.Map<String, Object> requestBody,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    
    // RequestBody에서 정보 추출
    Integer contentOrder = 0;
    String lectureContentTitle = null;
    String lectureContentDesc = null;
    
    if (requestBody != null) {
      if (requestBody.containsKey("contentOrder")) {
        contentOrder = ((Number) requestBody.get("contentOrder")).intValue();
      }
      if (requestBody.containsKey("lectureContentTitle")) {
        lectureContentTitle = (String) requestBody.get("lectureContentTitle");
      }
      if (requestBody.containsKey("lectureContentDesc")) {
        lectureContentDesc = (String) requestBody.get("lectureContentDesc");
      }
    }
    
    logger.info("강의에 콘텐츠 추가 API: lectureId={}, contentId={}, title={}", 
        lectureId, contentId, lectureContentTitle);

    int result = lectureService.addContentToLecture(lectureId, contentId, tenantId, contentOrder, lectureContentTitle, lectureContentDesc);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("콘텐츠 추가에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "콘텐츠가 강의에 추가되었습니다."));
  }

  /**
   * 강의에서 콘텐츠 제거 (관리자)
   * DELETE /api/v1/lectures/{lectureId}/contents/{contentId}
   */
  @DeleteMapping("/{lectureId}/contents/{contentId}")
  public ResponseEntity<ApiResponse<Void>> removeContentFromLecture(
      @PathVariable String lectureId,
      @PathVariable String contentId,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    logger.info("강의에서 콘텐츠 제거 API: lectureId={}, contentId={}", lectureId, contentId);

    int result = lectureService.removeContentFromLecture(lectureId, contentId, tenantId);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("제거할 콘텐츠를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "콘텐츠가 강의에서 제거되었습니다."));
  }

  /**
   * 강의의 콘텐츠 순서 변경 (관리자)
   * PUT /api/v1/lectures/{lectureId}/contents/{contentId}/order
   */
  @PutMapping("/{lectureId}/contents/{contentId}/order")
  public ResponseEntity<ApiResponse<Void>> reorderContent(
      @PathVariable String lectureId,
      @PathVariable String contentId,
      @RequestParam Integer contentOrder,
      HttpServletRequest request) {

    // 권한 검증
    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    String tenantId = ApiAuthUtil.getCurrentTenantId(request);
    logger.info("콘텐츠 순서 변경 API: lectureId={}, contentId={}, newOrder={}", lectureId, contentId, contentOrder);

    int result = lectureService.reorderContent(lectureId, contentId, tenantId, contentOrder);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("순서를 변경할 콘텐츠를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "콘텐츠 순서가 변경되었습니다."));
  }
}
