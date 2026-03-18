<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 콘텐츠 목록</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: #f5f5f5;
            padding: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 30px;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #f0f0f0;
            padding-bottom: 20px;
        }
        .header h1 {
            color: #333;
            font-size: 28px;
        }
        .nav-links {
            display: flex;
            gap: 10px;
        }
        .btn {
            display: inline-block;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-primary {
            background: #667eea;
            color: white;
        }
        .btn-primary:hover {
            background: #5568d3;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-secondary {
            background: #f0f0f0;
            color: #333;
        }
        .btn-secondary:hover {
            background: #e0e0e0;
        }
        .btn-danger {
            background: #ff6b6b;
            color: white;
            padding: 6px 12px;
            font-size: 13px;
        }
        .btn-danger:hover {
            background: #ff5252;
        }
        .btn-small {
            padding: 6px 12px;
            font-size: 13px;
        }
        .table-container {
            overflow-x: auto;
            margin-top: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        thead {
            background: #f8f9fa;
            border-bottom: 2px solid #ddd;
        }
        th {
            padding: 15px;
            text-align: left;
            font-weight: 600;
            color: #333;
        }
        td {
            padding: 15px;
            border-bottom: 1px solid #eee;
        }
        tr:hover {
            background: #f9f9f9;
        }
        .content-title {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
        .content-title:hover {
            text-decoration: underline;
        }
        .badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 3px;
            font-size: 12px;
            font-weight: 500;
        }
        .badge-video {
            background: #e3f2fd;
            color: #1976d2;
        }
        .badge-document {
            background: #f3e5f5;
            color: #7b1fa2;
        }
        .badge-link {
            background: #e0f2f1;
            color: #00796b;
        }
        .actions {
            display: flex;
            gap: 5px;
        }
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #999;
        }
        .empty-state p {
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div>
                <h1>📺 콘텐츠 목록</h1>
            </div>
            <div class="nav-links">
                <a href="<%= request.getContextPath() %>/" class="btn btn-secondary">홈</a>
                <a href="<%= request.getContextPath() %>/user/logout" class="btn btn-secondary">로그아웃</a>
            </div>
        </div>

        <% 
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            boolean isAdminUser = isAdmin != null && isAdmin;
        %>

        <% if (isAdminUser) { %>
        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/content/create" class="btn btn-primary">+ 새 콘텐츠 등록</a>
        </div>
        <% } %>

        <div class="table-container">
            <c:if test="${not empty contents}">
                <table>
                    <thead>
                        <tr>
                            <th style="width: 30%;">제목</th>
                            <th style="width: 15%;">유형</th>
                            <th style="width: 15%;">재생시간</th>
                            <th style="width: 15%;">등록일</th>
                            <th style="width: 25%;">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="content" items="${contents}">
                            <tr>
                                <td>
                                    <a href="<%= request.getContextPath() %>/content/${content.contentId}" class="content-title">
                                        ${content.contentTitle}
                                    </a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${content.contentType eq 'VIDEO'}">
                                            <span class="badge badge-video">동영상</span>
                                        </c:when>
                                        <c:when test="${content.contentType eq 'DOCUMENT'}">
                                            <span class="badge badge-document">문서</span>
                                        </c:when>
                                        <c:when test="${content.contentType eq 'LINK'}">
                                            <span class="badge badge-link">링크</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge">${content.contentType}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${content.durationMinutes}분</td>
                                <td>${content.regDt}</td>
                                <td>
                                    <div class="actions">
                                        <a href="<%= request.getContextPath() %>/content/${content.contentId}" class="btn btn-secondary btn-small">조회</a>
                                        <% if (isAdminUser) { %>
                                        <a href="<%= request.getContextPath() %>/content/${content.contentId}/edit" class="btn btn-secondary btn-small">수정</a>
                                        <form method="POST" action="<%= request.getContextPath() %>/content/${content.contentId}" style="display: inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                            <input type="hidden" name="_method" value="DELETE">
                                            <button type="submit" class="btn btn-danger btn-small">삭제</button>
                                        </form>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>

            <c:if test="${empty contents}">
                <div class="empty-state">
                    <p>📭 등록된 콘텐츠가 없습니다.</p>
                    <% if (isAdminUser) { %>
                    <a href="<%= request.getContextPath() %>/content/create" class="btn btn-primary">첫 콘텐츠 등록하기</a>
                    <% } %>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html>
