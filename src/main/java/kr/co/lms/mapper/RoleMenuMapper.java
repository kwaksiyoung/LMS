package kr.co.lms.mapper;

import kr.co.lms.vo.MenuVO;
import kr.co.lms.vo.RoleMenuVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 역할-메뉴 매핑 Mapper
 * 
 * 역할(tb_role)과 메뉴(tb_menu)의 N:M 관계를 관리하는 데이터 접근 계층
 * tb_role_menu 테이블을 통해 메뉴별 접근 권한을 제어
 */
@Mapper
public interface RoleMenuMapper {

    /**
     * 특정 메뉴에 접근할 수 있는 역할 목록 조회
     * 
     * @param roleMenuVO 메뉴 ID, 고객사 ID 포함
     * @return 역할 코드 리스트
     */
    List<String> selectRolesByMenu(RoleMenuVO roleMenuVO);

    /**
     * 특정 역할이 접근할 수 있는 메뉴 목록 조회
     * 사용 가능한 메뉴만 반환 (use_yn = 'Y')
     * 
     * @param roleMenuVO 역할 코드, 고객사 ID 포함
     * @return 메뉴 리스트 (메뉴명, 메뉴URL 포함)
     */
    List<MenuVO> selectMenusByRole(RoleMenuVO roleMenuVO);

    /**
     * 특정 URL에 접근할 수 있는 역할 목록 조회
     * 메뉴 URL로 역할 목록을 조회하여 접근 권한 검증
     * 
     * @param roleMenuVO 메뉴 URL, 고객사 ID 포함
     * @return 역할 코드 리스트
     */
    List<String> selectRolesByUrl(RoleMenuVO roleMenuVO);

    /**
     * 역할-메뉴 매핑 추가
     * 
     * @param roleMenuVO 역할-메뉴 VO
     * @return 영향받은 행 수
     */
    int insertRoleMenu(RoleMenuVO roleMenuVO);

    /**
     * 다중 역할-메뉴 매핑 추가 (배치)
     * 메뉴 등록 시 여러 역할에 한 번에 매핑
     * 
     * @param roleMenuList 역할-메뉴 리스트
     * @return 영향받은 행 수
     */
    int insertRoleMenuBatch(List<RoleMenuVO> roleMenuList);

    /**
     * 특정 역할의 모든 메뉴 매핑 삭제
     * 역할 삭제 시 관련된 모든 메뉴 매핑을 제거
     * 
     * @param roleMenuVO 역할 코드, 고객사 ID 포함
     * @return 영향받은 행 수
     */
    int deleteRoleMenusByRole(RoleMenuVO roleMenuVO);

    /**
     * 특정 메뉴의 모든 역할 매핑 삭제
     * 메뉴 삭제 시 관련된 모든 역할 매핑을 제거
     * 
     * @param roleMenuVO 메뉴 ID, 고객사 ID 포함
     * @return 영향받은 행 수
     */
    int deleteRoleMenusByMenu(RoleMenuVO roleMenuVO);

    /**
     * 특정 역할-메뉴 매핑 삭제
     * 
     * @param roleMenuVO 역할 코드, 메뉴 ID, 고객사 ID 포함
     * @return 영향받은 행 수
     */
    int deleteRoleMenu(RoleMenuVO roleMenuVO);

    /**
     * 특정 역할-메뉴 매핑 존재 여부 확인
     * 
     * @param roleMenuVO 역할 코드, 메뉴 ID, 고객사 ID 포함
     * @return 매핑 존재 여부 (0: 없음, 1: 있음)
     */
    int countRoleMenu(RoleMenuVO roleMenuVO);

    /**
     * 특정 역할의 메뉴 매핑 개수 조회
     * 
     * @param roleMenuVO 역할 코드, 고객사 ID 포함
     * @return 메뉴 개수
     */
    int countMenusByRole(RoleMenuVO roleMenuVO);
}
