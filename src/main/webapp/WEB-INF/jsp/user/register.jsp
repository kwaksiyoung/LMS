<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - LMS</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/common.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/register.css">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/nav.css">
</head>
<body class="logged-out">
    <!-- 공통 헤더 포함 (비로그인 상태) -->
    <jsp:include page="/WEB-INF/jsp/layout/header.jsp" />
    <div class="register-container">
        <div class="register-header">
            <h1>회원가입</h1>
            <p>LMS에 새로운 계정을 만들어보세요</p>
        </div>

        <!-- 알림 메시지 -->
        <div id="alertContainer"></div>

        <!-- 회원가입 폼 -->
        <form id="registerForm" onsubmit="handleRegister(event)">

            <!-- 테넌트 선택 -->
            <div class="form-group">
                <label for="tenantId">
                    소속 조직
                    <span class="required">*</span>
                </label>
                <select id="tenantId" name="tenantId" required>
                    <option value="">조직을 선택하세요</option>
                </select>
                <div id="tenantError" class="validation-message"></div>
            </div>

            <!-- 사용자 ID -->
            <div class="form-group">
                <label for="userId">
                    사용자 ID
                    <span class="required">*</span>
                </label>
                <div class="input-wrapper">
                    <input 
                        type="text" 
                        id="userId" 
                        name="userId" 
                        placeholder="4-20자, 영문/숫자/언더스코어"
                        required
                    >
                    <button type="button" class="btn-check" onclick="checkUserIdDuplicate()">
                        중복확인
                    </button>
                </div>
                <div id="userIdError" class="validation-message"></div>
            </div>

            <!-- 비밀번호 -->
            <div class="form-group">
                <label for="password">
                    비밀번호
                    <span class="required">*</span>
                </label>
                <input 
                    type="password" 
                    id="password" 
                    name="password" 
                    placeholder="8자 이상, 영문 대소문자/숫자/특수문자 포함"
                    required
                    onchange="validatePassword()"
                    oninput="checkPasswordStrength()"
                >
                <div class="password-strength">
                    <div>비밀번호 강도:</div>
                    <div class="strength-bar">
                        <div id="strengthBar" class="strength-bar-fill"></div>
                    </div>
                    <div id="strengthText"></div>
                </div>
                <div id="passwordError" class="validation-message"></div>
            </div>

            <!-- 비밀번호 확인 -->
            <div class="form-group">
                <label for="passwordConfirm">
                    비밀번호 확인
                    <span class="required">*</span>
                </label>
                <input 
                    type="password" 
                    id="passwordConfirm" 
                    name="passwordConfirm" 
                    placeholder="비밀번호를 다시 입력하세요"
                    required
                    onchange="validatePassword()"
                >
                <div id="passwordConfirmError" class="validation-message"></div>
            </div>

            <!-- 사용자명 -->
            <div class="form-group">
                <label for="userName">
                    사용자명
                    <span class="required">*</span>
                </label>
                <input 
                    type="text" 
                    id="userName" 
                    name="userName" 
                    placeholder="1-50자"
                    required
                >
                <div id="userNameError" class="validation-message"></div>
            </div>

            <!-- 이메일 -->
            <div class="form-group">
                <label for="email">
                    이메일
                    <span class="required">*</span>
                </label>
                <input 
                    type="email" 
                    id="email" 
                    name="email" 
                    placeholder="user@example.com"
                    required
                >
                <div id="emailError" class="validation-message"></div>
            </div>

            <!-- 전화번호 -->
            <div class="form-group">
                <label for="phone">
                    전화번호
                    <span class="required">*</span>
                </label>
                <input 
                    type="tel" 
                    id="phone" 
                    name="phone" 
                    placeholder="010-1234-5678 또는 01012345678"
                    required
                >
                <div id="phoneError" class="validation-message"></div>
            </div>

            <!-- 주소 -->
            <div class="form-group">
                <label for="address">
                    주소 (선택)
                </label>
                <input 
                    type="text" 
                    id="address" 
                    name="address" 
                    placeholder="주소를 입력하세요"
                >
                <div id="addressError" class="validation-message"></div>
            </div>

            <!-- 버튼 -->
            <div class="form-actions">
                <button type="submit" class="btn btn-register" id="registerBtn">
                    회원가입
                </button>
                <button type="reset" class="btn btn-cancel">
                    초기화
                </button>
            </div>
        </form>

        <!-- 로그인 링크 -->
        <div class="login-link">
            이미 회원이신가요? <a href="<%= request.getContextPath() %>/auth/login">로그인</a>
        </div>
    </div>

    <script>
        // Context Path
        const contextPath = '${pageContext.request.contextPath}';

        // ========== 초기화 ==========
        window.addEventListener('DOMContentLoaded', () => {
            loadTenants();
        });

        // ========== 테넌트 목록 로드 ==========
        async function loadTenants() {
            try {
                const response = await fetch(contextPath + '/api/v1/tenants');
                const result = await response.json();

                if (result.success && result.data) {
                    const tenantSelect = document.getElementById('tenantId');
                    result.data.forEach(tenant => {
                        const option = document.createElement('option');
                        option.value = tenant.tenantId;
                        option.textContent = tenant.tenantName || tenant.tenantId;
                        tenantSelect.appendChild(option);
                    });
                }
            } catch (error) {
                console.error('테넌트 로드 실패:', error);
                showAlert('테넌트 정보를 로드할 수 없습니다.', 'error');
            }
        }

        // ========== ID 중복 확인 ==========
        async function checkUserIdDuplicate() {
            const userId = document.getElementById('userId').value.trim();
            const tenantId = document.getElementById('tenantId').value;
            const userIdError = document.getElementById('userIdError');

            // 입력값 검증
            if (!userId) {
                showValidationError('userIdError', '사용자 ID를 입력하세요.');
                return;
            }

            if (!/^[a-zA-Z0-9_]{4,20}$/.test(userId)) {
                showValidationError('userIdError', '사용자 ID는 4-20자이며 영문, 숫자, 언더스코어만 사용 가능합니다.');
                return;
            }

            if (!tenantId) {
                showValidationError('tenantError', '조직을 먼저 선택하세요.');
                return;
            }

            try {
                const response = await fetch(contextPath + '/api/v1/auth/check-userid', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ userId, tenantId })
                });

                const result = await response.json();

                if (result.data.isDuplicate) {
                    showValidationError('userIdError', '이미 사용 중인 아이디입니다.');
                } else {
                    userIdError.textContent = '✓ 사용 가능한 아이디입니다.';
                    userIdError.className = 'validation-message success';
                }
            } catch (error) {
                console.error('중복 확인 실패:', error);
                showValidationError('userIdError', '중복 확인 중 오류가 발생했습니다.');
            }
        }

        // ========== 비밀번호 강도 확인 ==========
        function checkPasswordStrength() {
            const password = document.getElementById('password').value;
            const strengthBar = document.getElementById('strengthBar');
            const strengthText = document.getElementById('strengthText');

            let strength = 0;
            let strengthLabel = '';

            // 길이 확인
            if (password.length >= 8) strength += 25;
            if (password.length >= 12) strength += 25;

            // 영문 소문자
            if (/[a-z]/.test(password)) strength += 15;

            // 영문 대문자
            if (/[A-Z]/.test(password)) strength += 15;

            // 숫자
            if (/\d/.test(password)) strength += 10;

            // 특수문자
            if (/[@$!%*?&]/.test(password)) strength += 10;

            // 강도 레벨 결정
            if (strength === 0) {
                strengthBar.className = 'strength-bar-fill';
                strengthText.textContent = '';
            } else if (strength < 50) {
                strengthBar.className = 'strength-bar-fill weak';
                strengthText.textContent = '약함 (더 많은 종류의 문자를 사용하세요)';
            } else if (strength < 80) {
                strengthBar.className = 'strength-bar-fill medium';
                strengthText.textContent = '중간 (조금 더 복잡하게 만들어보세요)';
            } else {
                strengthBar.className = 'strength-bar-fill strong';
                strengthText.textContent = '강함 (좋은 비밀번호입니다)';
            }
        }

        // ========== 비밀번호 검증 ==========
        function validatePassword() {
            const password = document.getElementById('password').value;
            const passwordConfirm = document.getElementById('passwordConfirm').value;
            const passwordError = document.getElementById('passwordError');
            const passwordConfirmError = document.getElementById('passwordConfirmError');

            clearValidationError('passwordError');
            clearValidationError('passwordConfirmError');

            // 비밀번호 형식 검증
            const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[a-zA-Z\d!@#$%^&*]{8,}$/;

            if (password && !passwordPattern.test(password)) {
                showValidationError('passwordError', 
                    '비밀번호는 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.');
            }

            // 비밀번호 일치 확인
            if (password && passwordConfirm && password !== passwordConfirm) {
                showValidationError('passwordConfirmError', '비밀번호가 일치하지 않습니다.');
            }
        }

        // ========== 회원가입 제출 ==========
        async function handleRegister(event) {
            event.preventDefault();
            console.log('회원가입 버튼 클릭됨');

            // 모든 필드 검증
            const isValid = validateForm();
            console.log('폼 검증 결과:', isValid);
            if (!isValid) {
                console.log('폼 검증 실패 - 제출 중단');
                return;
            }

            const formData = {
                userId: document.getElementById('userId').value.trim(),
                password: document.getElementById('password').value,
                passwordConfirm: document.getElementById('passwordConfirm').value,
                userName: document.getElementById('userName').value.trim(),
                email: document.getElementById('email').value.trim(),
                phone: document.getElementById('phone').value.trim(),
                address: document.getElementById('address').value.trim(),
                tenantId: document.getElementById('tenantId').value
            };

            // 비밀번호 일치 확인
            if (formData.password !== formData.passwordConfirm) {
                showAlert('비밀번호가 일치하지 않습니다.', 'error');
                return;
            }

            // 로딩 상태
            const registerBtn = document.getElementById('registerBtn');
            registerBtn.disabled = true;
            registerBtn.textContent = '가입 중...';

            try {
                const response = await fetch(contextPath + '/api/v1/auth/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(formData)
                });

                const result = await response.json();

                if (result.success) {
                    showAlert('회원가입이 완료되었습니다! 로그인해주세요.', 'success');
                    
                    // 2초 후 로그인 페이지로 이동
                    setTimeout(() => {
                        window.location.href = contextPath + '/auth/login';
                    }, 2000);
                } else {
                    showAlert(result.message || '회원가입에 실패했습니다.', 'error');
                }
            } catch (error) {
                console.error('회원가입 실패:', error);
                showAlert('회원가입 중 오류가 발생했습니다.', 'error');
            } finally {
                registerBtn.disabled = false;
                registerBtn.textContent = '회원가입';
            }
        }

        // ========== 폼 검증 ==========
        function validateForm() {
            clearAllErrors();

            const tenantId = document.getElementById('tenantId').value;
            const userId = document.getElementById('userId').value.trim();
            const password = document.getElementById('password').value;
            const passwordConfirm = document.getElementById('passwordConfirm').value;
            const userName = document.getElementById('userName').value.trim();
            const email = document.getElementById('email').value.trim();
            const phone = document.getElementById('phone').value.trim();

            console.log('=== 폼 검증 시작 ===');
            console.log('tenantId:', tenantId);
            console.log('userId:', userId);
            console.log('password:', password);
            console.log('passwordConfirm:', passwordConfirm);
            console.log('userName:', userName);
            console.log('email:', email);
            console.log('phone:', phone);

            let isValid = true;

            // 테넌트 검증
            if (!tenantId) {
                console.log('❌ 테넌트 선택 안 됨');
                showValidationError('tenantError', '조직을 선택하세요.');
                isValid = false;
            } else {
                console.log('✓ 테넌트 선택됨');
            }

            // 사용자 ID 검증
            if (!userId) {
                console.log('❌ userId 미입력');
                showValidationError('userIdError', '사용자 ID를 입력하세요.');
                isValid = false;
            } else if (!/^[a-zA-Z0-9_]{4,20}$/.test(userId)) {
                console.log('❌ userId 형식 오류:', userId);
                showValidationError('userIdError', 
                    '사용자 ID는 4-20자이며 영문, 숫자, 언더스코어만 사용 가능합니다.');
                isValid = false;
            } else {
                console.log('✓ userId 검증 통과');
            }

            // 비밀번호 검증
            if (!password) {
                console.log('❌ password 미입력');
                showValidationError('passwordError', '비밀번호를 입력하세요.');
                isValid = false;
            } else if (!/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*])[a-zA-Z\d!@#$%^&*]{8,}$/.test(password)) {
                console.log('❌ password 형식 오류:', password);
                showValidationError('passwordError', 
                    '비밀번호는 영문 대소문자, 숫자, 특수문자를 모두 포함해야 합니다.');
                isValid = false;
            } else {
                console.log('✓ password 검증 통과');
            }

            // 비밀번호 확인 검증
            if (!passwordConfirm) {
                console.log('❌ passwordConfirm 미입력');
                showValidationError('passwordConfirmError', '비밀번호 확인을 입력하세요.');
                isValid = false;
            } else if (password !== passwordConfirm) {
                console.log('❌ 비밀번호 일치 안 함');
                showValidationError('passwordConfirmError', '비밀번호가 일치하지 않습니다.');
                isValid = false;
            } else {
                console.log('✓ passwordConfirm 검증 통과');
            }

            // 사용자명 검증
            if (!userName) {
                console.log('❌ userName 미입력');
                showValidationError('userNameError', '사용자명을 입력하세요.');
                isValid = false;
            } else if (userName.length < 1 || userName.length > 50) {
                console.log('❌ userName 길이 오류');
                showValidationError('userNameError', '사용자명은 1-50자입니다.');
                isValid = false;
            } else {
                console.log('✓ userName 검증 통과');
            }

            // 이메일 검증
            if (!email) {
                console.log('❌ email 미입력');
                showValidationError('emailError', '이메일을 입력하세요.');
                isValid = false;
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                console.log('❌ email 형식 오류:', email);
                showValidationError('emailError', '유효한 이메일 형식이 아닙니다.');
                isValid = false;
            } else {
                console.log('✓ email 검증 통과');
            }

            // 전화번호 검증
            if (!phone) {
                console.log('❌ phone 미입력');
                showValidationError('phoneError', '전화번호를 입력하세요.');
                isValid = false;
            } else if (!/^(\d{3}-\d{3,4}-\d{4}|\d{10,11})$/.test(phone)) {
                console.log('❌ phone 형식 오류:', phone);
                showValidationError('phoneError', 
                    '전화번호는 010-1234-5678 형식이거나 숫자만 입력해주세요.');
                isValid = false;
            } else {
                console.log('✓ phone 검증 통과');
            }

            console.log('=== 폼 검증 결과:', isValid, '===');
            return isValid;
        }

        // ========== 유틸리티 함수 ==========
        function showValidationError(elementId, message) {
            const element = document.getElementById(elementId);
            if (element) {
                element.textContent = message;
                element.className = 'validation-message error';
            }
        }

        function clearValidationError(elementId) {
            const element = document.getElementById(elementId);
            if (element) {
                element.textContent = '';
                element.className = 'validation-message';
            }
        }

        function clearAllErrors() {
            const errorFields = [
                'tenantError', 'userIdError', 'passwordError', 
                'passwordConfirmError', 'userNameError', 'emailError', 'phoneError', 'addressError'
            ];

            errorFields.forEach(fieldId => {
                clearValidationError(fieldId);
            });
        }

        function showAlert(message, type = 'info') {
            const alertContainer = document.getElementById('alertContainer');
            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type}`;
            alertDiv.textContent = message;

            alertContainer.innerHTML = '';
            alertContainer.appendChild(alertDiv);

            // 3초 후 자동 제거
            if (type !== 'error') {
                setTimeout(() => {
                    alertDiv.remove();
                }, 3000);
            }
        }
    </script>
</body>
</html>
