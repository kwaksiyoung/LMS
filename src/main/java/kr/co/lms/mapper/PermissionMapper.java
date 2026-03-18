package kr.co.lms.mapper;

import kr.co.lms.vo.PermissionVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 권한 관리 Mapper (DAO)
 * MyBatis @Mapper 어노테이션 사용
 */
@Mapper
public interface PermissionMapper {
    
    /**
     * 권한 조회 (코드로)
     */
    PermissionVO selectPermission(@Param("permCd") String permCd);
    
    /**
     * 권한 목록 조회
     */
    List<PermissionVO> selectPermissionList(PermissionVO permissionVO);
    
    /**
     * 권한 등록
     */
    int insertPermission(PermissionVO permissionVO);
    
    /**
     * 권한 수정
     */
    int updatePermission(PermissionVO permissionVO);
    
    /**
     * 권한 삭제
     */
    int deletePermission(@Param("permCd") String permCd);
    
    /**
     * 권한 수 조회
     */
    int selectPermissionCount(PermissionVO permissionVO);
}
