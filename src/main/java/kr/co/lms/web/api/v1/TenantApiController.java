package kr.co.lms.web.api.v1;

import kr.co.lms.service.TenantService;
import kr.co.lms.vo.TenantVO;
import kr.co.lms.web.api.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 테넌트(고객사) 관련 REST API 컨트롤러
 * 
 * 엔드포인트:
 * - GET /api/v1/tenants - 활성 테넌트 목록 조회 (데이터베이스)
 * 
 * 회원가입 시 사용자가 소속할 테넌트를 선택할 때 사용
 */
@RestController
@RequestMapping("/api/v1/tenants")
@RequiredArgsConstructor
public class TenantApiController {

    private static final Logger logger = LoggerFactory.getLogger(TenantApiController.class);

    private final TenantService tenantService;

    /**
     * 활성 테넌트 목록 조회 (데이터베이스)
     * 
     * 회원가입 페이지에서 사용자가 선택할 수 있는 테넌트 목록을 반환
     * subscription_status = 'ACTIVE'인 테넌트만 반환
     * 
     * 응답 예시:
     * {
     *   "success": true,
     *   "message": "테넌트 목록 조회 성공",
     *   "data": [
     *     {
     *       "tenantId": "TENANT001",
     *       "tenantName": "한국 교육 센터",
     *       "tenantDesc": "한국의 주요 교육 기관"
     *     },
     *     {
     *       "tenantId": "TENANT002",
     *       "tenantName": "글로벌 러닝 센터",
     *       "tenantDesc": "국제 온라인 학습 플랫폼"
     *     }
     *   ]
     * }
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getTenants() {
        logger.info("활성 테넌트 목록 조회 API 요청");

        try {
            // 데이터베이스에서 활성 테넌트 목록 조회
            List<TenantVO> tenantList = tenantService.selectActiveTenants();

            // TenantVO를 Map으로 변환
            List<Map<String, String>> tenants = new ArrayList<>();
            for (TenantVO tenant : tenantList) {
                Map<String, String> tenantMap = new HashMap<>();
                tenantMap.put("tenantId", tenant.getTenantId());
                tenantMap.put("tenantName", tenant.getTenantNm());
                tenantMap.put("tenantDesc", tenant.getTenantDesc() != null ? tenant.getTenantDesc() : "");
                tenants.add(tenantMap);
            }

            logger.info("활성 테넌트 목록 조회 성공: {} 개", tenants.size());

            return ResponseEntity.ok(
                    new ApiResponse<>(true, tenants, "테넌트 목록 조회 성공"));

        } catch (Exception e) {
            logger.error("테넌트 목록 조회 중 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, null, "테넌트 목록 조회 중 오류가 발생했습니다."));
        }
    }
}
