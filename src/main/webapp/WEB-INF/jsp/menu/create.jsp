<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>메뉴 등록</title>
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
            background-color: #28a745;
            color: white;
            flex: 1;
        }
        
        .btn-primary:hover {
            background-color: #218838;
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
        
        @media (max-width: 600px) {
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .checkbox-group {
                grid-template-columns: 1fr;
            }
        }

    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    <jsp:include page="/WEB-INF/jsp/layout/navigation.jsp" />
    
    <div class="container">
        <div class="header">
            <h1>➕ 새 메뉴 등록</h1>
            <p>새로운 메뉴를 등록하고 접근 가능한 역할을 선택합니다.</p>
        </div>
        
        <c:if test="${not empty errorMessage}">
            <div class="error-message">${errorMessage}</div>
        </c:if>
        
        <div class="form-card">
            <form method="post" action="${pageContext.request.contextPath}/menu">
                <div class="form-row">
                    <div class="form-group">
                        <label for="menuId">메뉴 ID <span class="required">*</span></label>
                        <input type="text" id="menuId" name="menuId" placeholder="예: MENU_LECTURE" required>
                        <div class="form-help">영문 대문자와 언더스코어(_)만 사용 가능합니다. 예: MENU_LECTURE, MENU_COURSE</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="menuNm">메뉴명 <span class="required">*</span></label>
                        <input type="text" id="menuNm" name="menuNm" placeholder="예: 강의 관리" required>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="menuUrl">메뉴 URL <span class="required">*</span></label>
                    <input type="text" id="menuUrl" name="menuUrl" placeholder="예: /lecture" required>
                    <div class="form-help">예: /lecture, /content, /user/management</div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="menuIcon">아이콘</label>
                        <div class="icon-input">
                            <input type="text" id="menuIcon" name="menuIcon" placeholder="예: fa-book">
                            <div class="icon-preview" id="iconPreview">😀</div>
                        </div>
                        <div class="form-help">Font Awesome 아이콘 클래스 또는 이모지를 사용할 수 있습니다.</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="sortOrder">정렬 순서</label>
                        <input type="number" id="sortOrder" name="sortOrder" value="0" min="0">
                        <div class="form-help">숫자가 작을수록 앞에 표시됩니다.</div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="parentMenuId">부모 메뉴</label>
                    <select id="parentMenuId" name="parentMenuId">
                        <option value="">부모 메뉴 없음 (최상위)</option>
                        <c:forEach var="parentMenu" items="${parentMenus}">
                            <option value="${parentMenu.menuId}">${parentMenu.menuNm}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="roleSelect">접근 가능한 역할 <span class="required">*</span></label>
                    <div class="checkbox-group">
                        <c:forEach var="role" items="${roles}">
                            <div class="checkbox-item">
                                <input type="checkbox" id="role_${role.roleCd}" name="selectedRoles" value="${role.roleCd}">
                                <label for="role_${role.roleCd}">${role.roleNm}</label>
                            </div>
                        </c:forEach>
                    </div>
                    <div class="form-help">이 메뉴에 접근할 수 있는 역할을 선택하세요. 최소 1개 이상 선택해야 합니다.</div>
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">✅ 메뉴 등록</button>
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
