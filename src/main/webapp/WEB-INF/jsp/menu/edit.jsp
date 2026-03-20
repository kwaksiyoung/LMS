<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메뉴 수정</title>
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
        
        .form-card {
            background: white;
            border-radius: 8px;
            padding: 30px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #333;
        }
        
        .required {
            color: #dc3545;
        }
        
        input[type="text"],
        input[type="number"],
        select,
        textarea {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 14px;
            font-family: inherit;
        }
        
        input[type="text"]:focus,
        input[type="number"]:focus,
        select:focus,
        textarea:focus {
            outline: none;
            border-color: #007bff;
            box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
        }
        
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        input[type="text"]:disabled {
            background-color: #e9ecef;
            color: #6c757d;
        }
        
        .checkbox-group {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 10px;
        }
        
        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 8px;
        }
        
        input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }
        
        .checkbox-item label {
            margin: 0;
            cursor: pointer;
        }
        
        .form-help {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
        }
        
        .form-actions {
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
        
        .btn-primary {
            background-color: #007bff;
            color: white;
            flex: 1;
        }
        
        .btn-primary:hover {
            background-color: #0056b3;
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        
        .error-message {
            background-color: #f8d7da;
            color: #721c24;
            padding: 12px 15px;
            border-radius: 6px;
            margin-bottom: 20px;
        }
        
        .icon-input {
            display: flex;
            gap: 10px;
            align-items: center;
        }
        
        .icon-preview {
            font-size: 24px;
            min-width: 40px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
        
        .info-box {
            background-color: #e7f3ff;
            border-left: 4px solid #007bff;
            padding: 12px 15px;
            margin-bottom: 20px;
            border-radius: 4px;
            font-size: 14px;
        }
        
        @media (max-width: 600px) {
            .form-row {
                grid-template-columns: 1fr;
            }
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
            <h1>✏️ 메뉴 수정</h1>
            <p>메뉴 정보를 수정하고 접근 가능한 역할을 변경합니다.</p>
        </div>
        
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <div class="form-card">
            <div class="info-box">
                📌 메뉴 ID는 변경할 수 없습니다. 메뉴 ID를 변경해야 하는 경우 새로 등록 후 기존 메뉴를 삭제하세요.
            </div>
            
            <form method="post" action="${pageContext.request.contextPath}/menu/${menu.menuId}">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="menuId">메뉴 ID</label>
                        <input type="text" id="menuId" name="menuId" value="${menu.menuId}" disabled>
                        <div class="form-help">변경 불가 필드</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="menuNm">메뉴명 <span class="required">*</span></label>
                        <input type="text" id="menuNm" name="menuNm" value="${menu.menuNm}" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="menuUrl">메뉴 URL <span class="required">*</span></label>
                    <input type="text" id="menuUrl" name="menuUrl" value="${menu.menuUrl}" required>
                    <div class="form-help">예: /lecture, /content, /user/management</div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="menuIcon">아이콘</label>
                        <div class="icon-input">
                            <input type="text" id="menuIcon" name="menuIcon" value="${menu.menuIcon}">
                            <div class="icon-preview" id="iconPreview">${not empty menu.menuIcon ? menu.menuIcon : '😀'}</div>
                        </div>
                        <div class="form-help">Font Awesome 아이콘 클래스 또는 이모지를 사용할 수 있습니다.</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="sortOrder">정렬 순서</label>
                        <input type="number" id="sortOrder" name="sortOrder" value="${menu.sortOrder}" min="0">
                        <div class="form-help">숫자가 작을수록 앞에 표시됩니다.</div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="parentMenuId">부모 메뉴</label>
                    <select id="parentMenuId" name="parentMenuId">
                        <option value="">부모 메뉴 없음 (최상위)</option>
                        <c:forEach var="parentMenu" items="${parentMenus}">
                            <option value="${parentMenu.menuId}" ${parentMenu.menuId == menu.parentMenuId ? 'selected' : ''}>
                                ${parentMenu.menuNm}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="useYn">사용 여부 <span class="required">*</span></label>
                    <select id="useYn" name="useYn" required>
                        <option value="">선택하세요</option>
                        <option value="Y" ${menu.useYn == 'Y' ? 'selected' : ''}>사용</option>
                        <option value="N" ${menu.useYn == 'N' ? 'selected' : ''}>미사용</option>
                    </select>
                    <div class="form-help">메뉴를 사용 또는 미사용으로 설정할 수 있습니다.</div>
                </div>
                
                <div class="form-group">
                    <label for="roleSelect">접근 가능한 역할 <span class="required">*</span></label>
                    <div class="checkbox-group">
                        <c:forEach var="role" items="${roles}">
                            <div class="checkbox-item">
                                <input type="checkbox" id="role_${role.roleCd}" name="selectedRoles" value="${role.roleCd}" 
                                       ${selectedRoles != null && selectedRoles.contains(role.roleCd) ? 'checked' : ''}>
                                <label for="role_${role.roleCd}">${role.roleNm}</label>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="form-help">이 메뉴에 접근할 수 있는 역할을 선택하세요. 최소 1개 이상 선택해야 합니다.</div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">✏️ 메뉴 수정</button>
                    <a href="${pageContext.request.contextPath}/menu/list" class="btn btn-secondary">취소</a>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        // 메뉴 아이콘 미리보기 업데이트
        document.getElementById('menuIcon').addEventListener('change', function() {
            const preview = document.getElementById('iconPreview');
            const value = this.value.trim();
            
            // 이모지 또는 텍스트 표시
            if (value) {
                preview.textContent = value.length === 1 ? value : '📌';
            } else {
                preview.textContent = '😀';
            }
        });
    </script>
</body>
</html>
