<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 강의 ${isCreate ? '등록' : '수정'}</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/lecture.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/nav.css">
</head>
<body>
    <!-- 공통 헤더 포함 -->
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    
    <!-- 공통 네비게이션 포함 -->
    <jsp:include page="/WEB-INF/jsp/layout/navigation.jsp" />

    <main class="main-content">
        <div class="container">
            <div class="page-header">
                <h1>${isCreate ? '➕ 강의 등록' : '✏️ 강의 수정'}</h1>
            </div>

        <% if (request.getParameter("error") != null) { %>
        <div class="alert alert-error">
            <%= request.getParameter("error") %>
        </div>
        <% } %>

        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>

        <form method="POST" action="<%= request.getContextPath() %>/lecture/${isCreate ? 'create' : 'edit'}">
            <c:if test="${not isCreate}">
                <input type="hidden" name="lectureId" value="${lecture.lectureId}">
            </c:if>

            <c:if test="${isCreate}">
            <div class="form-group">
                <label for="lectureId">강의 ID</label>
                <input type="text" id="lectureId" name="lectureId" placeholder="자동 생성됨 (비워두면 자동 생성)" value="${lecture.lectureId}">
                <p class="help-text">강의 고유 식별자 (비워두면 자동으로 생성됩니다)</p>
            </div>
            </c:if>

            <div class="form-group">
                <label for="lectureNm">강의명 *</label>
                <input type="text" id="lectureNm" name="lectureNm" required 
                       placeholder="예: Java 기초" value="${lecture.lectureNm}">
                <p class="help-text">강의의 이름 (필수)</p>
            </div>

            <div class="form-group">
                <label for="lectureDesc">강의 설명</label>
                <textarea id="lectureDesc" name="lectureDesc" 
                          placeholder="강의에 대한 자세한 설명을 입력하세요">${lecture.lectureDesc}</textarea>
                <p class="help-text">강의의 상세 설명</p>
            </div>

            <div class="form-group">
                <label for="durationMinutes">강의 시간 (분)</label>
                <input type="number" id="durationMinutes" name="durationMinutes" min="0" 
                       placeholder="예: 120" value="${lecture.durationMinutes}">
                <p class="help-text">강의의 총 진행 시간 (분 단위)</p>
            </div>

            <div class="form-group">
                <label for="lectureType">차시 유형 *</label>
                <select id="lectureType" name="lectureType" required>
                    <option value="">선택하세요</option>
                    <option value="REQUIRED" <c:if test="${lecture.lectureType eq 'REQUIRED'}">selected</c:if>>필수 차시</option>
                    <option value="OPTIONAL" <c:if test="${lecture.lectureType eq 'OPTIONAL'}">selected</c:if>>선택 차시</option>
                </select>
                <p class="help-text">필수: 수료 조건에 포함 / 선택: 자유 선택 (필수)</p>
            </div>

            <div class="form-group">
                <label for="useYn">사용 여부 *</label>
                <select id="useYn" name="useYn" required>
                    <option value="">선택하세요</option>
                    <option value="Y" <c:if test="${lecture.useYn eq 'Y'}">selected</c:if>>사용</option>
                    <option value="N" <c:if test="${lecture.useYn eq 'N'}">selected</c:if>>미사용</option>
                </select>
                <p class="help-text">강의 활성화 여부 (필수)</p>
            </div>

            <!-- 강의 등록 후에 차시 구성할 수 있다는 안내 -->
            <c:if test="${isCreate}">
            <div style="padding: 15px; background: #e3f2fd; border-left: 4px solid #2196F3; border-radius: 4px; margin: 20px 0;">
                <strong>💡 팁:</strong> 강의를 등록한 후, 상세 페이지에서 "차시 추가" 버튼을 클릭하여 콘텐츠를 선택하고 차시 제목을 입력할 수 있습니다.
            </div>
            </c:if>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    ${isCreate ? '등록' : '수정'}
                </button>
                <a href="<%= request.getContextPath() %>/lecture/list" class="btn btn-secondary">취소</a>
            </div>
        </form>
        </div>
    </main>
</body>
</html>
