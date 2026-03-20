<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
