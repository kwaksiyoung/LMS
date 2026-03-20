<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메뉴 관리</title>
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
            max-width: 1200px;
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
        
        .controls {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
            background: white;
            padding: 15px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .search-form {
            display: flex;
            gap: 10px;
            flex: 1;
        }
        
        .search-form input {
            flex: 1;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
        }
        
        .search-form button {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
        }
        
        .search-form button:hover {
            background-color: #0056b3;
        }
        
        .btn-create {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            display: inline-block;
        }
        
        .btn-create:hover {
            background-color: #218838;
        }
        
        .table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        thead {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
        }
        
        th {
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #495057;
        }
        
        td {
            padding: 12px 15px;
            border-bottom: 1px solid #dee2e6;
        }
        
        tbody tr:hover {
            background-color: #f8f9fa;
        }
        
        .menu-name {
            font-weight: 500;
            color: #007bff;
        }
        
        .menu-name a {
            color: #007bff;
            text-decoration: none;
        }
        
        .menu-name a:hover {
            text-decoration: underline;
        }
        
        .roles {
            font-size: 12px;
            background-color: #e7f3ff;
            padding: 4px 8px;
            border-radius: 4px;
            display: inline-block;
        }
        
        .use-yn {
            font-size: 12px;
            padding: 4px 8px;
            border-radius: 4px;
        }
        
        .use-yn.yes {
            background-color: #d4edda;
            color: #155724;
        }
        
        .use-yn.no {
            background-color: #f8d7da;
            color: #721c24;
        }
        
        .btn-group {
            display: flex;
            gap: 5px;
        }
        
        .btn-small {
            padding: 6px 12px;
            font-size: 12px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        
        .btn-edit {
            background-color: #007bff;
            color: white;
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
        
        .pagination {
            display: flex;
            gap: 5px;
            justify-content: center;
            margin-top: 20px;
            padding: 20px;
        }
        
        .pagination a,
        .pagination span {
            padding: 8px 12px;
            border: 1px solid #dee2e6;
            border-radius: 4px;
            text-decoration: none;
            color: #007bff;
        }
        
        .pagination .active {
            background-color: #007bff;
            color: white;
            border-color: #007bff;
        }
        
        .pagination a:hover {
            background-color: #e7f3ff;
        }
        
        .empty-message {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px 15px;
            border-radius: 6px;
            margin-bottom: 20px;
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
            <h1>⚙️ 메뉴 관리</h1>
            <p>시스템의 메뉴를 관리합니다. 역할별로 접근 가능한 메뉴를 설정할 수 있습니다.</p>
        </div>
        
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <div class="controls">
            <form method="get" action="${pageContext.request.contextPath}/menu/list" class="search-form">
                <input type="text" name="keyword" placeholder="메뉴명으로 검색..." value="${keyword}">
                <button type="submit">🔍 검색</button>
            </form>
            <a href="${pageContext.request.contextPath}/menu/create" class="btn-create">➕ 새 메뉴 등록</a>
        </div>
        
        <div class="table-container">
            <c:if test="${empty menus}">
                <div class="empty-message">
                    등록된 메뉴가 없습니다.
                </div>
            </c:if>
            
            <c:if test="${not empty menus}">
                <table>
                    <thead>
                        <tr>
                            <th style="width: 15%;">메뉴 ID</th>
                            <th style="width: 25%;">메뉴명</th>
                            <th style="width: 20%;">메뉴 URL</th>
                            <th style="width: 20%;">접근 가능 역할</th>
                            <th style="width: 10%;">사용여부</th>
                            <th style="width: 10%;">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="menu" items="${menus}">
                            <tr>
                                <td><code>${menu.menuId}</code></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/menu/${menu.menuId}" class="menu-name">
                                        ${menu.menuNm}
                                    </a>
                                </td>
                                <td><code>${menu.menuUrl}</code></td>
                                <td>
                                    <!-- 역할 표시는 Service에서 가져온 데이터가 필요함 -->
                                    <span class="roles">N/A</span>
                                </td>
                                <td>
                                    <span class="use-yn ${menu.useYn == 'Y' ? 'yes' : 'no'}">
                                        ${menu.useYn == 'Y' ? '사용' : '미사용'}
                                    </span>
                                </td>
                                <td>
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/menu/${menu.menuId}/edit" class="btn-small btn-edit">수정</a>
                                        <form method="post" action="${pageContext.request.contextPath}/menu/${menu.menuId}" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                            <input type="hidden" name="_method" value="DELETE">
                                            <button type="submit" class="btn-small btn-delete">삭제</button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/menu/list?page=1&keyword=${keyword}">⬅️ 처음</a>
                        <a href="${pageContext.request.contextPath}/menu/list?page=${currentPage - 1}&keyword=${keyword}">◀️ 이전</a>
                    </c:if>
                    
                    <c:forEach var="p" begin="1" end="${totalPages}">
                        <c:if test="${p == currentPage}">
                            <span class="active">${p}</span>
                        </c:if>
                        <c:if test="${p != currentPage}">
                            <a href="${pageContext.request.contextPath}/menu/list?page=${p}&keyword=${keyword}">${p}</a>
                        </c:if>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/menu/list?page=${currentPage + 1}&keyword=${keyword}">다음 ▶️</a>
                        <a href="${pageContext.request.contextPath}/menu/list?page=${totalPages}&keyword=${keyword}">끝 ➡️</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
