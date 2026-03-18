<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 콘텐츠 수정</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 30px;
        }
        .header {
            margin-bottom: 30px;
            border-bottom: 2px solid #f0f0f0;
            padding-bottom: 20px;
        }
        .header h1 {
            color: #333;
            font-size: 28px;
            margin-bottom: 10px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }
        input[type="text"],
        input[type="url"],
        input[type="number"],
        textarea,
        select {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus,
        input[type="url"]:focus,
        input[type="number"]:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        textarea {
            resize: vertical;
            min-height: 120px;
        }
        .help-text {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
        }
        .button-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #f0f0f0;
        }
        .btn {
            display: inline-block;
            padding: 12px 24px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-primary {
            background: #667eea;
            color: white;
            flex: 1;
        }
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-secondary {
            background: #f0f0f0;
            color: #333;
        }
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        .error-message {
            background: #fee;
            color: #c33;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            display: none;
        }
        .info-box {
            background: #f0f0f0;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 13px;
            color: #666;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>✏️ 콘텐츠 수정</h1>
            <p style="color: #999; margin-top: 5px;">콘텐츠 정보를 수정합니다.</p>
        </div>

        <% if (request.getParameter("error") != null) { %>
        <div class="error-message" style="display: block;">
            <%= request.getParameter("error") %>
        </div>
        <% } %>

        <div class="info-box">
            💡 콘텐츠 ID는 수정할 수 없습니다.
        </div>

        <form method="POST" action="<%= request.getContextPath() %>/content/${content.contentId}">
            <input type="hidden" name="_method" value="PUT">
            
            <div class="form-group">
                <label for="contentId">콘텐츠 ID</label>
                <input type="text" id="contentId" name="contentId" value="${content.contentId}" readonly>
                <p class="help-text">고유한 콘텐츠 식별자 (수정 불가)</p>
            </div>

            <div class="form-group">
                <label for="contentTitle">제목 *</label>
                <input type="text" id="contentTitle" name="contentTitle" value="${content.contentTitle}" required placeholder="콘텐츠 제목을 입력하세요">
                <p class="help-text">콘텐츠의 제목 (필수)</p>
            </div>

            <div class="form-group">
                <label for="contentType">유형 *</label>
                <select id="contentType" name="contentType" required>
                    <option value="VIDEO" ${content.contentType eq 'VIDEO' ? 'selected' : ''}>동영상</option>
                    <option value="DOCUMENT" ${content.contentType eq 'DOCUMENT' ? 'selected' : ''}>문서</option>
                    <option value="LINK" ${content.contentType eq 'LINK' ? 'selected' : ''}>링크</option>
                </select>
                <p class="help-text">콘텐츠의 종류 (필수)</p>
            </div>

            <div class="form-group">
                <label for="contentUrl">URL/경로 *</label>
                <input type="url" id="contentUrl" name="contentUrl" value="${content.contentUrl}" required placeholder="https://example.com/video.mp4">
                <p class="help-text">콘텐츠 위치 (필수)</p>
            </div>

            <div class="form-group">
                <label for="durationMinutes">재생시간 (분)</label>
                <input type="number" id="durationMinutes" name="durationMinutes" value="${content.durationMinutes}" min="0" placeholder="예: 45">
                <p class="help-text">콘텐츠 길이 (분 단위)</p>
            </div>

            <div class="form-group">
                <label for="contentDesc">설명</label>
                <textarea id="contentDesc" name="contentDesc" placeholder="콘텐츠에 대한 설명을 입력하세요">${content.contentDesc}</textarea>
                <p class="help-text">콘텐츠 상세 설명</p>
            </div>

            <div class="form-group">
                <label for="useYn">사용 여부 *</label>
                <select id="useYn" name="useYn" required>
                    <option value="Y" ${content.useYn eq 'Y' ? 'selected' : ''}>사용</option>
                    <option value="N" ${content.useYn eq 'N' ? 'selected' : ''}>미사용</option>
                </select>
                <p class="help-text">콘텐츠 활성화 여부 (필수)</p>
            </div>

            <div class="button-group">
                <button type="submit" class="btn btn-primary">수정</button>
                <a href="<%= request.getContextPath() %>/content/list" class="btn btn-secondary">취소</a>
            </div>
        </form>
    </div>
</body>
</html>
