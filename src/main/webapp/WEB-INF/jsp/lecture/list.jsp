<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 강의 목록</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/lecture.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/nav.css">
</head>
<body>
    <!-- 공통 헤더 포함 -->
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    
    <!-- 공통 네비게이션 포함 -->
    <jsp:include page="/WEB-INF/jsp/layout/navigation.jsp" />

    <main class="main-content">
        <div class="container container-wide">
            <div class="page-header">
                <h1>📚 강의 목록</h1>
            </div>

        <% 
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            boolean isAdminUser = isAdmin != null && isAdmin;
        %>

        <!-- 검색/필터 폼 -->
        <div class="search-section" style="margin-bottom: 20px; padding: 15px; background: #f9f9f9; border-radius: 8px;">
            <form method="GET" action="<%= request.getContextPath() %>/lecture/list" class="search-form">
                <div style="display: grid; grid-template-columns: 1fr 1fr 1fr auto; gap: 10px; align-items: end;">
                    <!-- 강의명 검색 -->
                    <div>
                        <label for="keyword" style="display: block; margin-bottom: 5px; font-weight: bold;">강의명</label>
                        <input type="text" id="keyword" name="keyword" placeholder="강의명 또는 설명 검색" 
                               value="${keyword}" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                    </div>
                    
                    <!-- 차시 유형 필터 -->
                    <div>
                        <label for="lectureType" style="display: block; margin-bottom: 5px; font-weight: bold;">차시 유형</label>
                        <select id="lectureType" name="lectureType" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                            <option value="">전체</option>
                            <option value="REQUIRED" <c:if test="${lectureType eq 'REQUIRED'}">selected</c:if>>필수</option>
                            <option value="OPTIONAL" <c:if test="${lectureType eq 'OPTIONAL'}">selected</c:if>>선택</option>
                        </select>
                    </div>
                    
                    <!-- 페이지 크기 -->
                    <div>
                        <label for="pageSize" style="display: block; margin-bottom: 5px; font-weight: bold;">페이지당 항목</label>
                        <select id="pageSize" name="pageSize" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                            <option value="10" <c:if test="${pageSize eq 10}">selected</c:if>>10개</option>
                            <option value="20" <c:if test="${pageSize eq 20}">selected</c:if>>20개</option>
                            <option value="50" <c:if test="${pageSize eq 50}">selected</c:if>>50개</option>
                        </select>
                    </div>
                    
                    <!-- 검색 버튼 -->
                    <div>
                        <button type="submit" class="btn btn-primary" style="width: 100%;">🔍 검색</button>
                    </div>
                </div>
                
                <!-- 검색 초기화 링크 -->
                <div style="margin-top: 10px;">
                    <a href="<%= request.getContextPath() %>/lecture/list" class="btn btn-secondary btn-small" style="text-decoration: none;">초기화</a>
                </div>
            </form>
        </div>

        <!-- 검색 결과 정보 -->
        <c:if test="${not empty keyword or not empty lectureType}">
            <div style="margin-bottom: 15px; padding: 10px; background: #e7f3ff; border-left: 4px solid #2196F3; border-radius: 4px;">
                <strong>📊 검색 결과:</strong> 총 <strong>${totalCount}</strong>개의 강의를 찾았습니다.
                <c:if test="${not empty keyword}"> (검색어: "<strong>${keyword}</strong>")</c:if>
                <c:if test="${not empty lectureType}"> (유형: "<strong><c:choose>
                    <c:when test="${lectureType eq 'REQUIRED'}">필수</c:when>
                    <c:when test="${lectureType eq 'OPTIONAL'}">선택</c:when>
                    <c:otherwise>${lectureType}</c:otherwise>
                </c:choose></strong>")</c:if>
            </div>
        </c:if>
        
        <% if (isAdminUser) { %>
        <div style="margin-bottom: 20px;">
            <a href="<%= request.getContextPath() %>/lecture/create" class="btn btn-primary">+ 새 강의 등록</a>
        </div>
        <% } %>

        <div class="table-container">
            <c:if test="${not empty lectureList}">
                <table>
                    <thead>
                        <tr>
                            <th style="width: 25%;">강의명</th>
                            <th style="width: 15%;">차시 유형</th>
                            <th style="width: 10%;">차시 수</th>
                            <th style="width: 12%;">강의 시간</th>
                            <th style="width: 13%;">등록일</th>
                            <th style="width: 25%;">관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="lecture" items="${lectureList}">
                            <tr>
                                <td>
                                    <a href="<%= request.getContextPath() %>/lecture/view?lectureId=${lecture.lectureId}" class="lecture-title">
                                        ${lecture.lectureNm}
                                    </a>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${lecture.lectureType eq 'REQUIRED'}">
                                            <span class="badge" style="background-color: #d4edda; color: #155724;">필수</span>
                                        </c:when>
                                        <c:when test="${lecture.lectureType eq 'OPTIONAL'}">
                                            <span class="badge" style="background-color: #fff3cd; color: #856404;">선택</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge" style="background-color: #e2e3e5; color: #383d41;">-</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td style="text-align: center;">
                                    <c:choose>
                                        <c:when test="${not empty lecture.contentCount}">
                                            <strong>${lecture.contentCount}</strong>개
                                        </c:when>
                                        <c:otherwise>
                                            0개
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${not empty lecture.durationMinutes}">
                                        ${lecture.durationMinutes}분
                                    </c:if>
                                    <c:if test="${empty lecture.durationMinutes}">
                                        -
                                    </c:if>
                                </td>
                                <td>${lecture.regDt}</td>
                                <td>
                                    <div class="actions">
                                        <a href="<%= request.getContextPath() %>/lecture/view?lectureId=${lecture.lectureId}" class="btn btn-secondary btn-small">조회</a>
                                        <% if (isAdminUser) { %>
                                        <a href="<%= request.getContextPath() %>/lecture/edit?lectureId=${lecture.lectureId}" class="btn btn-secondary btn-small">수정</a>
                                        <form method="POST" action="<%= request.getContextPath() %>/lecture/delete" style="display: inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                            <input type="hidden" name="lectureId" value="${lecture.lectureId}">
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

            <c:if test="${empty lectureList}">
                <div class="empty-state">
                    <p>📭 등록된 강의가 없습니다.</p>
                    <% if (isAdminUser) { %>
                    <a href="<%= request.getContextPath() %>/lecture/create" class="btn btn-primary">첫 강의 등록하기</a>
                    <% } %>
                </div>
            </c:if>
            
            <!-- 페이징 네비게이션 -->
            <c:if test="${not empty lectureList and totalPages > 1}">
                <div class="pagination-section" style="margin-top: 30px; text-align: center;">
                    <nav class="pagination" style="display: flex; justify-content: center; gap: 5px; flex-wrap: wrap;">
                        <!-- 이전 페이지 버튼 -->
                        <c:if test="${currentPage > 1}">
                            <a href="<%= request.getContextPath() %>/lecture/list?page=1&pageSize=${pageSize}<c:if test="${not empty keyword}">&keyword=${keyword}</c:if><c:if test="${not empty lectureType}">&lectureType=${lectureType}</c:if>" 
                               class="btn btn-secondary btn-small" style="padding: 8px 10px;">«</a>
                            <a href="<%= request.getContextPath() %>/lecture/list?page=${currentPage - 1}&pageSize=${pageSize}<c:if test="${not empty keyword}">&keyword=${keyword}</c:if><c:if test="${not empty lectureType}">&lectureType=${lectureType}</c:if>" 
                               class="btn btn-secondary btn-small" style="padding: 8px 10px;">‹</a>
                        </c:if>
                        
                        <!-- 페이지 번호들 -->
                        <c:set var="pageStart" value="${currentPage - 4}"/>
                        <c:set var="pageEnd" value="${currentPage + 4}"/>
                        
                        <c:if test="${pageStart < 1}">
                            <c:set var="pageStart" value="1"/>
                        </c:if>
                        <c:if test="${pageEnd > totalPages}">
                            <c:set var="pageEnd" value="${totalPages}"/>
                        </c:if>
                        
                        <c:forEach var="page" begin="${pageStart}" end="${pageEnd}">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span style="padding: 8px 10px; background: #667eea; color: white; border-radius: 4px; font-weight: bold;">
                                        ${page}
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <a href="<%= request.getContextPath() %>/lecture/list?page=${page}&pageSize=${pageSize}<c:if test="${not empty keyword}">&keyword=${keyword}</c:if><c:if test="${not empty lectureType}">&lectureType=${lectureType}</c:if>" 
                                       class="btn btn-secondary btn-small" style="padding: 8px 10px;">
                                        ${page}
                                    </a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                        
                        <!-- 다음 페이지 버튼 -->
                        <c:if test="${currentPage < totalPages}">
                            <a href="<%= request.getContextPath() %>/lecture/list?page=${currentPage + 1}&pageSize=${pageSize}<c:if test="${not empty keyword}">&keyword=${keyword}</c:if><c:if test="${not empty lectureType}">&lectureType=${lectureType}</c:if>" 
                               class="btn btn-secondary btn-small" style="padding: 8px 10px;">›</a>
                            <a href="<%= request.getContextPath() %>/lecture/list?page=${totalPages}&pageSize=${pageSize}<c:if test="${not empty keyword}">&keyword=${keyword}</c:if><c:if test="${not empty lectureType}">&lectureType=${lectureType}</c:if>" 
                               class="btn btn-secondary btn-small" style="padding: 8px 10px;">»</a>
                        </c:if>
                    </nav>
                    
                    <!-- 페이징 정보 -->
                    <div style="margin-top: 15px; color: #666; font-size: 14px;">
                        페이지 <strong>${currentPage}</strong> / <strong>${totalPages}</strong> 
                        (총 <strong>${totalCount}</strong>개 항목)
                    </div>
                </div>
            </c:if>
        </div>
        </div>
    </main>
</body>
</html>
