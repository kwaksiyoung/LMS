package kr.co.lms.web.api.v1;

import kr.co.lms.web.api.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 상태 확인 컨트롤러
 * 
 * 헬스 체크 및 기본 정보 제공
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthApiController {

    /**
     * 헬스 체크 - 기본 상태 확인
     * 
     * @return API 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "UP");
        data.put("timestamp", System.currentTimeMillis());
        data.put("application", "LMS (eGovFrame 4.2.0)");
        data.put("version", "1.0.0");
        
        return ResponseEntity.ok(ApiResponse.success(data, "API 서버가 정상 운영 중입니다"));
    }

    /**
     * 상세 상태 확인
     * 
     * @return 상세 상태 정보
     */
    @GetMapping("/detailed")
    public ResponseEntity<ApiResponse<Map<String, Object>>> detailedHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        
        // 시스템 정보
        Map<String, Object> system = new LinkedHashMap<>();
        system.put("osName", System.getProperty("os.name"));
        system.put("osVersion", System.getProperty("os.version"));
        system.put("javaVersion", System.getProperty("java.version"));
        system.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        system.put("totalMemory", Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        system.put("freeMemory", Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        
        health.put("system", system);
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(ApiResponse.success(health));
    }
}
