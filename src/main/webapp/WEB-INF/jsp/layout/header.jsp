<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId = (String) session.getAttribute("userId");
    String userName = (String) session.getAttribute("userName");
    Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
    
    boolean isLoggedIn = userId != null && !userId.isEmpty();
    boolean isAdminUser = isAdmin != null && isAdmin;
%>

<!-- ========================================
     공통 상단 메뉴 (Top Header)
     - 로그인/로그아웃, 내정보 (우측정렬)
     ======================================== -->
<div class="top-header">
    <div class="top-header-container">
        <a href="<%= request.getContextPath() %>/" class="logo">
            📚 LMS
        </a>
        
        <div class="top-header-menu">
            <% if (isLoggedIn) { %>
            <div class="user-info">
                <span class="user-name"><%= userName != null ? userName : userId %></span>
                <a href="<%= request.getContextPath() %>/user/myinfo" class="menu-link">내정보</a>
                <a href="<%= request.getContextPath() %>/auth/logout" class="menu-link logout-link">로그아웃</a>
            </div>
            <% } else { %>
            <div class="user-info">
                <a href="<%= request.getContextPath() %>/auth/login" class="menu-link">로그인</a>
                <a href="<%= request.getContextPath() %>/user/register" class="menu-link">회원가입</a>
            </div>
            <% } %>
        </div>
    </div>
</div>
