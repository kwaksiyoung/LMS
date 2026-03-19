package kr.co.lms.web.controller;

import kr.co.lms.service.ContentService;
import kr.co.lms.service.LectureService;
import kr.co.lms.vo.ContentVO;
import kr.co.lms.vo.LectureVO;
import kr.co.lms.web.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

/**
 * 강의(단원) 관리 Controller (JSP MVC)
 *
 * 권한:
 * - ROLE_ADMIN: 모든 기능 (CRUD)
 * - 기타 사용자: 조회만 가능
 */
@Controller
@RequestMapping("/lecture")
@RequiredArgsConstructor
public class LectureController {

  private static final Logger logger = LoggerFactory.getLogger(LectureController.class);

  private final LectureService lectureService;
  private final ContentService contentService;
  private final AuthorizationUtil authorizationUtil;

  /**
   * 강의 목록 페이지 (검색/필터링/페이징 지원)
   */
  @GetMapping("/list")
  public String list(
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String lectureType,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "10") Integer pageSize,
      Model model) {
    
    logger.info("강의 목록 페이지 요청: keyword={}, lectureType={}, page={}, pageSize={}", 
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
    List<LectureVO> lectureList = lectureService.selectLectureListWithSearch(searchVO);
    
    // 총 개수 조회 (페이징 계산용)
    int totalCount = lectureService.selectLectureListWithSearchCount(searchVO);
    searchVO.setTotalCount(totalCount);
    searchVO.setTotalPages((totalCount + pageSize - 1) / pageSize); // 올림 처리
    
    // 모델에 데이터 추가
    model.addAttribute("lectureList", lectureList);
    model.addAttribute("searchVO", searchVO);
    model.addAttribute("keyword", keyword);
    model.addAttribute("lectureType", lectureType);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("totalCount", totalCount);
    model.addAttribute("totalPages", searchVO.getTotalPages());
    
    return "lecture/list";
  }

  /**
   * 강의 상세 조회 페이지 (최적화 - LEFT JOIN으로 N+1 해결)
   * JSP MVC 패턴: 콘텐츠 목록을 미리 로드하여 모달에 전달
   */
  @GetMapping("/view")
  public String view(String lectureId, HttpSession session, Model model) {
    logger.info("강의 상세 조회: lectureId={}", lectureId);
    
    // LEFT JOIN으로 최적화된 조회 (N+1 문제 해결)
    LectureVO lecture = lectureService.selectLectureWithContentsOptimized(lectureId);
    
    if (lecture == null) {
      logger.warn("강의를 찾을 수 없습니다: lectureId={}", lectureId);
      model.addAttribute("errorMessage", "강의를 찾을 수 없습니다.");
      return "redirect:/lecture/list";
    }
    
    // JSP MVC 패턴: 모달용 콘텐츠 목록 미리 로드 (useYn='Y'인 콘텐츠만)
    ContentVO searchVO = new ContentVO();
    searchVO.setUseYn("Y");
    // tenantId 설정 (session에서 조회)
    String tenantId = authorizationUtil.getTenantId(session);
    searchVO.setTenantId(tenantId);
    
    List<ContentVO> availableContents = contentService.selectContentList(searchVO);
    
    logger.info("강의 상세 조회: tenantId={}, 사용 가능한 콘텐츠 {} 개", tenantId, availableContents != null ? availableContents.size() : 0);
    
    model.addAttribute("lecture", lecture);
    model.addAttribute("availableContents", availableContents);  // 모달용 콘텐츠 목록
    return "lecture/view";
  }

  /**
   * 강의 등록 폼 (관리자만 접근)
   */
  @GetMapping("/create")
  public String createForm(HttpSession session, Model model) {
    logger.info("강의 등록 폼 요청");
    
    // 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
      logger.warn("권한 없음: 강의 등록 폼 접근 차단");
      return "redirect:/lecture/list";
    }
    
    String tenantId = authorizationUtil.getTenantId(session);
    model.addAttribute("lecture", new LectureVO());
    model.addAttribute("tenantId", tenantId);
    model.addAttribute("isCreate", true);
    return "lecture/form";
  }

  /**
   * 강의 등록 처리 (관리자만)
   */
  @PostMapping("/create")
  public String create(LectureVO lectureVO, HttpSession session, Model model) {
    logger.info("강의 등록 처리: lectureNm={}", lectureVO.getLectureNm());
    
    // 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
      logger.warn("권한 없음: 강의 등록 차단");
      model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
      return "redirect:/lecture/list";
    }
    
    // 강의 ID 자동 생성
    if (lectureVO.getLectureId() == null || lectureVO.getLectureId().trim().isEmpty()) {
      lectureVO.setLectureId("LEC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }
    
    // 테넌트 ID 자동 설정
    String tenantId = authorizationUtil.getTenantId(session);
    lectureVO.setTenantId(tenantId);
    
    int result = lectureService.insertLecture(lectureVO);
    if (result == 0) {
      logger.warn("강의 등록 실패");
      model.addAttribute("error", "강의 등록에 실패했습니다.");
      model.addAttribute("lecture", lectureVO);
      return "lecture/form";
    }
    
    logger.info("강의 등록 성공: lectureId={}", lectureVO.getLectureId());
    return "redirect:/lecture/view?lectureId=" + lectureVO.getLectureId();
  }

  /**
   * 강의 수정 폼 (관리자만 접근)
   */
  @GetMapping("/edit")
  public String editForm(String lectureId, HttpSession session, Model model) {
    logger.info("강의 수정 폼: lectureId={}", lectureId);
    
    // 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
      logger.warn("권한 없음: 강의 수정 폼 접근 차단");
      return "redirect:/lecture/list";
    }
    
    LectureVO lecture = lectureService.selectLecture(lectureId);
    if (lecture == null) {
      logger.warn("강의를 찾을 수 없습니다: lectureId={}", lectureId);
      model.addAttribute("errorMessage", "강의를 찾을 수 없습니다.");
      return "redirect:/lecture/list";
    }
    
    String tenantId = authorizationUtil.getTenantId(session);
    model.addAttribute("lecture", lecture);
    model.addAttribute("tenantId", tenantId);
    model.addAttribute("isCreate", false);
    return "lecture/form";
  }

  /**
   * 강의 수정 처리 (관리자만)
   */
  @PostMapping("/edit")
  public String edit(String lectureId, LectureVO lectureVO, HttpSession session, Model model) {
    logger.info("강의 수정 처리: lectureId={}", lectureId);
    
    // 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
      logger.warn("권한 없음: 강의 수정 차단");
      model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
      return "redirect:/lecture/list";
    }
    
    lectureVO.setLectureId(lectureId);
    String tenantId = authorizationUtil.getTenantId(session);
    lectureVO.setTenantId(tenantId);
    
    int result = lectureService.updateLecture(lectureVO);
    if (result == 0) {
      logger.warn("강의 수정 실패: lectureId={}", lectureId);
      model.addAttribute("error", "강의 수정에 실패했습니다.");
      model.addAttribute("lecture", lectureVO);
      return "lecture/form";
    }
    
    logger.info("강의 수정 성공: lectureId={}", lectureId);
    return "redirect:/lecture/view?lectureId=" + lectureId;
  }

  /**
   * 강의 삭제 (관리자만)
   */
  @PostMapping("/delete")
  public String delete(String lectureId, HttpSession session, Model model) {
    logger.info("강의 삭제: lectureId={}", lectureId);
    
    // 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
      logger.warn("권한 없음: 강의 삭제 차단");
      model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
      return "redirect:/lecture/list";
    }
    
    int result = lectureService.deleteLecture(lectureId);
    if (result == 0) {
      logger.warn("강의 삭제 실패: lectureId={}", lectureId);
      model.addAttribute("errorMessage", "강의 삭제에 실패했습니다.");
      return "redirect:/lecture/view?lectureId=" + lectureId;
    }
    
    logger.info("강의 삭제 성공: lectureId={}", lectureId);
    return "redirect:/lecture/list";
  }
}
