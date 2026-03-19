<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 강의 상세</title>
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
        <div class="container">
            <div class="page-header">
                <h1>📖 강의 상세 정보</h1>
            </div>

        <c:if test="${not empty errorMessage}">
            <div class="alert alert-error">
                ${errorMessage}
            </div>
        </c:if>

        <c:if test="${not empty lecture}">
            <% 
                Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
                boolean isAdminUser = isAdmin != null && isAdmin;
            %>

            <div class="lecture-detail">
                <div class="detail-section">
                    <div class="detail-header">
                        <h2>${lecture.lectureNm}</h2>
                        <c:if test="${isAdminUser}">
                        <div class="detail-actions">
                            <a href="<%= request.getContextPath() %>/lecture/edit?lectureId=${lecture.lectureId}" class="btn btn-secondary">수정</a>
                            <form method="POST" action="<%= request.getContextPath() %>/lecture/delete" style="display: inline;" 
                                  onsubmit="return confirm('정말 삭제하시겠습니까?');">
                                <input type="hidden" name="lectureId" value="${lecture.lectureId}">
                                <button type="submit" class="btn btn-danger">삭제</button>
                            </form>
                        </div>
                        </c:if>
                    </div>

                    <div class="detail-info">
                        <div class="info-row">
                            <span class="info-label">강의 ID:</span>
                            <span class="info-value">${lecture.lectureId}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">강의명:</span>
                            <span class="info-value">${lecture.lectureNm}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">강의 설명:</span>
                            <span class="info-value">${lecture.lectureDesc}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">강의 시간:</span>
                            <span class="info-value">
                                <c:if test="${not empty lecture.durationMinutes}">
                                    ${lecture.durationMinutes}분
                                </c:if>
                                <c:if test="${empty lecture.durationMinutes}">
                                    지정되지 않음
                                </c:if>
                            </span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">사용 여부:</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${lecture.useYn eq 'Y'}">
                                        <span class="badge badge-active">사용</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-inactive">미사용</span>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">차시 유형:</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${lecture.lectureType eq 'REQUIRED'}">
                                        <span class="badge" style="background-color: #d4edda; color: #155724;">필수 차시</span>
                                    </c:when>
                                    <c:when test="${lecture.lectureType eq 'OPTIONAL'}">
                                        <span class="badge" style="background-color: #fff3cd; color: #856404;">선택 차시</span>
                                    </c:when>
                                    <c:otherwise>
                                        ${lecture.lectureType}
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">등록일:</span>
                            <span class="info-value">${lecture.regDt}</span>
                        </div>
                        <div class="info-row">
                            <span class="info-label">수정일:</span>
                            <span class="info-value">${lecture.updDt}</span>
                        </div>
                    </div>
                </div>

                <div class="detail-section">
                    <div class="detail-header">
                        <h3>🎬 차시 구성</h3>
                        <% if (isAdminUser) { %>
                        <div class="detail-actions">
                            <button class="btn btn-primary btn-small" onclick="openContentModal('${lecture.lectureId}')">+ 차시 추가</button>
                        </div>
                        <% } %>
                    </div>
                    
                    <c:if test="${not empty lecture.contents}">
                        <div class="content-list lecture-content-list">
                            <c:forEach var="content" items="${lecture.contents}" varStatus="status">
                                <div class="content-item lecture-content-item" data-content-id="${content.contentId}">
                                    <div class="content-order">
                                        <strong>${status.index + 1}</strong>
                                    </div>
                                    <div class="content-header">
                                        <span class="content-title">${content.contentTitle}</span>
                                        <c:if test="${not empty content.contentType}">
                                            <span class="badge badge-${content.contentType eq 'VIDEO' ? 'video' : 'document'}">
                                                <c:choose>
                                                    <c:when test="${content.contentType eq 'VIDEO'}">동영상</c:when>
                                                    <c:when test="${content.contentType eq 'DOCUMENT'}">문서</c:when>
                                                    <c:when test="${content.contentType eq 'LINK'}">링크</c:when>
                                                    <c:otherwise>${content.contentType}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </c:if>
                                    </div>
                                    <p class="content-desc">${content.contentDesc}</p>
                                    <div class="content-meta">
                                        <c:if test="${not empty content.durationMinutes}">
                                            <span>⏱️ ${content.durationMinutes}분</span>
                                        </c:if>
                                        <span>📅 ${content.regDt}</span>
                                    </div>
                                    <% if (isAdminUser) { %>
                                    <div class="content-actions">
                                        <button class="btn btn-secondary btn-small" onclick="moveContentUp('${lecture.lectureId}', '${content.contentId}', ${status.index})">⬆️ 위로</button>
                                        <button class="btn btn-secondary btn-small" onclick="moveContentDown('${lecture.lectureId}', '${content.contentId}', ${status.index})">⬇️ 아래로</button>
                                        <button class="btn btn-danger btn-small" onclick="removeContent('${lecture.lectureId}', '${content.contentId}')">🗑️ 제거</button>
                                    </div>
                                    <% } %>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${empty lecture.contents}">
                        <div class="empty-state">
                            <p>📭 등록된 차시가 없습니다.</p>
                            <% if (isAdminUser) { %>
                            <button class="btn btn-primary" onclick="openContentModal('${lecture.lectureId}')">첫 차시 추가하기</button>
                            <% } %>
                        </div>
                    </c:if>
                </div>

                <!-- 콘텐츠 선택 모달 (관리자용) -->
                <% if (isAdminUser) { %>
                <div id="contentModal" class="modal" style="display: none;">
                    <div class="modal-content" style="max-width: 600px;">
                        <div class="modal-header">
                            <h2>차시 추가</h2>
                            <button class="close-btn" onclick="closeContentModal()">&times;</button>
                        </div>
                        <div class="modal-body">
                            <p style="color: #666; margin-bottom: 15px;">등록된 콘텐츠 중에서 선택하고 차시 제목을 입력하세요.</p>
                            
                            <!-- 콘텐츠 선택 섹션 (JSP MVC 패턴: 미리 로드된 콘텐츠 사용) -->
                            <div style="margin-bottom: 20px;">
                                <label style="display: block; margin-bottom: 10px; font-weight: bold;">콘텐츠 선택</label>
                                <div id="contentList" style="max-height: 300px; overflow-y: auto; border: 1px solid #ddd; border-radius: 5px; padding: 10px;">
                                    <% 
                                        java.util.List<kr.co.lms.vo.ContentVO> availableContents = 
                                            (java.util.List<kr.co.lms.vo.ContentVO>) request.getAttribute("availableContents");
                                        
                                        if (availableContents != null && availableContents.size() > 0) {
                                            for (kr.co.lms.vo.ContentVO content : availableContents) {
                                    %>
                                        <label class="content-checkbox">
                                            <input type="checkbox" value="<%= content.getContentId() %>" />
                                            <strong><%= content.getContentTitle() != null ? content.getContentTitle() : "(제목 없음)" %></strong> <br/>
                                            <small style="color: #666;"><%= content.getContentDesc() != null ? content.getContentDesc() : "" %></small>
                                            <small style="color: #999; display: block; margin-top: 5px;">
                                                유형: <%= content.getContentType() != null ? content.getContentType() : "-" %> | 시간: <%= content.getDurationMinutes() != null ? content.getDurationMinutes() : "-" %>분
                                            </small>
                                        </label>
                                    <% 
                                            }
                                        } else {
                                    %>
                                        <p style="color: #999; padding: 20px; text-align: center;">사용 중인 콘텐츠가 없습니다.<br/><small>콘텐츠 관리에서 콘텐츠를 등록해주세요.</small></p>
                                    <% 
                                        }
                                    %>
                                </div>
                            </div>
                            
                            <!-- 차시 제목 입력 섹션 -->
                            <div style="margin-bottom: 20px;">
                                <label for="lectureContentTitle" style="display: block; margin-bottom: 10px; font-weight: bold;">차시 제목 (선택사항)</label>
                                <input type="text" id="lectureContentTitle" placeholder="예: 1. 환경 설정, 2. 기본 개념" 
                                       style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;">
                                <p style="color: #999; font-size: 12px; margin-top: 5px;">차시에 대한 구분 제목을 입력하면 학습 화면에서 표시됩니다.</p>
                            </div>

                            <!-- 차시 설명 입력 섹션 -->
                            <div>
                                <label for="lectureContentDesc" style="display: block; margin-bottom: 10px; font-weight: bold;">차시 설명 (선택사항)</label>
                                <textarea id="lectureContentDesc" placeholder="이 차시에 대한 간단한 설명을 입력하세요." 
                                         style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; height: 80px; resize: vertical;"></textarea>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button class="btn btn-primary" onclick="addSelectedContent('${lecture.lectureId}')">선택한 차시 추가</button>
                            <button class="btn btn-secondary" onclick="closeContentModal()">취소</button>
                        </div>
                    </div>
                </div>
                <% } %>
                
            </div>
        </c:if>

        <c:if test="${empty lecture}">
            <div class="alert alert-error">
                강의 정보를 찾을 수 없습니다.
            </div>
            <a href="<%= request.getContextPath() %>/lecture/list" class="btn btn-secondary">목록으로 돌아가기</a>
        </c:if>
        </div>
    </main>



    <script>
        const contextPath = '<%= request.getContextPath() %>';

        function openContentModal(lectureId) {
            // JSP MVC 패턴: 이미 로드된 콘텐츠 목록 사용
            // (availableContents는 JSP에서 model.addAttribute()로 전달됨)
            
            const contentList = document.getElementById('contentList');
            if (!contentList) {
                console.error('contentList 엘리먼트를 찾을 수 없습니다.');
                alert('모달 초기화 오류: contentList 엘리먼트를 찾을 수 없습니다.');
                return;
            }
            
            // 모달 열기 전 콘텐츠 목록 초기화
            contentList.innerHTML = '';
            
            console.log('콘텐츠 모달 열기: JSP MVC 패턴 (미리 로드된 콘텐츠 사용)');
            
            // 모달 열기
            const contentModal = document.getElementById('contentModal');
            if (!contentModal) {
                console.error('contentModal 엘리먼트를 찾을 수 없습니다.');
                alert('모달 초기화 오류: contentModal 엘리먼트를 찾을 수 없습니다.');
                return;
            }
            contentModal.style.display = 'flex';
        }

        function closeContentModal() {
            document.getElementById('contentModal').style.display = 'none';
        }

        function addSelectedContent(lectureId) {
            const checkboxes = document.querySelectorAll('#contentList input[type="checkbox"]:checked');
            if (checkboxes.length === 0) {
                alert('추가할 차시를 선택해주세요.');
                return;
            }

            // 차시 제목과 설명 가져오기
            const lectureContentTitle = document.getElementById('lectureContentTitle').value;
            const lectureContentDesc = document.getElementById('lectureContentDesc').value;

            const contentIds = Array.from(checkboxes).map(cb => cb.value);
            
            // 각 콘텐츠를 추가
            let addedCount = 0;
            contentIds.forEach((contentId, index) => {
                fetch(contextPath + '/api/v1/lectures/' + lectureId + '/contents/' + contentId, {
                    method: 'POST',
                    credentials: 'include',  // JWT 토큰 포함
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        contentOrder: index,
                        lectureContentTitle: lectureContentTitle || null,
                        lectureContentDesc: lectureContentDesc || null
                    })
                })
                .then(response => {
                    if (response.ok) {
                        addedCount++;
                        if (addedCount === contentIds.length) {
                            alert('차시가 추가되었습니다. 페이지를 새로고침합니다.');
                            location.reload();
                        }
                    }
                })
                .catch(error => console.error('추가 실패:', error));
            });
        }

        function removeContent(lectureId, contentId) {
            if (!confirm('이 차시를 제거하시겠습니까?')) return;

            fetch(contextPath + '/api/v1/lectures/' + lectureId + '/contents/' + contentId, {
                method: 'DELETE',
                credentials: 'include',  // JWT 토큰 포함
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => {
                if (response.ok) {
                    alert('차시가 제거되었습니다.');
                    location.reload();
                } else {
                    alert('차시 제거에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('제거 실패:', error);
                alert('차시를 제거할 수 없습니다.');
            });
        }

        function moveContentUp(lectureId, contentId, index) {
            if (index === 0) {
                alert('맨 위로 이동할 수 없습니다.');
                return;
            }
            reorderContent(lectureId, contentId, index - 1);
        }

        function moveContentDown(lectureId, contentId, index) {
            const totalItems = document.querySelectorAll('.lecture-content-item').length;
            if (index === totalItems - 1) {
                alert('맨 아래로 이동할 수 없습니다.');
                return;
            }
            reorderContent(lectureId, contentId, index + 1);
        }

        function reorderContent(lectureId, contentId, newOrder) {
            fetch(contextPath + '/api/v1/lectures/' + lectureId + '/contents/' + contentId + '/order', {
                method: 'PUT',
                credentials: 'include',  // JWT 토큰 포함
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ contentOrder: newOrder })
            })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert('순서 변경에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('순서 변경 실패:', error);
                alert('순서를 변경할 수 없습니다.');
            });
        }

        // 모달 외부 클릭 시 닫기
        window.onclick = function(event) {
            const modal = document.getElementById('contentModal');
            if (event.target === modal) {
                modal.style.display = 'none';
            }
        }
    </script>
</body>
</html>
