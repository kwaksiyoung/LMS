<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 내 정보</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/myinfo.css">
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
            <h2>역할 및 권한</h2>

            <div>
                <% for (String role : roles) { %>
                <span class="badge"><%= role %></span>
                <% } %>
            </div>
        </div>
        <% } %>

        <div class="user-info-section">
            <h2>이용 가능한 기능</h2>

            <div>
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
