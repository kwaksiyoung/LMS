<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* ========================================
       글로벌 네비게이션 바 CSS
       ======================================== */
    .nav-global {
        background-color: #ffffff;
        border-bottom: 1px solid #e0e0e0;
        margin-bottom: 30px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    
    .nav-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 20px;
    }
    
    .nav-menu {
        list-style: none;
        display: flex;
        gap: 0;
        margin: 0;
        padding: 0;
    }
    
    .nav-item {
        position: relative;
        flex: 0 1 auto;
    }
    
    .nav-link {
        display: block;
        padding: 16px 20px;
        color: #333;
        text-decoration: none;
        font-size: 14px;
        font-weight: 500;
        border-bottom: 3px solid transparent;
        transition: all 0.3s ease;
        white-space: nowrap;
    }
    
    .nav-link:hover {
        background-color: #f8f9fa;
        border-bottom-color: #007bff;
        color: #007bff;
    }
    
    .nav-item:hover > .nav-submenu {
        display: block;
    }
    
    .nav-submenu {
        position: absolute;
        top: 100%;
        left: 0;
        background-color: #ffffff;
        border: 1px solid #e0e0e0;
        border-top: none;
        list-style: none;
        min-width: 180px;
        display: none;
        z-index: 100;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }
    
    .nav-submenu li {
        margin: 0;
        padding: 0;
    }
    
    .nav-submenu a {
        display: block;
        padding: 12px 20px;
        color: #555;
        text-decoration: none;
        font-size: 13px;
        transition: all 0.2s ease;
        border-left: 3px solid transparent;
    }
    
    .nav-submenu a:hover {
        background-color: #f8f9fa;
        color: #007bff;
        border-left-color: #007bff;
        padding-left: 23px;
    }
</style>

<!-- ========================================
     글로벌 네비게이션 바
     - 데이터베이스의 메뉴-역할 매핑을 기반으로 동적 생성
     - 역할별로만 허용된 메뉴만 표시
     - 드롭다운 서브메뉴 지원
     ======================================== -->
<c:if test="${not empty sessionScope.userId}">
<nav class="nav-global">
    <div class="nav-container">
        <ul class="nav-menu">
            <!-- 
                📌 동적 메뉴 주의사항:
                - navigation.jsp는 모든 페이지에 포함되므로 DB 조회 시 성능 영향 고려
                - 현재: 하드코딩 (추후 동적 조회로 개선 가능)
                - 메뉴 표시 여부는 sessionScope.menus 또는 isAdmin 플래그로 제어
                - 메뉴-역할 매핑은 AuthController 로그인 시 DB에서 조회하여 세션에 저장
            -->

            <!-- 콘텐츠 관리 메뉴 -->
            <c:if test="${sessionScope.isAdmin}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/content/list" class="nav-link">
                    📋 콘텐츠 관리
                </a>
                <ul class="nav-submenu">
                    <li><a href="${pageContext.request.contextPath}/content/list">콘텐츠 목록</a></li>
                    <li><a href="${pageContext.request.contextPath}/content/create">새 콘텐츠 등록</a></li>
                </ul>
            </li>
            </c:if>

            <!-- 강의 관리 메뉴: 관리자 및 강사만 표시 -->
            <c:if test="${sessionScope.isAdmin}">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/lecture/list" class="nav-link">
                    🎓 강의 관리
                </a>
                <ul class="nav-submenu">
                    <li><a href="${pageContext.request.contextPath}/lecture/list">강의 목록</a></li>
                    <li><a href="${pageContext.request.contextPath}/lecture/create">새 강의 등록</a></li>
                </ul>
            </li>
            </c:if>

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
