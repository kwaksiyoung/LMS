<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LMS - 강의 상세</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/lecture.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/nav.css">
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
            <c:set var="isAdminUser" value="${sessionScope.isAdmin}"/>

            <div class="lecture-detail">
                <div class="detail-section">
                    <div class="detail-header">
                        <h2>${lecture.lectureNm}</h2>
                        <c:if test="${isAdminUser}">
                        <div class="detail-actions">
                            <a href="${pageContext.request.contextPath}/lecture/edit?lectureId=${lecture.lectureId}" class="btn btn-secondary">수정</a>
                            <form method="POST" action="${pageContext.request.contextPath}/lecture/delete" style="display: inline;" 
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
                        <c:if test="${isAdminUser}">
                        <div class="detail-actions">
                            <button class="btn btn-primary btn-small" onclick="openContentModal('${lecture.lectureId}')">+ 차시 추가</button>
                        </div>
                        </c:if>
                    </div>
                    
                    <c:if test="${not empty lecture.contents}">
                        <div class="content-list lecture-content-list">
                            <c:forEach var="content" items="${lecture.contents}" varStatus="status">
                                <div class="content-item lecture-content-item" data-content-id="${content.contentId}" style="padding: 15px; border: 1px solid #ddd; border-radius: 6px; margin-bottom: 12px;">
                                    <div style="display: flex; align-items: center; gap: 15px;">
                                        <div class="content-order" style="min-width: 40px;">
                                            <strong style="font-size: 18px; color: #667eea;">${status.index + 1}</strong>
                                        </div>
                                        <div style="flex: 1;">
                                            <c:if test="${not empty content.lectureContentTitle}">
                                                <div style="font-weight: bold; font-size: 16px; color: #2c3e50; margin-bottom: 6px;">
                                                    📌 ${content.lectureContentTitle}
                                                </div>
                                            </c:if>
                                            <c:if test="${not empty content.durationMinutes}">
                                                <div style="color: #666; font-size: 14px;">
                                                    ⏱️ 재생 시간: <strong>${content.durationMinutes}분</strong>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    <c:if test="${isAdminUser}">
                                    <div class="content-actions">
                                        <form method="POST" action="${pageContext.request.contextPath}/lecture/reorderContent" style="display: inline;">
                                            <input type="hidden" name="lectureId" value="${lecture.lectureId}">
                                            <input type="hidden" name="contentId" value="${content.contentId}">
                                            <input type="hidden" name="newOrder" value="${status.index - 1}">
                                            <c:if test="${status.index > 0}">
                                            <button type="submit" class="btn btn-secondary btn-small">⬆️ 위로</button>
                                            </c:if>
                                        </form>
                                        <form method="POST" action="${pageContext.request.contextPath}/lecture/reorderContent" style="display: inline;">
                                            <input type="hidden" name="lectureId" value="${lecture.lectureId}">
                                            <input type="hidden" name="contentId" value="${content.contentId}">
                                            <input type="hidden" name="newOrder" value="${status.index + 1}">
                                            <c:if test="${status.index < lecture.contents.size() - 1}">
                                            <button type="submit" class="btn btn-secondary btn-small">⬇️ 아래로</button>
                                            </c:if>
                                        </form>
                                        <form method="POST" action="${pageContext.request.contextPath}/lecture/removeContent" style="display: inline;" onsubmit="return confirm('이 차시를 제거하시겠습니까?');">
                                            <input type="hidden" name="lectureId" value="${lecture.lectureId}">
                                            <input type="hidden" name="contentId" value="${content.contentId}">
                                            <button type="submit" class="btn btn-danger btn-small">🗑️ 제거</button>
                                        </form>
                                    </div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:if>
                    <c:if test="${empty lecture.contents}">
                        <div class="empty-state">
                            <p>📭 등록된 차시가 없습니다.</p>
                            <c:if test="${isAdminUser}">
                            <button class="btn btn-primary" onclick="openContentModal('${lecture.lectureId}')">첫 차시 추가하기</button>
                            </c:if>
                        </div>
                    </c:if>
                </div>

                <!-- 콘텐츠 선택 모달 (관리자용) -->
                <c:if test="${isAdminUser}">
                <div id="contentModal" class="modal" style="display: none;">
                    <div class="modal-content" style="max-width: 600px;">
                        <form id="contentForm" method="POST" action="${pageContext.request.contextPath}/lecture/addContent" onsubmit="return validateForm()">
                            <div class="modal-header">
                                <h2>차시 추가</h2>
                                <button type="button" class="close-btn" onclick="closeContentModal()">&times;</button>
                            </div>
                            <div class="modal-body">
                                <p style="color: #666; margin-bottom: 15px;">등록된 콘텐츠 중에서 선택하고 차시 제목을 입력하세요.</p>
                                
                                <!-- 숨겨진 lectureId 필드 -->
                                <input type="hidden" name="lectureId" value="${lecture.lectureId}">
                                
                                <!-- 콘텐츠 선택 섹션 (JSP MVC 패턴: 미리 로드된 콘텐츠 사용) -->
                                <div style="margin-bottom: 20px;">
                                    <label style="display: block; margin-bottom: 10px; font-weight: bold;">콘텐츠 선택</label>
                                    <div id="contentList" style="max-height: 300px; overflow-y: auto; border: 1px solid #ddd; border-radius: 5px; padding: 10px;">
                                        <c:choose>
                                            <c:when test="${not empty availableContents}">
                                                 <c:forEach var="content" items="${availableContents}">
                                                    <label style="display: block; padding: 10px; margin-bottom: 8px; border: 1px solid #e0e0e0; border-radius: 4px; cursor: pointer; transition: background 0.2s;">
                                                        <input type="radio" name="contentIds" value="${content.contentId}" style="margin-right: 8px;" />
                                                        <strong>${not empty content.contentTitle ? content.contentTitle : '(제목 없음)'}</strong>
                                                        <c:if test="${not empty content.durationMinutes}">
                                                            <span style="color: #999; margin-left: 10px;">⏱️ ${content.durationMinutes}분</span>
                                                        </c:if>
                                                    </label>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <p style="color: #999; padding: 20px; text-align: center;">사용 중인 콘텐츠가 없습니다.<br/><small>콘텐츠 관리에서 콘텐츠를 등록해주세요.</small></p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <!-- 차시 제목 입력 섹션 -->
                                <div style="margin-bottom: 20px;">
                                    <label for="lectureContentTitle" style="display: block; margin-bottom: 10px; font-weight: bold;">차시 제목 (선택사항)</label>
                                    <input type="text" id="lectureContentTitle" name="lectureContentTitle" placeholder="예: 1. 환경 설정, 2. 기본 개념" 
                                           style="width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box;">
                                    <p style="color: #999; font-size: 12px; margin-top: 5px;">차시에 대한 구분 제목을 입력하면 학습 화면에서 표시됩니다.</p>
                                </div>


                            </div>
                            <div class="modal-footer">
                                <button type="submit" class="btn btn-primary">선택한 차시 추가</button>
                                <button type="button" class="btn btn-secondary" onclick="closeContentModal()">취소</button>
                            </div>
                        </form>
                    </div>
                </div>
                </c:if>
                
            </div>
        </c:if>

        <c:if test="${empty lecture}">
            <div class="alert alert-error">
                강의 정보를 찾을 수 없습니다.
            </div>
            <a href="${pageContext.request.contextPath}/lecture/list" class="btn btn-secondary">목록으로 돌아가기</a>
        </c:if>
        </div>
    </main>



    <script>
        const contextPath = '${pageContext.request.contextPath}';

        function openContentModal(lectureId) {
            // JSP MVC 패턴: 이미 로드된 콘텐츠 목록 사용
            // (availableContents는 LectureController.view()에서 model.addAttribute()로 전달됨)
            // JSP에서 렌더링된 HTML은 변경하지 않음 (그대로 유지)
            
            const contentList = document.getElementById('contentList');
            if (!contentList) {
                console.error('contentList 엘리먼트를 찾을 수 없습니다.');
                alert('모달 초기화 오류: contentList 엘리먼트를 찾을 수 없습니다.');
                return;
            }
            
            console.log('콘텐츠 모달 열기: JSP MVC 패턴 (미리 로드된 콘텐츠 사용)');
            console.log('contentList HTML:', contentList.innerHTML.length + '자');
            
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


        function closeContentModal() {
            document.getElementById('contentModal').style.display = 'none';
            // 폼 초기화
            document.getElementById('contentForm').reset();
        }

        function validateForm() {
            const checkboxes = document.querySelectorAll('input[name="contentIds"]:checked');
            if (checkboxes.length === 0) {
                alert('최소 1개의 콘텐츠를 선택하세요.');
                return false;
            }
            return true;
        }

        function toggleContentFields(checkbox) {
            // 현재는 모든 선택 콘텐츠에 동일한 제목/설명을 적용하므로
            // 동적 필드 처리는 필요 없음
            console.log('콘텐츠 선택:', checkbox.value, checkbox.checked);
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
