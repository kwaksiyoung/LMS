package kr.co.lms.web.api.v1;

import kr.co.lms.service.CourseService;
import kr.co.lms.vo.CourseVO;
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
 * 과정 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/courses              - 과정 목록 (전체 공개)
 * - GET    /api/v1/courses/{courseId}   - 과정 상세 (전체 공개)
 * - POST   /api/v1/courses              - 과정 등록 (관리자)
 * - PUT    /api/v1/courses/{courseId}   - 과정 수정 (관리자)
 * - DELETE /api/v1/courses/{courseId}   - 과정 삭제 (관리자)
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseApiController {

  private static final Logger logger = LoggerFactory.getLogger(CourseApiController.class);

  private final CourseService courseService;

  /**
   * 과정 목록 조회
   * GET /api/v1/courses?status=ACTIVE&instructorId=admin001
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<CourseVO>>> getCourseList(
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String instructorId) {

    logger.info("과정 목록 조회 API: status={}, instructorId={}", status, instructorId);

    CourseVO searchVO = new CourseVO();
    searchVO.setStatus(status);
    searchVO.setInstructorId(instructorId);

    List<CourseVO> courses = courseService.selectCourseList(searchVO);
    return ResponseEntity.ok(ApiResponse.success(courses));
  }

  /**
   * 과정 상세 조회
   * GET /api/v1/courses/{courseId}
   */
  @GetMapping("/{courseId}")
  public ResponseEntity<ApiResponse<CourseVO>> getCourse(@PathVariable String courseId) {
    logger.info("과정 상세 조회 API: courseId={}", courseId);

    CourseVO course = courseService.selectCourse(courseId);
    if (course == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("과정을 찾을 수 없습니다: " + courseId));
    }

    return ResponseEntity.ok(ApiResponse.success(course));
  }

  /**
   * 과정 등록 (관리자)
   * POST /api/v1/courses
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> createCourse(
      @RequestBody CourseVO courseVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    // 담당자 미입력 시 현재 사용자로 설정
    if (courseVO.getInstructorId() == null || courseVO.getInstructorId().trim().isEmpty()) {
      courseVO.setInstructorId(ApiAuthUtil.getCurrentUserId(request));
    }

    logger.info("과정 등록 API: courseNm={}", courseVO.getCourseNm());

    int result = courseService.insertCourse(courseVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("과정 등록에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "과정이 등록되었습니다."));
  }

  /**
   * 과정 수정 (관리자)
   * PUT /api/v1/courses/{courseId}
   */
  @PutMapping("/{courseId}")
  public ResponseEntity<ApiResponse<Void>> updateCourse(
      @PathVariable String courseId,
      @RequestBody CourseVO courseVO,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    courseVO.setCourseId(courseId);
    logger.info("과정 수정 API: courseId={}", courseId);

    int result = courseService.updateCourse(courseVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 과정을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "과정이 수정되었습니다."));
  }

  /**
   * 과정 삭제 (관리자)
   * DELETE /api/v1/courses/{courseId}
   */
  @DeleteMapping("/{courseId}")
  public ResponseEntity<ApiResponse<Void>> deleteCourse(
      @PathVariable String courseId,
      HttpServletRequest request) {

    if (!ApiAuthUtil.isAdmin(request)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("관리자만 접근할 수 있습니다."));
    }

    logger.info("과정 삭제 API: courseId={}", courseId);

    int result = courseService.deleteCourse(courseId);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("삭제할 과정을 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "과정이 삭제되었습니다."));
  }
}
