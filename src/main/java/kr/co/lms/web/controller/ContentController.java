package kr.co.lms.web.controller;

import kr.co.lms.service.ContentService;
import kr.co.lms.vo.ContentVO;
import kr.co.lms.web.util.AuthorizationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 콘텐츠 관리 Controller
 * 
 * 권한:
 * - ROLE_ADMIN: 모든 기능 (CRUD)
 * - ROLE_STUDENT, ROLE_INSTRUCTOR: 조회만 가능
 */
@Controller
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentController {

    private static final Logger logger = LoggerFactory.getLogger(ContentController.class);

    private final ContentService contentService;
    private final AuthorizationUtil authorizationUtil;

    /**
     * 콘텐츠 목록 조회
     */
    @GetMapping("/list")
    public String listContents(ContentVO contentVO, Model model) {
        logger.debug("콘텐츠 목록 조회");
        List<ContentVO> contents = contentService.selectContentList(contentVO);
        model.addAttribute("contents", contents);
        return "content/list";
    }

    /**
     * 콘텐츠 상세 조회
     */
    @GetMapping("/{contentId}")
    public String getContent(@PathVariable String contentId, Model model) {
        logger.debug("콘텐츠 조회: contentId={}", contentId);
        ContentVO content = contentService.selectContent(contentId);
        model.addAttribute("content", content);
        return "content/detail";
    }

    /**
     * 콘텐츠 생성 폼 (관리자만 접근 가능)
     */
    @GetMapping("/create")
    public String createForm(HttpSession session, Model model) {
        logger.debug("콘텐츠 생성 폼");
        
        // 권한 체크
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 콘텐츠 생성 접근 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/";
        }
        
        // 세션에서 테넌트 ID 가져오기
        String tenantId = authorizationUtil.getTenantId(session);
        model.addAttribute("tenantId", tenantId);
        
        return "content/create";
    }

    /**
     * 콘텐츠 등록 (관리자만 가능)
     */
    @PostMapping
    public String createContent(ContentVO contentVO, HttpSession session, Model model) {
        logger.info("콘텐츠 등록: contentId={}", contentVO.getContentId());
        
        // 권한 체크
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 콘텐츠 등록 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/";
        }
        
        // 테넌트 ID 자동 설정
        String tenantId = authorizationUtil.getTenantId(session);
        contentVO.setTenantId(tenantId);
        
        int result = contentService.insertContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 등록 성공");
            return "redirect:/content/list";
        } else {
            logger.warn("콘텐츠 등록 실패");
            model.addAttribute("error", "콘텐츠 등록에 실패했습니다.");
            return "content/create";
        }
    }

    /**
     * 콘텐츠 수정 폼 (관리자만 접근 가능)
     */
    @GetMapping("/{contentId}/edit")
    public String editForm(@PathVariable String contentId, HttpSession session, Model model) {
        logger.debug("콘텐츠 수정 폼: contentId={}", contentId);
        
        // 권한 체크
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 콘텐츠 수정 폼 접근 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/";
        }
        
        ContentVO content = contentService.selectContent(contentId);
        model.addAttribute("content", content);
        return "content/edit";
    }

    /**
     * 콘텐츠 수정 (관리자만 가능)
     */
    @PutMapping("/{contentId}")
    public String updateContent(@PathVariable String contentId, ContentVO contentVO, 
                               HttpSession session, Model model) {
        logger.info("콘텐츠 수정: contentId={}", contentId);
        
        // 권한 체크
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 콘텐츠 수정 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/";
        }
        
        contentVO.setContentId(contentId);
        
        // 테넌트 ID 유지
        String tenantId = authorizationUtil.getTenantId(session);
        contentVO.setTenantId(tenantId);
        
        int result = contentService.updateContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 수정 성공");
            return "redirect:/content/" + contentId;
        } else {
            logger.warn("콘텐츠 수정 실패");
            model.addAttribute("error", "콘텐츠 수정에 실패했습니다.");
            return "content/edit";
        }
    }

    /**
     * 콘텐츠 삭제 (관리자만 가능)
     */
    @DeleteMapping("/{contentId}")
    public String deleteContent(@PathVariable String contentId, HttpSession session, Model model) {
        logger.info("콘텐츠 삭제: contentId={}", contentId);
        
        // 권한 체크
        if (!authorizationUtil.isAdmin(session)) {
            logger.warn("권한 없음: 콘텐츠 삭제 차단");
            model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
            return "redirect:/";
        }
        
        // ContentVO에 contentId와 tenantId 설정
        ContentVO contentVO = new ContentVO();
        contentVO.setContentId(contentId);
        contentVO.setTenantId(authorizationUtil.getTenantId(session));
        
        int result = contentService.deleteContent(contentVO);
        if (result > 0) {
            logger.info("콘텐츠 삭제 성공");
        } else {
            logger.warn("콘텐츠 삭제 실패");
        }
        return "redirect:/content/list";
    }

    /**
     * 과정별 콘텐츠 조회
     */
    @GetMapping("/course/{courseId}")
    public String listContentsByCourse(@PathVariable String courseId, Model model) {
        logger.debug("과정별 콘텐츠 조회: courseId={}", courseId);
        List<ContentVO> contents = contentService.selectContentsByCourseId(courseId);
        model.addAttribute("contents", contents);
        model.addAttribute("courseId", courseId);
        return "content/courseContents";
    }
}
