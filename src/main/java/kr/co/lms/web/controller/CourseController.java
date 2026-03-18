package kr.co.lms.web.controller;

import kr.co.lms.service.CourseService;
import kr.co.lms.vo.CourseVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 과정 관리 Controller (JSP용)
 */
@Controller
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    /**
     * 과정 목록 페이지
     */
    @GetMapping("/list")
    public String list(Model model) {
        logger.info("과정 목록 페이지 요청");
        List<CourseVO> courseList = courseService.selectCourseList(new CourseVO());
        model.addAttribute("courseList", courseList);
        return "course/list";
    }

    /**
     * 과정 상세 조회
     */
    @GetMapping("/view")
    public String view(String courseId, Model model) {
        logger.info("과정 상세 조회: courseId={}", courseId);
        CourseVO course = courseService.selectCourse(courseId);
        model.addAttribute("course", course);
        return "course/view";
    }

    /**
     * 과정 등록 폼
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        logger.info("과정 등록 폼 요청");
        model.addAttribute("course", new CourseVO());
        return "course/form";
    }

    /**
     * 과정 등록 처리
     */
    @PostMapping("/create")
    public String create(CourseVO courseVO, HttpSession session) {
        logger.info("과정 등록 처리: courseNm={}", courseVO.getCourseNm());
        
        // 강사 ID는 세션에서 가져오기
        String instructorId = (String) session.getAttribute("userId");
        courseVO.setInstructorId(instructorId);
        
        courseService.insertCourse(courseVO);
        return "redirect:/course/list";
    }

    /**
     * 과정 수정 폼
     */
    @GetMapping("/edit")
    public String editForm(String courseId, Model model) {
        logger.info("과정 수정 폼: courseId={}", courseId);
        CourseVO course = courseService.selectCourse(courseId);
        model.addAttribute("course", course);
        return "course/form";
    }

    /**
     * 과정 수정 처리
     */
    @PostMapping("/edit")
    public String edit(CourseVO courseVO) {
        logger.info("과정 수정 처리: courseId={}", courseVO.getCourseId());
        courseService.updateCourse(courseVO);
        return "redirect:/course/view?courseId=" + courseVO.getCourseId();
    }

    /**
     * 과정 삭제
     */
    @PostMapping("/delete")
    public String delete(String courseId) {
        logger.info("과정 삭제: courseId={}", courseId);
        courseService.deleteCourse(courseId);
        return "redirect:/course/list";
    }

    /**
     * 강사의 과정 목록
     */
    @GetMapping("/mylist")
    public String myList(HttpSession session, Model model) {
        String instructorId = (String) session.getAttribute("userId");
        logger.info("강사의 과정 목록: instructorId={}", instructorId);
        List<CourseVO> courseList = courseService.selectCourseListByInstructor(instructorId);
        model.addAttribute("courseList", courseList);
        return "course/mylist";
    }

    /**
     * 과정 상태 변경
     */
    @PostMapping("/changeStatus")
    public String changeStatus(String courseId, String status) {
        logger.info("과정 상태 변경: courseId={}, status={}", courseId, status);
        courseService.updateCourseStatus(courseId, status);
        return "redirect:/course/view?courseId=" + courseId;
    }
}
