package kr.co.lms.service;

import kr.co.lms.vo.UserVO;
import kr.co.lms.vo.RegisterRequestVO;
import kr.co.lms.vo.RegisterResponseVO;
import java.util.List;

/**
 * 사용자 관리 Service 인터페이스
 */
public interface UserService {

    /**
     * 사용자 조회 (ID로)
     */
    UserVO selectUser(String userId);

    /**
     * 사용자 목록 조회
     */
    List<UserVO> selectUserList(UserVO userVO);

    /**
     * 사용자 등록
     */
    int insertUser(UserVO userVO);

    /**
     * 사용자 수정
     */
    int updateUser(UserVO userVO);

    /**
     * 사용자 삭제
     */
    int deleteUser(String userId);

    /**
     * 로그인 사용자 조회
     *
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @return 사용자 정보 (비밀번호 포함)
     */
    UserVO selectUserForLogin(String userId, String tenantId);

    /**
     * 사용자 존재 여부 확인
     */
    boolean isUserExists(String userId);

    /**
     * 사용자 수 조회
     */
    int selectUserCount(UserVO userVO);

    /**
     * 회원가입 처리
     * 
     * @param registerRequest 회원가입 요청 정보
     * @return 회원가입 응답 (성공/실패)
     */
    RegisterResponseVO registerUser(RegisterRequestVO registerRequest);

    /**
     * 사용자 ID 중복 확인
     * 
     * @param userId 확인할 사용자 ID
     * @param tenantId 테넌트 ID
     * @return true: 이미 존재, false: 사용 가능
     */
    boolean isUserIdDuplicate(String userId, String tenantId);

    /**
     * 로그인 인증 (비밀번호 검증)
     *
     * @param userId 사용자 ID
     * @param password 입력한 비밀번호
     * @param tenantId 테넌트 ID
     * @return 인증 성공 시 사용자 정보, 실패 시 null
     */
    UserVO authenticateUser(String userId, String password, String tenantId);

    /**
     * 사용자의 역할 목록 조회
     * 
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @return 역할 코드 목록
     */
    java.util.List<String> selectUserRoles(String userId, String tenantId);
}
