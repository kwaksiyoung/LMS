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

    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    <jsp:include page="/WEB-INF/jsp/layout/navigation.jsp" />
    
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
                         ${menu.regDt}
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
                <form method="post" action="${pageContext.request.contextPath}/menu/${menu.menuId}/delete" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                    <button type="submit" class="btn btn-delete">🗑️ 삭제</button>
                </form>
                <a href="${pageContext.request.contextPath}/menu/list" class="btn btn-back">돌아가기</a>
            </div>
        </div>
    </div>
</body>
</html>
