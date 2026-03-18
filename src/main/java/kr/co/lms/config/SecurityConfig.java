package kr.co.lms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 설정
 * 
 * 비밀번호 암호화 방식:
 * - BCryptPasswordEncoder: 강력한 해시 기반 암호화
 * - strength = 12: 계산량 조정 (12단계, CPU 부하 고려)
 * - 매번 다른 salt 생성으로 rainbow table 공격 방어
 * 
 * BCrypt의 장점:
 * - 단방향 암호화 (복호화 불가능)
 * - 자동 salt 생성
 * - 시간이 지남에 따라 strength 증가 가능 (호환성 유지)
 * - OWASP 권장 알고리즘
 */
@Configuration
public class SecurityConfig {

    /**
     * 비밀번호 인코더 빈
     * 
     * BCryptPasswordEncoder 사용:
     * - 표준 password hashing algorithm
     * - PBKDF2, Argon2 등과 달리 Spring Security에 내장
     * - 프로덕션 수준의 보안성
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);  // strength = 12 (기본값 10)
    }
}
