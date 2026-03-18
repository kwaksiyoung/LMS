<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 내 정보</title>
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
            max-width: 600px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 30px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #f0f0f0;
            padding-bottom: 20px;
        }
        .header h1 {
            color: #333;
            font-size: 28px;
        }
        .nav-links {
            display: flex;
            gap: 10px;
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
        .btn-secondary {
            background: #f0f0f0;
            color: #333;
        }
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        .btn-danger {
            background: #ff6b6b;
            color: white;
            padding: 8px 16px;
            font-size: 13px;
        }
        .btn-danger:hover {
            background: #ff5252;
        }
        .user-info-section {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 15px 0;
            border-bottom: 1px solid #eee;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            color: #666;
            font-weight: 500;
            min-width: 120px;
        }
        .info-value {
            color: #333;
            font-weight: 600;
        }
        .badge-admin {
            display: inline-block;
            background: #ff6b6b;
            color: white;
            padding: 2px 8px;
            border-radius: 3px;
            font-size: 11px;
            margin-left: 5px;
        }
        .badge {
            display: inline-block;
            background: #e3f2fd;
            color: #1976d2;
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 12px;
            margin-right: 5px;
            margin-bottom: 5px;
        }
        .section-title {
            color: #333;
            font-size: 18px;
            font-weight: 600;
            margin: 30px 0 15px 0;
            padding-bottom: 10px;
            border-bottom: 2px solid #667eea;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div>
                <h1>👤 내 정보</h1>
            </div>
            <div class="nav-links">
                <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">홈</a>
                <a href="<%= request.getContextPath() %>/user/logout" class="btn btn-danger">로그아웃</a>
            </div>
        </div>

        <%
            String userId = (String) session.getAttribute("userId");
            String userName = (String) session.getAttribute("userName");
            String userEmail = (String) session.getAttribute("userEmail");
            String tenantId = (String) session.getAttribute("tenantId");
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            java.util.List<String> roles = (java.util.List<String>) session.getAttribute("roles");
            
            boolean isAdminUser = isAdmin != null && isAdmin;
        %>

        <div class="user-info-section">
            <div class="section-title">사용자 정보</div>
            
            <div class="info-row">
                <span class="info-label">아이디</span>
                <span class="info-value"><%= userId %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">이름</span>
                <span class="info-value"><%= userName %><% if (isAdminUser) { %><span class="badge-admin">관리자</span><% } %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">이메일</span>
                <span class="info-value"><%= userEmail %></span>
            </div>
            
            <div class="info-row">
                <span class="info-label">고객사</span>
                <span class="info-value"><%= tenantId %></span>
            </div>
        </div>

        <% if (roles != null && !roles.isEmpty()) { %>
        <div class="user-info-section">
            <div class="section-title">역할 및 권한</div>
            
            <div style="padding: 15px 0;">
                <% for (String role : roles) { %>
                <span class="badge"><%= role %></span>
                <% } %>
            </div>
        </div>
        <% } %>

        <div class="user-info-section">
            <div class="section-title">이용 가능한 기능</div>
            
            <div style="padding: 15px 0; color: #666; line-height: 1.8;">
                <p>✓ 콘텐츠 목록 조회</p>
                <% if (isAdminUser) { %>
                <p>✓ 콘텐츠 등록</p>
                <p>✓ 콘텐츠 수정</p>
                <p>✓ 콘텐츠 삭제</p>
                <% } %>
            </div>
        </div>
    </div>
</body>
</html>
