<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 로그인</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/login.css">
</head>
<body>
    <div class="login-container">
        <h1>LMS 로그인</h1>

        <% if (request.getParameter("error") != null) { %>
        <div class="error-message" style="display: block;">
            로그인에 실패했습니다. 사용자 ID와 비밀번호를 확인하세요.
        </div>
        <% } %>

        <form method="post" action="<%= request.getContextPath() %>/user/login/process">
            <div class="form-group">
                <label for="tenantId">테넌트</label>
                <select id="tenantId" name="tenantId" required>
                    <option value="">테넌트 선택</option>
                    <option value="TENANT001">TENANT001</option>
                    <option value="TENANT002">TENANT002</option>
                </select>
            </div>

            <div class="form-group">
                <label for="userId">사용자 ID</label>
                <input type="text" id="userId" name="userId" required autofocus>
            </div>

            <div class="form-group">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password" required>
            </div>

            <div class="remember-me">
                <input type="checkbox" id="rememberMe" name="rememberMe">
                <label for="rememberMe">자동 로그인</label>
            </div>

            <button type="submit">로그인</button>
        </form>

        <div style="text-align: center; margin-top: 20px; color: #999; font-size: 14px;">
            <p>계정이 없으신가요? <a href="<%= request.getContextPath() %>/user/register" style="color: #667eea; text-decoration: none;">회원가입</a></p>
        </div>
    </div>
</body>
</html>
