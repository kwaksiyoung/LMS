<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 콘텐츠 등록</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/content.css">
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>➕ 콘텐츠 등록   </h1>
        </div>

        <% if (request.getParameter("error") != null) { %>
        <div class="alert alert-error">
            <%= request.getParameter("error") %>
        </div>
        <% } %>

        <form method="POST" action="<%= request.getContextPath() %>/content">
            <div class="form-group">
                <label for="contentId">콘텐츠 ID *</label>
                <input type="text" id="contentId" name="contentId" required placeholder="예: CONTENT001">
                <p class="help-text">고유한 콘텐츠 식별자 (필수)</p>
            </div>

            <div class="form-group">
                <label for="contentTitle">제목 *</label>
                <input type="text" id="contentTitle" name="contentTitle" required placeholder="콘텐츠 제목을 입력하세요">
                <p class="help-text">콘텐츠의 제목 (필수)</p>
            </div>

            <div class="form-group">
                <label for="contentType">유형 *</label>
                <select id="contentType" name="contentType" required>
                    <option value="">선택하세요</option>
                    <option value="VIDEO">동영상</option>
                    <option value="DOCUMENT">문서</option>
                    <option value="LINK">링크</option>
                </select>
                <p class="help-text">콘텐츠의 종류 (필수)</p>
            </div>

            <div class="form-group">
                <label for="contentUrl">URL/경로 *</label>
                <input type="url" id="contentUrl" name="contentUrl" required placeholder="https://example.com/video.mp4">
                <p class="help-text">콘텐츠 위치 (필수)</p>
            </div>

            <div class="form-group">
                <label for="durationMinutes">재생시간 (분)</label>
                <input type="number" id="durationMinutes" name="durationMinutes" min="0" placeholder="예: 45">
                <p class="help-text">콘텐츠 길이 (분 단위)</p>
            </div>

            <div class="form-group">
                <label for="contentDesc">설명</label>
                <textarea id="contentDesc" name="contentDesc" placeholder="콘텐츠에 대한 설명을 입력하세요"></textarea>
                <p class="help-text">콘텐츠 상세 설명</p>
            </div>

            <div class="form-group">
                <label for="useYn">사용 여부 *</label>
                <select id="useYn" name="useYn" required>
                    <option value="Y">사용</option>
                    <option value="N">미사용</option>
                </select>
                <p class="help-text">콘텐츠 활성화 여부 (필수)</p>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">등록</button>
                <a href="<%= request.getContextPath() %>/content/list" class="btn btn-secondary">취소</a>
            </div>
        </form>
    </div>
</body>
</html>
