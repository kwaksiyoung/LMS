<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String userId = (String) session.getAttribute("userId");
    Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
    boolean isLoggedIn = userId != null && !userId.isEmpty();
    boolean isAdminUser = isAdmin != null && isAdmin;
%>

<!-- ========================================
     글로벌 네비게이션 바
     - 기능별 메뉴 구성 (콘텐츠 관리, 강의 관리 등)
     - 드롭다운 서브메뉴 지원
     ======================================== -->
<% if (isLoggedIn) { %>
<nav class="nav-global">
    <div class="nav-container">
        <ul class="nav-menu">
            <!-- 콘텐츠 관리 메뉴 -->
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/content/list" class="nav-link">
                    📋 콘텐츠 관리
                </a>
                <% if (isAdminUser) { %>
                <ul class="nav-submenu">
                    <li><a href="<%= request.getContextPath() %>/content/list">콘텐츠 목록</a></li>
                    <li><a href="<%= request.getContextPath() %>/content/create">새 콘텐츠 등록</a></li>
                </ul>
                <% } %>
            </li>

            <!-- 강의 관리 메뉴 (새로 추가) -->
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/lecture/list" class="nav-link">
                    🎓 강의 관리
                </a>
                <% if (isAdminUser) { %>
                <ul class="nav-submenu">
                    <li><a href="<%= request.getContextPath() %>/lecture/list">강의 목록</a></li>
                    <li><a href="<%= request.getContextPath() %>/lecture/create">새 강의 등록</a></li>
                </ul>
                <% } %>
            </li>

            <!-- 메뉴 관리 (관리자만) -->
            <% if (isAdminUser) { %>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/menu/list" class="nav-link">
                    ⚙️ 메뉴 관리
                </a>
                <ul class="nav-submenu">
                    <li><a href="<%= request.getContextPath() %>/menu/list">메뉴 목록</a></li>
                    <li><a href="<%= request.getContextPath() %>/menu/create">새 메뉴 등록</a></li>
                </ul>
            </li>
            <% } %>

            <!-- 대시보드 (관리자만) -->
            <% if (isAdminUser) { %>
            <li class="nav-item">
                <a href="<%= request.getContextPath() %>/dashboard" class="nav-link">
                    📊 대시보드
                </a>
            </li>
            <% } %>
        </ul>
    </div>
</nav>
<% } %>
