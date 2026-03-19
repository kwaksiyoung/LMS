# 강의 차시 관리 JSP MVC 변환 계획서

**작성일**: 2026-03-19  
**상태**: 분석 완료 → 내일 구현 시작  
**목표**: REST API (fetch) 방식에서 JSP MVC (form) 방식으로 차시 관리 기능 변환

---

## 1. 현황 요약

### 현재 상태 ✅
- ✅ 강의 기본정보 등록 (form.jsp) 완료
- ✅ 강의 상세 조회 (view.jsp) 완료
- ✅ 차시 목록 표시 완료
- ✅ 모달 UI (콘텐츠 선택 + 입력 필드) 완료

### 문제점 🔴
- 🔴 차시 추가/삭제/순서 변경이 **REST API (fetch)** 방식 사용 중
- 🔴 JSP MVC 패턴과 불일치 (세션 기반 인증 vs JWT 토큰)
- 🔴 LectureController에 해당 메서드 없음

### 해결 계획 🟢
1. LectureController에 3개 새 @PostMapping 메서드 추가
2. lecture/view.jsp 모달을 `<form>` 기반으로 변환
3. JavaScript fetch → 순수 폼 제출로 변경
4. 모달 자동 닫기 + AJAX 새로고침 구현

---

## 2. 구현 범위

### Phase A: 백엔드 (LectureController)

**파일**: `src/main/java/kr/co/lms/web/controller/LectureController.java`

#### 추가할 메서드 3개

| 메서드명 | HTTP | 경로 | 기능 | 응답 |
|---------|------|------|------|------|
| `addContent` | POST | `/lecture/addContent` | 다중 콘텐츠 추가 (개별 제목/설명) | redirect + AJAX |
| `removeContent` | POST | `/lecture/removeContent` | 차시 제거 | JSON |
| `reorderContent` | POST | `/lecture/reorderContent` | 차시 순서 변경 | JSON |

#### 구현 템플릿 (따를 패턴)

```java
@PostMapping("/addContent")
public String addContent(
    String lectureId,
    @RequestParam(value="contentIds[]") List<String> contentIds,
    @RequestParam(required=false) String lectureContentTitle,
    @RequestParam(required=false) String lectureContentDesc,
    HttpSession session,
    Model model) {
    
    // 1️⃣ 권한 검증
    if (!authorizationUtil.isAdmin(session)) {
        logger.warn("권한 없음: 차시 추가 차단");
        model.addAttribute("errorMessage", "관리자만 접근할 수 있습니다.");
        return "redirect:/lecture/list";
    }
    
    // 2️⃣ 테넌트 ID 추출
    String tenantId = authorizationUtil.getTenantId(session);
    
    // 3️⃣ 검증
    if (contentIds == null || contentIds.isEmpty()) {
        logger.warn("차시 추가 실패: 콘텐츠 미선택");
        model.addAttribute("errorMessage", "최소 1개의 콘텐츠를 선택하세요.");
        return "redirect:/lecture/view?lectureId=" + lectureId;
    }
    
    // 4️⃣ 비즈니스 로직
    try {
        int successCount = 0;
        for (String contentId : contentIds) {
            int result = lectureService.addContentToLecture(
                lectureId,
                contentId,
                lectureContentTitle,
                lectureContentDesc,
                tenantId
            );
            if (result > 0) successCount++;
        }
        
        if (successCount == 0) {
            logger.warn("차시 추가 실패: DB 저장 실패");
            model.addAttribute("errorMessage", "차시 추가에 실패했습니다.");
            return "redirect:/lecture/view?lectureId=" + lectureId;
        }
        
        logger.info("차시 추가 성공: lectureId={}, count={}", lectureId, successCount);
        
        // 5️⃣ AJAX 또는 리다이렉트 응답
        return "redirect:/lecture/view?lectureId=" + lectureId;
        
    } catch (Exception e) {
        logger.error("차시 추가 중 오류: {}", e.getMessage(), e);
        model.addAttribute("errorMessage", "차시 추가 중 오류가 발생했습니다.");
        return "redirect:/lecture/view?lectureId=" + lectureId;
    }
}
```

**따를 패턴**:
- ✅ `logger.info()` 로깅 (메서드명, 주요 파라미터)
- ✅ `authorizationUtil.isAdmin(session)` 권한 검증
- ✅ `authorizationUtil.getTenantId(session)` 테넌트 ID 추출
- ✅ 검증 후 즉시 리다이렉트 또는 폼 반환
- ✅ 비즈니스 로직에서 성공/실패 체크
- ✅ 성공: `redirect:/lecture/view?lectureId=...`
- ✅ 실패: 에러 메시지와 함께 원래 페이지로 리다이렉트

---

### Phase B: 프론트엔드 (lecture/view.jsp)

**파일**: `src/main/webapp/WEB-INF/jsp/lecture/view.jsp`

#### 변경 범위

| 라인 | 변경 전 | 변경 후 | 설명 |
|------|---------|--------|------|
| 178-237 | div 모달 | form 모달 | 모달을 `<form method="POST">` 로 감싸기 |
| 199 | `checkbox` (선택 목적) | `checkbox` + `hidden` | 선택한 contentId를 POST로 전송 |
| 286-325 | `addSelectedContent()` fetch | `form.submit()` | fetch 제거, 순수 폼 제출 |
| 327-349 | `removeContent()` fetch | POST 폼 제출 또는 fetch 유지 | 삭제 버튼 처리 방식 |
| 368-388 | `reorderContent()` fetch | POST 폼 제출 또는 fetch 유지 | 순서 변경 버튼 처리 방식 |

#### 모달 변환 구조

**변경 전** (fetch 기반):
```html
<div id="contentModal" class="modal">
  <input type="checkbox" value="content1" />
  <button onclick="addSelectedContent(lectureId)">추가</button>
</div>
<script>
  function addSelectedContent(lectureId) {
    fetch('/api/v1/lectures/...' POST)
    location.reload()
  }
</script>
```

**변경 후** (form 기반):
```html
<form id="contentForm" method="POST" action="<%= request.getContextPath() %>/lecture/addContent" 
      onsubmit="return validateForm()">
  <input type="hidden" name="lectureId" value="${lecture.lectureId}">
  
  <!-- 다중 선택 체크박스 -->
  <c:forEach var="content" items="${availableContents}">
    <label class="content-checkbox">
      <input type="checkbox" name="contentIds" value="${content.contentId}" 
             onchange="toggleContentFields(this)" />
      ${content.contentTitle}
    </label>
    
    <!-- 해당 콘텐츠의 제목/설명 입력 필드 (동적) -->
    <div class="content-fields" style="display:none; margin-left: 20px;">
      <input type="text" name="contentTitle[${content.contentId}]" placeholder="차시 제목" />
      <textarea name="contentDesc[${content.contentId}]" placeholder="차시 설명"></textarea>
    </div>
  </c:forEach>
  
  <button type="submit" class="btn btn-primary">선택한 차시 추가</button>
  <button type="button" onclick="closeContentModal()">취소</button>
</form>

<script>
  function toggleContentFields(checkbox) {
    if (checkbox.checked) {
      // 해당 콘텐츠의 입력 필드 표시
    } else {
      // 해당 콘텐츠의 입력 필드 숨김
    }
  }
  
  function validateForm() {
    const checked = document.querySelectorAll('input[name="contentIds"]:checked');
    if (checked.length === 0) {
      alert('최소 1개의 콘텐츠를 선택하세요.');
      return false;
    }
    return true;
  }
</script>
```

---

## 3. 상세 구현 계획

### Day 1 구현 순서 (내일)

#### Step 1️⃣: LectureController 메서드 추가 (30분)
- [ ] `@PostMapping("/addContent")` 구현
  - 입력: `lectureId`, `contentIds[]`, `lectureContentTitle`, `lectureContentDesc`
  - 검증: 콘텐츠 선택 필수
  - DB 처리: `lectureService.addContentToLecture()` 호출
  - 응답: `redirect:/lecture/view?lectureId=...`
  
- [ ] `@PostMapping("/removeContent")` 구현
  - 입력: `lectureId`, `contentId`
  - 검증: 콘텐츠 존재 확인
  - DB 처리: `lectureService.removeContentFromLecture()` 호출
  - 응답: JSON `{success: true}` 또는 리다이렉트

- [ ] `@PostMapping("/reorderContent")` 구현
  - 입력: `lectureId`, `contentId`, `newOrder`
  - 검증: 순서 범위 확인
  - DB 처리: `lectureService.reorderContent()` 호출
  - 응답: JSON `{success: true}` 또는 리다이렉트

#### Step 2️⃣: 권한/tenantId 패턴 적용 (15분)
- [ ] 모든 메서드에 `authorizationUtil.isAdmin(session)` 추가
- [ ] 모든 메서드에 `authorizationUtil.getTenantId(session)` 추가
- [ ] 권한 없으면 `redirect:/lecture/list`
- [ ] 실패 시 `model.addAttribute("errorMessage", "...")` + 리다이렉트

#### Step 3️⃣: lecture/view.jsp 모달 변환 (45분)
- [ ] 모달 HTML을 `<form>` 으로 감싸기 (라인 178-237)
- [ ] `action="/lecture/addContent"` 설정
- [ ] 체크박스 처리:
  - 현재: `<input type="checkbox" value="contentId" />`
  - 변경: `<input type="checkbox" name="contentIds" value="contentId" />`
  
- [ ] 제목/설명 입력 필드 처리:
  - **옵션 A** (간단): 모든 선택 콘텐츠에 동일 제목/설명 적용
    ```html
    <input type="text" name="lectureContentTitle" />
    <textarea name="lectureContentDesc"></textarea>
    ```
  - **옵션 B** (복잡): 각 콘텐츠마다 개별 입력
    ```html
    <div class="content-fields" id="fields_${content.contentId}" style="display:none;">
      <input type="text" name="contentTitle[${content.contentId}]" />
      <textarea name="contentDesc[${content.contentId}]"></textarea>
    </div>
    ```
    → **사용자 선택**: 위에서 "개별 입력" 선택함 → 옵션 B 구현

- [ ] JavaScript 함수 변경:
  - `addSelectedContent()` 제거 또는 폼 제출로 변경
  - `closeContentModal()` 유지 (form 제출 후에도 필요)
  - `validateForm()` 추가 (체크박스 유효성 검사)
  - `toggleContentFields()` 추가 (체크박스 선택 시 입력 필드 표시/숨김)

- [ ] 모달 자동 닫기:
  - form 제출 성공 시 모달 자동 닫기
  - 방법 1: 서버에서 JSON 응답 (AJAX로 처리)
  - 방법 2: form 제출 후 리다이렉트, 모달 상태 유지 안 함

#### Step 4️⃣: 테스트 (30분)
- [ ] 단일 콘텐츠 선택 + 추가
- [ ] 다중 콘텐츠 선택 + 추가
- [ ] 콘텐츠 미선택 시 검증 실패
- [ ] 차시 제목/설명 입력 후 저장 확인
- [ ] 모달 자동 닫힘 확인
- [ ] 차시 목록 새로고침 확인

#### Step 5️⃣: 코드 정리 (15분)
- [ ] REST API 메서드 주석 처리 (유지하되 비활성화)
- [ ] 불필요한 JavaScript 함수 정리
- [ ] 로깅 메시지 확인
- [ ] lsp_diagnostics 확인 (타입 오류 없음)

---

## 4. 주요 결정사항 확인

### 사용자 답변 (선택사항)
- ✅ **차시 제목/설명**: 각 콘텐츠마다 **개별 입력** (모달에서 동적 필드)
- ✅ **화면 반응**: 모달만 닫고 **AJAX로 차시 목록 새로고침** (페이지 새로고침 X)
- ✅ **REST API**: **유지** (프론트 분리 시 대비, 주석 처리)

### 구현 세부사항
- **contentIds 전달**: `name="contentIds"` 배열로 여러 개 선택 가능
- **폼 제출**: POST 메서드로 `/lecture/addContent` 호출
- **응답**: 
  - 성공: 모달 닫기 + 차시 목록 AJAX 새로고침
  - 실패: 에러 메시지 표시 + 모달 유지
- **검증**: 콘텐츠 미선택 시 JS `validateForm()` 에서 차단

---

## 5. 참고: 코드 스니펫

### 파일 경로
- **Controller**: `src/main/java/kr/co/lms/web/controller/LectureController.java`
- **Service**: `src/main/java/kr/co/lms/service/LectureService.java`
- **JSP**: `src/main/webapp/WEB-INF/jsp/lecture/view.jsp`
- **AuthorizationUtil**: `src/main/java/kr/co/lms/web/util/AuthorizationUtil.java`

### 기존 메서드 (참고용)
- `create()` (라인 139-169): POST 폼 제출 패턴
- `edit()` (라인 201-226): POST 폼 제출 + 리다이렉트 패턴
- `delete()` (라인 231-251): 권한 검증 + 리다이렉트 패턴

### 서비스 메서드 (이미 존재)
- `lectureService.addContentToLecture(lectureId, contentId, tenantId)`
- `lectureService.removeContentFromLecture(lectureId, contentId, tenantId)`
- `lectureService.reorderContent(lectureId, contentId, newOrder, tenantId)`

---

## 6. 위험 요소 & 주의사항

| 위험 | 대책 |
|------|------|
| tenantId 미설정 → 다른 테넌트 데이터 수정 | `authorizationUtil.getTenantId(session)` 필수 |
| 권한 미검증 → 일반 사용자가 차시 수정 | `authorizationUtil.isAdmin(session)` 필수 |
| 콘텐츠 선택 안 함 → 빈 값 저장 | `validateForm()` JS 검증 + 서버 검증 |
| 동적 필드 미처리 → 선택되지 않은 콘텐츠 정보 손실 | `toggleContentFields()` 함수로 show/hide 처리 |
| 모달 닫기 안 됨 → 사용자 혼동 | `closeContentModal()` 함수 유지 및 테스트 |

---

## 7. 완료 기준

### 백엔드 ✅
- [ ] 3개 메서드 구현 완료
- [ ] 모든 메서드에 권한 검증 & tenantId 설정
- [ ] lsp_diagnostics 통과 (타입 오류 없음)
- [ ] 로깅 메시지 확인

### 프론트엔드 ✅
- [ ] 모달을 `<form>` 으로 변환
- [ ] 체크박스 + 동적 입력 필드 구현
- [ ] JavaScript 함수 정리 (fetch 제거)
- [ ] 모달 자동 닫기 + AJAX 새로고침

### 테스트 ✅
- [ ] 단일/다중 콘텐츠 추가
- [ ] 에러 메시지 표시
- [ ] 모달 자동 닫기
- [ ] 차시 목록 새로고침

---

## 8. Momus 검증 대기

계획 검증 필요 시:
```bash
# 내일 아침에 실행
momus prompt=".sisyphus/plans/lecture-chapter-management.md"
```

**검증 항목**:
- 계획의 명확성 (각 단계가 구체적인가?)
- 검증 가능성 (완료 기준이 측정 가능한가?)
- 완성도 (누락된 항목이 없는가?)

---

**작성자**: Claude Code (Sisyphus)  
**마지막 업데이트**: 2026-03-19 14:22 KST  
**상태**: ✅ 분석 완료 → 내일 구현 시작 준비 완료
