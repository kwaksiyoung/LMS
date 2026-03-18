package kr.co.lms.web.api.v1;

import kr.co.lms.service.ContentService;
import kr.co.lms.vo.ContentVO;
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

/**
 * 콘텐츠 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/contents                  - 콘텐츠 목록 (관리자)
 * - GET    /api/v1/contents/{contentId}      - 콘텐츠 상세
 * - POST   /api/v1/contents                  - 콘텐츠 등록 (관리자)
 * - PUT    /api/v1/contents/{contentId}      - 콘텐츠 수정 (관리자)
 * - DELETE /api/v1/contents/{contentId}      - 콘텐츠 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentApiController {

  private static final Logger logger = LoggerFactory.getLogger(ContentApiController.class);

  private final ContentService contentService;

  /**
   * 콘텐츠 목록 조회 (관리자)
   * GET /api/v1/contents?courseId=COURSE001
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<ContentVO>>> getContentList(
      @RequestParam(required = false) String courseId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("콘텐츠 목록 조회 API: courseId={}", courseId);

    List<ContentVO> contents;
    if (courseId != null && !courseId.trim().isEmpty()) {
      contents = contentService.selectContentsByCourseId(courseId);
    } else {
      ContentVO searchVO = new ContentVO();
      searchVO.setTenantId(ApiAuthUtil.getCurrentTenantId(request));
      contents = contentService.selectContentList(searchVO);
    }

    return ResponseEntity.ok(ApiResponse.success(contents));
  }

  /**
   * 콘텐츠 상세 조회
   * GET /api/v1/contents/{contentId}
   */
  @GetMapping("/{contentId}")
  public ResponseEntity<ApiResponse<ContentVO>> getContent(@PathVariable String contentId) {
    logger.info("콘텐츠 상세 조회 API: contentId={}", contentId);

    ContentVO content = contentService.selectContent(contentId);
    if (content == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("콘텐츠를 찾을 수 없습니다: " + contentId));
    }

    return ResponseEntity.ok(ApiResponse.success(content));
  }

  /**
   * 콘텐츠 등록 (관리자)
   * POST /api/v1/contents
   *
   * tenantId는 JWT에서 강제 설정
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createContent(
      @RequestBody ContentVO contentVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    contentVO.setTenantId(ApiAuthUtil.getCurrentTenantId(request));
    logger.info("콘텐츠 등록 API: contentTitle={}", contentVO.getContentTitle());

    int result = contentService.insertContent(contentVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("콘텐츠 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "콘텐츠가 등록되었습니다."));
  }

  /**
   * 콘텐츠 수정 (관리자)
   * PUT /api/v1/contents/{contentId}
   */
  @PutMapping("/{contentId}")
  public ResponseEntity<ApiResponse<Void>> updateContent(
      @PathVariable String contentId,
      @RequestBody ContentVO contentVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    contentVO.setContentId(contentId);
    contentVO.setTenantId(ApiAuthUtil.getCurrentTenantId(request));
    logger.info("콘텐츠 수정 API: contentId={}", contentId);

    int result = contentService.updateContent(contentVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 콘텐츠를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "콘텐츠가 수정되었습니다."));
  }

  /**
   * 콘텐츠 삭제 (관리자)
   * DELETE /api/v1/contents/{contentId}
   *
   * ContentService.deleteContent()는 ContentVO를 받으므로 tenantId 포함 필수
   */
  @DeleteMapping("/{contentId}")
  public ResponseEntity<ApiResponse<Void>> deleteContent(
      @PathVariable String contentId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("콘텐츠 삭제 API: contentId={}", contentId);

    ContentVO deleteVO = new ContentVO();
    deleteVO.setContentId(contentId);
    deleteVO.setTenantId(ApiAuthUtil.getCurrentTenantId(request));

    int result = contentService.deleteContent(deleteVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 콘텐츠를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "콘텐츠가 삭제되었습니다."));
  }
}
