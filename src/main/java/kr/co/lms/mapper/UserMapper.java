package kr.co.lms.mapper;

import kr.co.lms.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 사용자 Mapper (MyBatis)
 * 
 * 데이터베이스 접근 계층
 * user-mapper.xml과 함께 사용
 */
@Mapper
public interface UserMapper {

    /**
     * 사용자 조회 (ID로)
     */
    UserVO selectUser(@Param("userId") String userId);

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
    int deleteUser(@Param("userId") String userId);

    /**
     * 로그인 사용자 조회
     */
    UserVO selectUserForLogin(@Param("userId") String userId, @Param("tenantId") String tenantId);

    /**
     * 사용자 수 조회
     */
    int selectUserCount(UserVO userVO);

    /**
     * 사용자에게 역할 할당
     * 
     * @param userId 사용자 ID
     * @param roleCd 역할 코드 (ROLE_STUDENT, ROLE_INSTRUCTOR 등)
     * @param tenantId 테넌트 ID
     * @return 할당 결과
     */
    int assignRoleToUser(
        @Param("userId") String userId,
        @Param("roleCd") String roleCd,
        @Param("tenantId") String tenantId
    );

    /**
     * 사용자의 역할 목록 조회
     * 
     * @param userId 사용자 ID
     * @param tenantId 테넌트 ID
     * @return 역할 코드 목록 (예: [ROLE_STUDENT], [ROLE_ADMIN, ROLE_MANAGER])
     */
    List<String> selectUserRoles(
        @Param("userId") String userId,
        @Param("tenantId") String tenantId
    );
}
