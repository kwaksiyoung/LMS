package kr.co.lms.mapper;

import kr.co.lms.vo.RoleVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 역할 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 */
@Mapper
public interface RoleMapper {
    
    /**
     * 역할 조회 (코드로)
     */
    RoleVO selectRole(@Param("roleCd") String roleCd);
    
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
     * 역할 삭제
     */
    int deleteRole(@Param("roleCd") String roleCd);
    
    /**
     * 역할 수 조회
     */
    int selectRoleCount(RoleVO roleVO);
}
