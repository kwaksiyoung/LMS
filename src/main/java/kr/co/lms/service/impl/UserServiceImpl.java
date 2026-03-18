package kr.co.lms.service.impl;

import kr.co.lms.mapper.UserMapper;
import kr.co.lms.service.UserService;
import kr.co.lms.service.EncryptionService;
import kr.co.lms.vo.UserVO;
import kr.co.lms.vo.RegisterRequestVO;
import kr.co.lms.vo.RegisterResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 관리 Service 구현
 * 
 * 암호화 통합:
 * - 회원정보 저장(insert) 시: email, phone, address 암호화
 * - 회원정보 조회(select) 시: 암호화된 데이터 복호화
 * - 회원정보 수정(update) 시: 변경된 필드만 암호화
 * 
 * 테넌트 관리:
 * - 모든 쿼리에 tenantId 포함 필수 (멀티테넌시)
 * - 각 테넌트별 독립 암호화 키 사용
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final EncryptionService encryptionService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 조회 (ID로)
     * 
     * 조회 후 암호화된 필드(email, phone, address) 복호화
     */
    @Override
    @Transactional(readOnly = true)
    public UserVO selectUser(String userId) {
        logger.debug("사용자 조회: userId={}", userId);
        
        UserVO user = userMapper.selectUser(userId);
        
        if (user != null) {
            // 암호화된 개인정보 복호화
            try {
                String tenantId = user.getTenantId();
                
                if (tenantId != null && !tenantId.isEmpty()) {
                    // email, phone, address 복호화
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        user.setEmail(encryptionService.decryptEmail(tenantId, user.getEmail()));
                    }
                    if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                        user.setPhone(encryptionService.decryptPhone(tenantId, user.getPhone()));
                    }
                    if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                        user.setAddress(encryptionService.decryptAddress(tenantId, user.getAddress()));
                    }
                    
                    logger.debug("사용자 정보 복호화 완료: userId={}", userId);
                } else {
                    logger.warn("사용자의 tenantId가 없음: userId={}", userId);
                }
            } catch (Exception e) {
                logger.error("사용자 정보 복호화 실패: userId={}, error={}", userId, e.getMessage());
                throw new RuntimeException("사용자 정보 복호화에 실패했습니다.", e);
            }
        }
        
        return user;
    }

    /**
     * 사용자 목록 조회
     * 
     * 조회 후 각 사용자의 암호화된 필드(email, phone, address) 복호화
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserVO> selectUserList(UserVO userVO) {
        logger.debug("사용자 목록 조회: {}", userVO);
        
        List<UserVO> userList = userMapper.selectUserList(userVO);
        
        try {
            // 각 사용자의 개인정보 복호화
            for (UserVO user : userList) {
                if (user != null) {
                    String tenantId = user.getTenantId();
                    
                    if (tenantId != null && !tenantId.isEmpty()) {
                        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                            user.setEmail(encryptionService.decryptEmail(tenantId, user.getEmail()));
                        }
                        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                            user.setPhone(encryptionService.decryptPhone(tenantId, user.getPhone()));
                        }
                        if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                            user.setAddress(encryptionService.decryptAddress(tenantId, user.getAddress()));
                        }
                    } else {
                        logger.warn("사용자의 tenantId가 없음: userId={}", user.getUserId());
                    }
                }
            }
            logger.debug("사용자 목록 복호화 완료: 총 {} 명", userList.size());
        } catch (Exception e) {
            logger.error("사용자 목록 복호화 실패: error={}", e.getMessage());
            throw new RuntimeException("사용자 목록 복호화에 실패했습니다.", e);
        }
        
        return userList;
    }

    /**
     * 사용자 등록
     * 
     * 저장 전 개인정보(email, phone, address) 암호화
     * 암호화 후 암호화된 값을 userVO에 설정하고 DB에 저장
     */
    @Override
    public int insertUser(UserVO userVO) {
        logger.info("사용자 등록: userId={}", userVO.getUserId());
        
        try {
            String tenantId = userVO.getTenantId();
            
            if (tenantId != null && !tenantId.isEmpty()) {
                // email, phone, address 암호화
                if (userVO.getEmail() != null && !userVO.getEmail().isEmpty()) {
                    String encryptedEmail = encryptionService.encryptEmail(tenantId, userVO.getEmail());
                    userVO.setEmail(encryptedEmail);
                }
                if (userVO.getPhone() != null && !userVO.getPhone().isEmpty()) {
                    String encryptedPhone = encryptionService.encryptPhone(tenantId, userVO.getPhone());
                    userVO.setPhone(encryptedPhone);
                }
                if (userVO.getAddress() != null && !userVO.getAddress().isEmpty()) {
                    String encryptedAddress = encryptionService.encryptAddress(tenantId, userVO.getAddress());
                    userVO.setAddress(encryptedAddress);
                }
                
                logger.debug("사용자 정보 암호화 완료: userId={}", userVO.getUserId());
            } else {
                logger.warn("사용자의 tenantId가 없음: userId={}", userVO.getUserId());
                throw new RuntimeException("사용자의 tenantId가 필수입니다.");
            }
            
            // 암호화된 데이터를 DB에 저장
            int result = userMapper.insertUser(userVO);
            
            if (result > 0) {
                logger.info("사용자 등록 성공: userId={}", userVO.getUserId());
            } else {
                logger.warn("사용자 등록 실패: userId={}", userVO.getUserId());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("사용자 등록 중 오류 발생: userId={}, error={}", userVO.getUserId(), e.getMessage());
            throw new RuntimeException("사용자 등록에 실패했습니다.", e);
        }
    }

    /**
     * 사용자 수정
     * 
     * 수정 전 변경된 개인정보(email, phone, address) 암호화
     * 암호화 후 암호화된 값을 userVO에 설정하고 DB 업데이트
     */
    @Override
    public int updateUser(UserVO userVO) {
        logger.info("사용자 수정: userId={}", userVO.getUserId());
        
        try {
            String tenantId = userVO.getTenantId();
            
            if (tenantId != null && !tenantId.isEmpty()) {
                // 변경된 email, phone, address만 암호화
                if (userVO.getEmail() != null && !userVO.getEmail().isEmpty()) {
                    String encryptedEmail = encryptionService.encryptEmail(tenantId, userVO.getEmail());
                    userVO.setEmail(encryptedEmail);
                }
                if (userVO.getPhone() != null && !userVO.getPhone().isEmpty()) {
                    String encryptedPhone = encryptionService.encryptPhone(tenantId, userVO.getPhone());
                    userVO.setPhone(encryptedPhone);
                }
                if (userVO.getAddress() != null && !userVO.getAddress().isEmpty()) {
                    String encryptedAddress = encryptionService.encryptAddress(tenantId, userVO.getAddress());
                    userVO.setAddress(encryptedAddress);
                }
                
                logger.debug("사용자 정보 암호화 완료: userId={}", userVO.getUserId());
            } else {
                logger.warn("사용자의 tenantId가 없음: userId={}", userVO.getUserId());
                throw new RuntimeException("사용자의 tenantId가 필수입니다.");
            }
            
            // 암호화된 데이터로 DB 업데이트
            int result = userMapper.updateUser(userVO);
            
            if (result > 0) {
                logger.info("사용자 수정 성공: userId={}", userVO.getUserId());
            } else {
                logger.warn("사용자 수정 실패: userId={}", userVO.getUserId());
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("사용자 수정 중 오류 발생: userId={}, error={}", userVO.getUserId(), e.getMessage());
            throw new RuntimeException("사용자 수정에 실패했습니다.", e);
        }
    }

    /**
     * 사용자 삭제
     */
    @Override
    public int deleteUser(String userId) {
        logger.info("사용자 삭제: userId={}", userId);
        int result = userMapper.deleteUser(userId);
        if (result > 0) {
            logger.info("사용자 삭제 성공: userId={}", userId);
        } else {
            logger.warn("사용자 삭제 실패: userId={}", userId);
        }
        return result;
    }

    /**
     * 로그인 사용자 조회
     *
     * 조회 후 암호화된 필드(email, phone, address) 복호화
     */
    @Override
    @Transactional(readOnly = true)
    public UserVO selectUserForLogin(String userId, String tenantId) {
        logger.debug("로그인 사용자 조회: userId={}, tenantId={}", userId, tenantId);

        UserVO user = userMapper.selectUserForLogin(userId, tenantId);
        
        if (user != null) {
            try {
                tenantId = user.getTenantId();
                
                if (tenantId != null && !tenantId.isEmpty()) {
                    // email, phone 복호화 (주로 email 사용)
                    if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                        user.setEmail(encryptionService.decryptEmail(tenantId, user.getEmail()));
                    }
                    if (user.getPhone() != null && !user.getPhone().isEmpty()) {
                        user.setPhone(encryptionService.decryptPhone(tenantId, user.getPhone()));
                    }
                    if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                        user.setAddress(encryptionService.decryptAddress(tenantId, user.getAddress()));
                    }
                    
                    logger.debug("로그인 사용자 정보 복호화 완료: userId={}", userId);
                } else {
                    logger.warn("로그인 사용자의 tenantId가 없음: userId={}", userId);
                }
            } catch (Exception e) {
                logger.error("로그인 사용자 정보 복호화 실패: userId={}, error={}", userId, e.getMessage());
                throw new RuntimeException("사용자 정보 복호화에 실패했습니다.", e);
            }
        }
        
        return user;
    }

    /**
     * 사용자 존재 여부 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(String userId) {
        logger.debug("사용자 존재 확인: userId={}", userId);
        UserVO user = userMapper.selectUser(userId);
        return user != null;
    }

    /**
     * 사용자 수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int selectUserCount(UserVO userVO) {
        logger.debug("사용자 수 조회");
        return userMapper.selectUserCount(userVO);
    }

    /**
     * 회원가입 처리
     * 
     * 프로세스:
     * 1. 입력값 검증 (비밀번호 일치 확인)
     * 2. ID 중복 확인
     * 3. 비밀번호 BCrypt 암호화
     * 4. UserVO 생성 및 tenantId 설정
     * 5. DB 저장 (email, phone, address는 AES-256-GCM으로 암호화)
     * 6. 로그 기록
     */
    @Override
    public RegisterResponseVO registerUser(RegisterRequestVO registerRequest) {
        logger.info("회원가입 요청: userId={}, tenantId={}", 
                   registerRequest.getUserId(), registerRequest.getTenantId());
        
        try {
            // 1. 비밀번호 일치 확인
            if (!registerRequest.getPassword().equals(registerRequest.getPasswordConfirm())) {
                logger.warn("회원가입 실패: 비밀번호 불일치 - userId={}", registerRequest.getUserId());
                return new RegisterResponseVO(false, "비밀번호가 일치하지 않습니다.");
            }

            // 2. ID 중복 확인
            if (isUserIdDuplicate(registerRequest.getUserId(), registerRequest.getTenantId())) {
                logger.warn("회원가입 실패: ID 중복 - userId={}, tenantId={}", 
                           registerRequest.getUserId(), registerRequest.getTenantId());
                return new RegisterResponseVO(false, "이미 사용 중인 아이디입니다.");
            }

            // 3. UserVO 생성 및 데이터 설정
            UserVO userVO = new UserVO();
            userVO.setUserId(registerRequest.getUserId());
            userVO.setTenantId(registerRequest.getTenantId());
            userVO.setUserName(registerRequest.getUserName());
            userVO.setEmail(registerRequest.getEmail());
            userVO.setPhone(registerRequest.getPhone());
            userVO.setAddress(registerRequest.getAddress());
            userVO.setUseYn("Y");
            userVO.setRegDt(LocalDateTime.now());
            userVO.setUpdDt(LocalDateTime.now());

            // 4. 비밀번호 BCrypt 암호화
            String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());
            userVO.setPassword(encryptedPassword);

            logger.debug("비밀번호 BCrypt 암호화 완료 - userId={}", registerRequest.getUserId());

            // 5. DB 저장 (email, phone, address는 insertUser 내부에서 AES-256-GCM 암호화됨)
            int result = insertUser(userVO);

            if (result > 0) {
                // 6. 회원가입 사용자에게 기본 역할(ROLE_STUDENT) 자동 할당
                try {
                    int roleResult = userMapper.assignRoleToUser(
                        registerRequest.getUserId(),
                        "ROLE_STUDENT",
                        registerRequest.getTenantId()
                    );
                    
                    if (roleResult > 0) {
                        logger.info("기본 역할 할당 성공: userId={}, role=ROLE_STUDENT, tenantId={}", 
                                   registerRequest.getUserId(), registerRequest.getTenantId());
                    } else {
                        logger.warn("기본 역할 할당 실패: userId={}, role=ROLE_STUDENT", 
                                   registerRequest.getUserId());
                    }
                } catch (Exception e) {
                    logger.error("기본 역할 할당 중 오류: userId={}, error={}", 
                               registerRequest.getUserId(), e.getMessage());
                    // 역할 할당 실패해도 회원가입은 성공한 것으로 간주 (나중에 수동 할당 가능)
                }
                
                logger.info("회원가입 완료: userId={}, tenantId={}", 
                           registerRequest.getUserId(), registerRequest.getTenantId());
                return new RegisterResponseVO(true, "회원가입이 완료되었습니다.",
                        registerRequest.getUserId(),
                        registerRequest.getEmail(),
                        registerRequest.getUserName(),
                        registerRequest.getTenantId());
            } else {
                logger.error("회원가입 실패: DB 저장 실패 - userId={}", registerRequest.getUserId());
                return new RegisterResponseVO(false, "회원가입 중 오류가 발생했습니다.");
            }

        } catch (Exception e) {
            logger.error("회원가입 중 예외 발생: userId={}, error={}", 
                        registerRequest.getUserId(), e.getMessage(), e);
            return new RegisterResponseVO(false, "회원가입 중 오류가 발생했습니다.");
        }
    }

    /**
     * 사용자 ID 중복 확인
     * 
     * 같은 테넌트 내에서 해당 ID의 사용자가 존재하는지 확인
     * 
     * @param userId 확인할 사용자 ID
     * @param tenantId 테넌트 ID
     * @return true: 이미 존재 (중복), false: 사용 가능
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUserIdDuplicate(String userId, String tenantId) {
        logger.debug("ID 중복 확인: userId={}, tenantId={}", userId, tenantId);
        
        try {
            UserVO userVO = new UserVO();
            userVO.setUserId(userId);
            userVO.setTenantId(tenantId);
            
            UserVO existingUser = userMapper.selectUser(userId);
            
            // 같은 테넌트 내에서 존재하는지 확인
            if (existingUser != null && tenantId.equals(existingUser.getTenantId())) {
                logger.debug("ID 중복 확인 결과: 이미 존재 - userId={}, tenantId={}", userId, tenantId);
                return true;  // 중복
            }
            
            logger.debug("ID 중복 확인 결과: 사용 가능 - userId={}, tenantId={}", userId, tenantId);
            return false;  // 사용 가능
        } catch (Exception e) {
            logger.error("ID 중복 확인 중 오류: userId={}, tenantId={}, error={}", 
                        userId, tenantId, e.getMessage());
            throw new RuntimeException("ID 중복 확인에 실패했습니다.", e);
        }
    }

    /**
     * 로그인 인증 (비밀번호 검증)
     * 
     * 프로세스:
     * 1. userId로 사용자 조회 (비밀번호 포함)
     * 2. 사용자 존재 여부 확인
     * 3. BCrypt로 비밀번호 비교
     * 4. 성공 시 사용자 정보 반환, 실패 시 null 반환
     */
    @Override
    @Transactional(readOnly = true)
    public UserVO authenticateUser(String userId, String password, String tenantId) {
        logger.info("로그인 인증 시도: userId={}, tenantId={}", userId, tenantId);

        try {
            // 1. userId와 tenantId로 사용자 조회 (비밀번호 포함)
            UserVO user = selectUserForLogin(userId, tenantId);
            
            // 2. 사용자 존재 여부 확인
            if (user == null) {
                logger.warn("로그인 실패: 사용자 없음 - userId={}", userId);
                return null;
            }
            
            // 3. 사용 가능 여부 확인 (use_yn = 'Y')
            if (!"Y".equals(user.getUseYn())) {
                logger.warn("로그인 실패: 계정 비활성화 - userId={}", userId);
                return null;
            }
            
            // 4. BCrypt로 비밀번호 비교 (입력 비밀번호 vs DB 저장된 암호화 비밀번호)
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("로그인 실패: 비밀번호 불일치 - userId={}", userId);
                return null;
            }
            
            logger.info("로그인 성공: userId={}, tenantId={}", userId, user.getTenantId());
            return user;
            
        } catch (Exception e) {
            logger.error("로그인 인증 중 오류: userId={}, error={}", userId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 사용자의 역할 목록 조회
     * 
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @return 역할 코드 목록
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<String> selectUserRoles(String userId, String tenantId) {
        logger.debug("사용자 역할 조회: userId={}, tenantId={}", userId, tenantId);
        
        try {
            java.util.List<String> roles = userMapper.selectUserRoles(userId, tenantId);
            
            if (roles == null) {
                roles = new java.util.ArrayList<>();
            }
            
            logger.debug("사용자 역할 조회 완료: userId={}, roles={}", userId, roles);
            return roles;
            
        } catch (Exception e) {
            logger.error("사용자 역할 조회 중 오류: userId={}, tenantId={}, error={}", 
                        userId, tenantId, e.getMessage());
            return new java.util.ArrayList<>();
        }
    }
}
