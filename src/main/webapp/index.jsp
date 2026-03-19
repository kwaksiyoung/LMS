<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId = (String) session.getAttribute("userId");
    String userName = (String) session.getAttribute("userName");
    Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

    boolean isLoggedIn = userId != null && !userId.isEmpty();
    boolean isAdminUser = isAdmin != null && isAdmin;
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - Learning Management System</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/nav.css">
    <style>
        .main-container {
            max-width: 1400px;
            margin: 40px auto;
            padding: 0 20px;
        }
        .welcome-section {
            background: white;
            border-radius: 10px;
            padding: 40px;
            margin-bottom: 40px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            text-align: center;
        }
        .welcome-section h2 {
            color: #333;
            margin-bottom: 15px;
            font-size: 32px;
        }
        .cards-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
            margin-bottom: 40px;
        }
        .card {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            transition: all 0.3s;
            text-align: center;
        }
        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
        }
        .card-icon {
            font-size: 40px;
            margin-bottom: 15px;
        }
        .card h3 {
            color: #333;
            margin-bottom: 10px;
            font-size: 18px;
        }
        .card p {
            color: #666;
            font-size: 14px;
            margin-bottom: 20px;
        }
        .card-links {
            display: flex;
            gap: 10px;
            flex-direction: column;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
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
        .status-section {
            background: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            margin-bottom: 40px;
        }
        .status-section h3 {
            color: #4CAF50;
            margin-bottom: 15px;
        }
        .status-section ul {
            list-style: none;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .status-section li {
            color: #666;
            padding: 10px;
            background: #f9f9f9;
            border-radius: 5px;
            border-left: 4px solid #4CAF50;
        }
        .status-section li:before {
            content: "✓ ";
            color: #4CAF50;
            font-weight: bold;
            margin-right: 5px;
        }
        .login-prompt {
            background: white;
            border-radius: 10px;
            padding: 60px 40px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .login-prompt h2 {
            color: #333;
            margin-bottom: 15px;
            font-size: 28px;
        }
        .login-buttons {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
    </style>
</head>
<body>
    <!-- 공통 헤더 포함 -->
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    
    <!-- 공통 네비게이션 포함 (로그인 시에만 표시) -->
    <% if (isLoggedIn) { %>
    <jsp:include page="/WEB-INF/jsp/layout/navigation.jsp" />
    <% } else { %>
    <!-- 비로그인 상태 body 태그에 클래스 추가 -->
    <script>document.body.classList.add('logged-out');</script>
    <% } %>

    <main class="main-content">
    <div class="main-container">
        <% if (isLoggedIn) { %>
        <div class="welcome-section">
            <h2>환영합니다, <%= userName != null ? userName : userId %>님!</h2>
            <p>Learning Management System에 오신 것을 환영합니다.</p>
        </div>

        <div class="status-section">
            <h3>✓ 시스템 상태</h3>
            <ul>
                <li>Spring Framework 5.3.27</li>
                <li>eGovFrame 4.2.0</li>
                <li>MariaDB 데이터베이스 연동</li>
                <li>역할 기반 접근 제어</li>
                <li>REST API 기반 아키텍처</li>
                <li>다중 테넌트 지원</li>
            </ul>
        </div>

        <div class="cards-grid">
            <div class="card">
                <div class="card-icon">👤</div>
                <h3>내 정보</h3>
                <p>개인 정보를 확인하고 관리합니다.</p>
                <div class="card-links">
                    <a href="<%= request.getContextPath() %>/user/myinfo" class="btn btn-primary">정보 관리</a>
                </div>
            </div>

            <% if (isAdminUser) { %>
            <div class="card">
                <div class="card-icon">⚙️</div>
                <h3>콘텐츠 관리</h3>
                <p>콘텐츠를 등록하고 관리합니다.</p>
                <div class="card-links">
                    <a href="<%= request.getContextPath() %>/content/list" class="btn btn-primary">목록</a>
                    <a href="<%= request.getContextPath() %>/content/create" class="btn btn-secondary">등록</a>
                </div>
            </div>
            <% } %>
        </div>

        <% } else { %>
        <div class="login-prompt">
            <h2>Learning Management System</h2>
            <p>로그인하여 학습을 시작하세요.</p>
            <div class="login-buttons">
                <a href="<%= request.getContextPath() %>/auth/login" class="btn btn-primary">로그인</a>
                <a href="<%= request.getContextPath() %>/user/register" class="btn btn-secondary">회원가입</a>
            </div>
        </div>

        <div class="status-section">
            <h3>✓ 시스템 상태</h3>
            <ul>
                <li>Spring Framework 5.3.27</li>
                <li>eGovFrame 4.2.0</li>
                <li>MariaDB 연동</li>
                <li>역할 기반 제어</li>
                <li>REST API</li>
                <li>다중 테넌트</li>
            </ul>
        </div>
        <% } %>
    </div>
    </main>
</body>
</html>
