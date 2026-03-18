package kr.co.lms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;

/**
 * Redis 세션 클러스터링 설정
 * WAS 다중화 환경에서 세션 공유를 통한 무중단 배포 지원
 */
@Configuration
@PropertySource("classpath:application.properties")
@EnableRedisHttpSession(
    maxInactiveIntervalInSeconds = 1800, // 30분
    redisNamespace = "lms:session"
)
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Value("${redis.host:localhost}")
    private String redisHost;

    @Value("${redis.port:6379}")
    private int redisPort;

    @Value("${redis.password:}")
    private String redisPassword;

    @Value("${redis.database:0}")
    private int redisDatabase;
    
    /**
     * RedisConfig 초기화 후 설정값 로깅
     */
    @PostConstruct
    public void init() {
        String logMessage = String.format(
            "\n" +
            "=== Redis Configuration ===\n" +
            "Host: %s\n" +
            "Port: %d\n" +
            "Database: %d\n" +
            "Password: %s\n" +
            "Log Path: %s\n" +
            "===========================\n",
            redisHost,
            redisPort,
            redisDatabase,
            (redisPassword != null && !redisPassword.isEmpty()) ? "***설정됨***" : "미설정",
            System.getProperty("user.dir") + "/logs"
        );
        
        // 콘솔과 로그 파일 모두에 출력
        System.out.println(logMessage);
        logger.info(logMessage);
    }

    /**
     * Redis 연결 팩토리 (Lettuce 사용)
     * 
     * Lettuce는:
     * - Non-blocking, async-driven 라이브러리
     * - 스레드-세이프
     * - 높은 성능
     * - 연결 풀링 지원
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);
        
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }

        return new LettuceConnectionFactory(config);
    }

    /**
     * Redis Template 설정
     * 
     * RedisTemplate은 Redis 명령을 실행하는 중심 클래스
     * - Key/Value Serializer: 기본값은 JDK 직렬화 (비효율적)
     * - StringRedisSerializer: 문자열 키 (가독성 좋음)
     * - GenericJackson2JsonRedisSerializer: JSON 직렬화 (객체 저장 가능)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key Serializer (String)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value Serializer (JSON)
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis 연결 테스트
     * 
     * 애플리케이션 시작 시 Redis 연결 상태 확인
     */
    @Bean
    public RedisConnectionTest redisConnectionTest(RedisConnectionFactory connectionFactory) {
        return new RedisConnectionTest(connectionFactory);
    }

    /**
     * Redis 연결 테스트 클래스
     */
    public static class RedisConnectionTest {
        public RedisConnectionTest(RedisConnectionFactory connectionFactory) {
            try {
                connectionFactory.getConnection().ping();
                System.out.println("[Redis] ✓ Redis 연결 성공");
            } catch (Exception e) {
                System.err.println("[Redis] ✗ Redis 연결 실패: " + e.getMessage());
                System.err.println("[Redis] Redis 서버가 실행 중인지 확인하세요.");
                System.err.println("[Redis] > redis-server");
            }
        }
    }
}
