<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- ========================================
     글로벌 네비게이션 바
     - 기능별 메뉴 구성 (콘텐츠 관리, 강의 관리 등)
     - 드롭다운 서브메뉴 지원
     ======================================== -->
<c:if test="${not empty sessionScope.userId}">
<nav class="nav-global">
    <div class="nav-container">
        <ul class="nav-menu">
            <!-- 콘텐츠 관리 메뉴 -->
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/content/list" class="nav-link">
                    📋 콘텐츠 관리
                </a>
                <c:if test="${sessionScope.isAdmin}">
                <ul class="nav-submenu">
                    <li><a href="${pageContext.request.contextPath}/content/list">콘텐츠 목록</a></li>
                    <li><a href="${pageContext.request.contextPath}/content/create">새 콘텐츠 등록</a></li>
                </ul>
                </c:if>
            </li>

            <!-- 강의 관리 메뉴 (새로 추가) -->
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/lecture/list" class="nav-link">
                    🎓 강의 관리
                </a>
                <c:if test="${sessionScope.isAdmin}">
                <ul class="nav-submenu">
                    <li><a href="${pageContext.request.contextPath}/lecture/list">강의 목록</a></li>
                    <li><a href="${pageContext.request.contextPath}/lecture/create">새 강의 등록</a></li>
                </ul>
                </c:if>
            </li>

            <!-- 메뉴 관리 (관리자만) -->
            <c:if test="${sessionScope.isAdmin}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/menu/list" class="nav-link">
                    ⚙️ 메뉴 관리
                </a>
                <ul class="nav-submenu">
                    <li><a href="${pageContext.request.contextPath}/menu/list">메뉴 목록</a></li>
                    <li><a href="${pageContext.request.contextPath}/menu/create">새 메뉴 등록</a></li>
                </ul>
            </li>
            </c:if>

            <!-- 대시보드 (관리자만) -->
            <c:if test="${sessionScope.isAdmin}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dashboard" class="nav-link">
                    📊 대시보드
                </a>
            </li>
            </c:if>
        </ul>
    </div>
</nav>
</c:if>
