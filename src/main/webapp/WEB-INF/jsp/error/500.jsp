<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - 서버 오류</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            text-align: center;
        }
        .error-container {
            background: rgba(0,0,0,0.2);
            padding: 40px;
            border-radius: 10px;
            max-width: 600px;
        }
        h1 {
            font-size: 72px;
            margin-bottom: 10px;
        }
        p {
            font-size: 18px;
            margin-bottom: 30px;
            opacity: 0.9;
        }
        a {
            display: inline-block;
            padding: 12px 30px;
            background: white;
            color: #f5576c;
            text-decoration: none;
            border-radius: 5px;
            font-weight: 500;
            transition: all 0.3s;
        }
        a:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>500</h1>
        <p>서버에서 오류가 발생했습니다.<br/>잠시 후 다시 시도해주세요.</p>
        <a href="<%= request.getContextPath() %>/">홈으로 돌아가기</a>
    </div>
</body>
</html>
