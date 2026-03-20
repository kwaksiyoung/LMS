package kr.co.lms.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring 캐싱 설정
 * 
 * 메모리 기반 로컬 캐싱 사용 (@Cacheable, @CacheEvict)
 * - 프로덕션에서는 Redis로 변경 권장
 * - 단일 서버 또는 세션 아이티니티 필요한 경우 로컬 캐싱 충분
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 캐시 매니저 설정
     * 
     * 사용하는 캐시:
     * - "menuByRole": 역할별 메뉴 목록 (1시간)
     * 
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "menuByRole"  // 역할별 메뉴 목록 캐시
        );
    }
}
