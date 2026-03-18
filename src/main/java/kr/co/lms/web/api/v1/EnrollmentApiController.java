package kr.co.lms.web.api.v1;

import kr.co.lms.service.EnrollmentService;
import kr.co.lms.vo.EnrollmentVO;
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
 * 수강 신청 REST API 컨트롤러
 *
 * 엔드포인트:
 * - GET    /api/v1/enrollments                    - 내 수강 내역 조회
 * - POST   /api/v1/enrollments                    - 수강 신청
 * - PUT    /api/v1/enrollments/{enrollmentId}     - 수강 상태 변경
 * - DELETE /api/v1/enrollments/{enrollmentId}     - 수강 취소
 */
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentApiController {

  private static final Logger logger = LoggerFactory.getLogger(EnrollmentApiController.class);

  private final EnrollmentService enrollmentService;

  /**
   * 수강 내역 조회
   * GET /api/v1/enrollments?userId=xxx (관리자: 특정 사용자 조회 가능)
   */
  @GetMapping
  public ResponseEntity<ApiResponse<List<EnrollmentVO>>> getEnrollmentList(
      @RequestParam(required = false) String userId,
      HttpServletRequest request) {

    String currentUserId = ApiAuthUtil.getCurrentUserId(request);
    boolean isAdmin = ApiAuthUtil.isAdmin(request);

    // 관리자가 특정 사용자 조회 요청 시
    String targetUserId = (isAdmin && userId != null) ? userId : currentUserId;
    logger.info("수강 내역 조회 API: targetUserId={}", targetUserId);

    List<EnrollmentVO> enrollments = enrollmentService.selectEnrollmentsByUserId(targetUserId);
    return ResponseEntity.ok(ApiResponse.success(enrollments));
  }

  /**
   * 수강 신청
   * POST /api/v1/enrollments
   * Body: { "courseId": "COURSE001" }
   *
   * userId는 JWT에서 강제 설정 (클라이언트 조작 방지)
   */
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> enroll(
      @RequestBody EnrollmentVO enrollmentVO,
      HttpServletRequest request) {

    String userId = ApiAuthUtil.getCurrentUserId(request);

    enrollmentVO.setUserId(userId);
    enrollmentVO.setEnrollmentStatus("ENROLL");

    logger.info("수강 신청 API: userId={}, courseId={}", userId, enrollmentVO.getCourseId());

    int result = enrollmentService.insertEnrollment(enrollmentVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(ApiResponse.error("수강 신청에 실패했습니다."));
    }

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(null, "수강 신청이 완료되었습니다."));
  }

  /**
   * 수강 상태 변경
   * PUT /api/v1/enrollments/{enrollmentId}
   *
   * 본인 또는 관리자만 가능
   */
  @PutMapping("/{enrollmentId}")
  public ResponseEntity<ApiResponse<Void>> updateEnrollment(
      @PathVariable String enrollmentId,
      @RequestBody EnrollmentVO enrollmentVO,
      HttpServletRequest request) {

    EnrollmentVO existing = enrollmentService.selectEnrollment(enrollmentId);
    if (existing == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수강 정보를 찾을 수 없습니다."));
    }

    String currentUserId = ApiAuthUtil.getCurrentUserId(request);
    if (!ApiAuthUtil.isAdmin(request) && !existing.getUserId().equals(currentUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("본인 수강 정보만 수정할 수 있습니다."));
    }

    enrollmentVO.setEnrollmentId(enrollmentId);
    logger.info("수강 상태 변경 API: enrollmentId={}", enrollmentId);

    int result = enrollmentService.updateEnrollment(enrollmentVO);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수정할 수강 정보를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "수강 정보가 수정되었습니다."));
  }

  /**
   * 수강 취소
   * DELETE /api/v1/enrollments/{enrollmentId}
   *
   * 본인 또는 관리자만 가능
   */
  @DeleteMapping("/{enrollmentId}")
  public ResponseEntity<ApiResponse<Void>> cancelEnrollment(
      @PathVariable String enrollmentId,
      HttpServletRequest request) {

    EnrollmentVO existing = enrollmentService.selectEnrollment(enrollmentId);
    if (existing == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("수강 정보를 찾을 수 없습니다."));
    }

    String currentUserId = ApiAuthUtil.getCurrentUserId(request);
    if (!ApiAuthUtil.isAdmin(request) && !existing.getUserId().equals(currentUserId)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(ApiResponse.error("본인 수강만 취소할 수 있습니다."));
    }

    logger.info("수강 취소 API: enrollmentId={}", enrollmentId);

    int result = enrollmentService.deleteEnrollment(enrollmentId);
    if (result == 0) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(ApiResponse.error("취소할 수강 정보를 찾을 수 없습니다."));
    }

    return ResponseEntity.ok(ApiResponse.success(null, "수강이 취소되었습니다."));
  }
}
