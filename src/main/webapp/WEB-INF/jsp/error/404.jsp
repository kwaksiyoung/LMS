<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - 페이지를 찾을 수 없습니다</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/error.css">
</head>
<body>
    <div class="error-container">
        <h1>404</h1>
        <p>요청한 페이지를 찾을 수 없습니다.</p>
        <a href="<%= request.getContextPath() %>/">홈으로 돌아가기</a>
    </div>
</body>
</html>
