package kr.co.lms.service;

import kr.co.lms.vo.PermissionVO;
import java.util.List;

/**
 * 권한 관리 Service 인터페이스
 */
public interface PermissionService {
    
    /**
     * 권한 조회 (코드로)
     */
    PermissionVO selectPermission(String permCd);
    
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
    int deletePermission(String permCd);
    
    /**
     * 권한 수 조회
     */
    int selectPermissionCount(PermissionVO permissionVO);
}
