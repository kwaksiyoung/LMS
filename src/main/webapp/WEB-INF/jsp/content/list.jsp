<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 콘텐츠 목록</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/content.css">
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
