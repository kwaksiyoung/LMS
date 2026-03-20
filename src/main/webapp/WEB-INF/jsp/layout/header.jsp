<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    /* ========================================
       상단 Top Header CSS
       ======================================== */
    .top-header {
        background-color: #ffffff;
        border-bottom: 1px solid #e0e0e0;
        padding: 0;
        margin-bottom: 0;
        box-shadow: 0 2px 4px rgba(0,0,0,0.05);
    }
    
    .top-header-container {
        max-width: 1200px;
        margin: 0 auto;
        padding: 0 20px;
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: 60px;
    }
    
    .logo {
        font-size: 20px;
        font-weight: 700;
        color: #007bff;
        text-decoration: none;
        display: flex;
        align-items: center;
        gap: 8px;
        white-space: nowrap;
    }
    
    .logo:hover {
        color: #0056b3;
    }
    
    .top-header-menu {
        display: flex;
        align-items: center;
    }
    
    .user-info {
        display: flex;
        align-items: center;
        gap: 15px;
    }
    
    .user-name {
        font-weight: 500;
        color: #333;
        font-size: 14px;
    }
    
    .menu-link {
        color: #555;
        text-decoration: none;
        font-size: 14px;
        padding: 8px 12px;
        border-radius: 4px;
        transition: all 0.2s ease;
    }
    
    .menu-link:hover {
        background-color: #f8f9fa;
        color: #007bff;
    }
    
    .logout-link {
        color: #dc3545;
    }
    
    .logout-link:hover {
        background-color: #ffe5e5;
        color: #bd2130;
    }
</style>

<!-- ========================================
     공통 상단 메뉴 (Top Header)
     - 로그인/로그아웃, 내정보 (우측정렬)
     ======================================== -->
<div class="top-header">
    <div class="top-header-container">
        <a href="${pageContext.request.contextPath}/" class="logo">
            📚 LMS
        </a>
        
        <div class="top-header-menu">
            <c:if test="${not empty sessionScope.userId}">
            <div class="user-info">
                <span class="user-name">${not empty sessionScope.userName ? sessionScope.userName : sessionScope.userId}</span>
                <a href="${pageContext.request.contextPath}/user/myinfo" class="menu-link">내정보</a>
                <a href="${pageContext.request.contextPath}/auth/logout" class="menu-link logout-link">로그아웃</a>
            </div>
            </c:if>
            <c:if test="${empty sessionScope.userId}">
            <div class="user-info">
                <a href="${pageContext.request.contextPath}/auth/login" class="menu-link">로그인</a>
                <a href="${pageContext.request.contextPath}/user/register" class="menu-link">회원가입</a>
            </div>
            </c:if>
        </div>
    </div>
</div>
