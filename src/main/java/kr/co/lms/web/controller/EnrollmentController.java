package kr.co.lms.web.controller;

import kr.co.lms.service.EnrollmentService;
import kr.co.lms.vo.EnrollmentVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 수강 관리 Controller
 */
@Controller
@RequestMapping("/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    private final EnrollmentService enrollmentService;

    /**
     * 수강 목록 조회
     */
    @GetMapping("/list")
    public String listEnrollments(EnrollmentVO enrollmentVO, Model model) {
        logger.debug("수강 목록 조회");
        List<EnrollmentVO> enrollments = enrollmentService.selectEnrollmentList(enrollmentVO);
        model.addAttribute("enrollments", enrollments);
        return "enrollment/list";
    }

    /**
     * 수강 상세 조회
     */
    @GetMapping("/{enrollmentId}")
    public String getEnrollment(@PathVariable String enrollmentId, Model model) {
        logger.debug("수강 조회: enrollmentId={}", enrollmentId);
        EnrollmentVO enrollment = enrollmentService.selectEnrollment(enrollmentId);
        model.addAttribute("enrollment", enrollment);
        return "enrollment/detail";
    }

    /**
     * 수강 생성 폼
     */
    @GetMapping("/create")
    public String createForm() {
        logger.debug("수강 생성 폼");
        return "enrollment/create";
    }

    /**
     * 수강 등록
     */
    @PostMapping
    public String createEnrollment(EnrollmentVO enrollmentVO) {
        logger.info("수강 등록: enrollmentId={}", enrollmentVO.getEnrollmentId());
        int result = enrollmentService.insertEnrollment(enrollmentVO);
        if (result > 0) {
            logger.info("수강 등록 성공");
            return "redirect:/enrollment/list";
        } else {
            logger.warn("수강 등록 실패");
            return "enrollment/create";
        }
    }

    /**
     * 수강 수정 폼
     */
    @GetMapping("/{enrollmentId}/edit")
    public String editForm(@PathVariable String enrollmentId, Model model) {
        logger.debug("수강 수정 폼: enrollmentId={}", enrollmentId);
        EnrollmentVO enrollment = enrollmentService.selectEnrollment(enrollmentId);
        model.addAttribute("enrollment", enrollment);
        return "enrollment/edit";
    }

    /**
     * 수강 수정
     */
    @PutMapping("/{enrollmentId}")
    public String updateEnrollment(@PathVariable String enrollmentId, EnrollmentVO enrollmentVO) {
        logger.info("수강 수정: enrollmentId={}", enrollmentId);
        enrollmentVO.setEnrollmentId(enrollmentId);
        int result = enrollmentService.updateEnrollment(enrollmentVO);
        if (result > 0) {
            logger.info("수강 수정 성공");
            return "redirect:/enrollment/" + enrollmentId;
        } else {
            logger.warn("수강 수정 실패");
            return "enrollment/edit";
        }
    }

    /**
     * 수강 삭제
     */
    @DeleteMapping("/{enrollmentId}")
    public String deleteEnrollment(@PathVariable String enrollmentId) {
        logger.info("수강 삭제: enrollmentId={}", enrollmentId);
        int result = enrollmentService.deleteEnrollment(enrollmentId);
        if (result > 0) {
            logger.info("수강 삭제 성공");
        } else {
            logger.warn("수강 삭제 실패");
        }
        return "redirect:/enrollment/list";
    }

    /**
     * 사용자별 수강 과정 조회
     */
    @GetMapping("/user/{userId}")
    public String listEnrollmentsByUser(@PathVariable String userId, Model model) {
        logger.debug("사용자별 수강 과정 조회: userId={}", userId);
        List<EnrollmentVO> enrollments = enrollmentService.selectEnrollmentsByUserId(userId);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("userId", userId);
        return "enrollment/userEnrollments";
    }

    /**
     * 과정별 수강 현황 조회
     */
    @GetMapping("/course/{courseId}")
    public String listEnrollmentsByCourse(@PathVariable String courseId, Model model) {
        logger.debug("과정별 수강 현황 조회: courseId={}", courseId);
        List<EnrollmentVO> enrollments = enrollmentService.selectEnrollmentsByCourseId(courseId);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("courseId", courseId);
        return "enrollment/courseEnrollments";
    }
}
