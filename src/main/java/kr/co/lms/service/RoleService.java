package kr.co.lms.service;

import kr.co.lms.vo.RoleVO;
import java.util.List;

/**
 * 역할 관리 Service 인터페이스
 */
public interface RoleService {
    
/**
 * 역할 조회 (코드 + tenant_id로)
 */
RoleVO selectRole(RoleVO roleVO);

/**
 * 역할 목록 조회
 */
List<RoleVO> selectRoleList(RoleVO roleVO);

/**
 * 역할 등록
 */
int insertRole(RoleVO roleVO);

/**
 * 역할 수정
 */
int updateRole(RoleVO roleVO);

/**
 * 역할 삭제 (논리적 삭제)
 */
int deleteRole(RoleVO roleVO);
    
    /**
     * 역할 수 조회
     */
    int selectRoleCount(RoleVO roleVO);
}
