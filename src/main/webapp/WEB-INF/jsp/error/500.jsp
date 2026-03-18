<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 서버 오류</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/error.css">
</head>
<body class="error-500">
    <div class="error-container">
        <h1>500</h1>
        <p>서버에서 오류가 발생했습니다.<br/>잠시 후 다시 시도해주세요.</p>
        <a href="<%= request.getContextPath() %>/">홈으로 돌아가기</a>
    </div>
</body>
</html>
