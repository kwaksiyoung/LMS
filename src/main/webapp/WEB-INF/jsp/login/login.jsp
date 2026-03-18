<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 로그인</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .login-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            padding: 40px;
            max-width: 400px;
            width: 100%;
        }
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 30px;
            font-size: 24px;
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
        input[type="password"] {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        input[type="text"]:focus,
        input[type="password"]:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }
        .error-message {
            background: #fee;
            color: #c33;
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            display: none;
        }
        button {
            width: 100%;
            padding: 12px;
            background: #667eea;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s;
        }
        button:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .remember-me {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }
        .remember-me input {
            margin-right: 8px;
        }
        .remember-me label {
            margin: 0;
            font-weight: normal;
        }
    </style>
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
                <select id="tenantId" name="tenantId" required style="width: 100%; padding: 12px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px;">
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
