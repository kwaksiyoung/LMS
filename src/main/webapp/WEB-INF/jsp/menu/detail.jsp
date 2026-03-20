<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메뉴 상세</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: #f5f5f5;
            color: #333;
        }
        
        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .header {
            margin-bottom: 30px;
        }
        
        .header h1 {
            font-size: 28px;
            margin-bottom: 10px;
            color: #222;
        }
        
        .header p {
            color: #666;
            font-size: 14px;
        }
        
        .detail-card {
            background: white;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .detail-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 20px;
            padding-bottom: 20px;
            border-bottom: 1px solid #eee;
        }
        
        .detail-row:last-child {
            border-bottom: none;
        }
        
        .detail-field {
            margin-bottom: 15px;
        }
        
        .detail-label {
            font-weight: 600;
            color: #666;
            font-size: 13px;
            text-transform: uppercase;
            margin-bottom: 5px;
        }
        
        .detail-value {
            font-size: 16px;
            color: #333;
            word-break: break-word;
        }
        
        .detail-value code {
            background-color: #f5f5f5;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }
        
        .icon-display {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .icon-display .icon {
            font-size: 32px;
        }
        
        .status-badge {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .status-active {
            background-color: #d4edda;
            color: #155724;
        }
        
        .status-inactive {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .roles-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }
        
        .role-badge {
            background-color: #e7f3ff;
            color: #004085;
            padding: 4px 12px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 500;
        }
        
        .empty-roles {
            color: #999;
            font-style: italic;
        }
        
        .actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
        }
        
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-edit {
            background-color: #007bff;
            color: white;
            flex: 1;
        }
        
        .btn-edit:hover {
            background-color: #0056b3;
        }
        
        .btn-delete {
            background-color: #dc3545;
            color: white;
        }
        
        .btn-delete:hover {
            background-color: #c82333;
        }
        
        .btn-back {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-back:hover {
            background-color: #5a6268;
        }
        
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
</head>
<body>
    <%@ include file="../layout/header.jsp" %>
    <%@ include file="../layout/navigation.jsp" %>
    
    <div class="container">
        <div class="header">
            <h1>📋 메뉴 상세 정보</h1>
            <p>메뉴의 상세 정보를 확인합니다.</p>
        </div>
        
        <div class="detail-card">
            <div class="detail-row">
                <div class="detail-field">
                    <div class="detail-label">메뉴 ID</div>
                    <div class="detail-value"><code>${menu.menuId}</code></div>
                </div>
                
                <div class="detail-field">
                    <div class="detail-label">메뉴명</div>
                    <div class="detail-value">${menu.menuNm}</div>
                </div>
            </div>
            
            <div class="detail-row">
                <div class="detail-field">
                    <div class="detail-label">메뉴 URL</div>
                    <div class="detail-value"><code>${menu.menuUrl}</code></div>
                </div>
                
                <div class="detail-field">
                    <div class="detail-label">정렬 순서</div>
                    <div class="detail-value">${menu.sortOrder}</div>
                </div>
            </div>
            
            <div class="detail-row">
                <div class="detail-field">
                    <div class="detail-label">아이콘</div>
                    <div class="detail-value">
                        <div class="icon-display">
                            <span class="icon">${not empty menu.menuIcon ? menu.menuIcon : '📌'}</span>
                            <span><code>${menu.menuIcon}</code></span>
                        </div>
                    </div>
                </div>
                
                <div class="detail-field">
                    <div class="detail-label">사용 여부</div>
                    <div class="detail-value">
                        <span class="status-badge ${menu.useYn == 'Y' ? 'status-active' : 'status-inactive'}">
                            ${menu.useYn == 'Y' ? '사용 중' : '미사용'}
                        </span>
                    </div>
                </div>
            </div>
            
            <div class="detail-row">
                <div class="detail-field">
                    <div class="detail-label">부모 메뉴</div>
                    <div class="detail-value">
                        <c:if test="${not empty menu.parentMenuId}">
                            <code>${menu.parentMenuId}</code>
                        </c:if>
                        <c:if test="${empty menu.parentMenuId}">
                            <span style="color: #999;">없음 (최상위 메뉴)</span>
                        </c:if>
                    </div>
                </div>
                
                <div class="detail-field">
                    <div class="detail-label">등록일</div>
                    <div class="detail-value">
                        <fmt:formatDate value="${menu.regDt}" pattern="yyyy-MM-dd HH:mm:ss" />
                    </div>
                </div>
            </div>
            
            <div class="detail-field" style="grid-column: 1/-1; border-bottom: 1px solid #eee; padding-bottom: 20px; margin-bottom: 0;">
                <div class="detail-label">접근 가능한 역할</div>
                <div class="detail-value">
                    <div class="roles-container">
                        <c:if test="${empty selectedRoles}">
                            <span class="empty-roles">할당된 역할이 없습니다.</span>
                        </c:if>
                        <c:forEach var="role" items="${selectedRoles}">
                            <span class="role-badge">${role}</span>
                        </c:forEach>
                    </div>
                </div>
            </div>
            
            <div class="actions">
                <a href="${pageContext.request.contextPath}/menu/${menu.menuId}/edit" class="btn btn-edit">✏️ 수정</a>
                <form method="post" action="${pageContext.request.contextPath}/menu/${menu.menuId}" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                    <input type="hidden" name="_method" value="DELETE">
                    <button type="submit" class="btn btn-delete">🗑️ 삭제</button>
                </form>
                <a href="${pageContext.request.contextPath}/menu/list" class="btn btn-back">돌아가기</a>
            </div>
        </div>
    </div>
</body>
</html>
