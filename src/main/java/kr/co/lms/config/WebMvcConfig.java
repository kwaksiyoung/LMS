package kr.co.lms.config;

import kr.co.lms.web.interceptor.MenuAccessInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Spring MVC 설정
 * LocalDateTime 포매터 등록 및 인터셉터 설정
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    
    @Autowired(required = false)
    private MenuAccessInterceptor menuAccessInterceptor;
    
    /**
     * LocalDateTime 포매터 등록
     * JSP에서 EL expression으로 LocalDateTime을 출력할 때 자동으로 포맷팅됨
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new Formatter<LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            @Override
            public String print(LocalDateTime localDateTime, Locale locale) {
                if (localDateTime == null) {
                    return "";
                }
                return localDateTime.format(formatter);
            }
            
            @Override
            public LocalDateTime parse(String text, Locale locale) {
                if (text == null || text.isEmpty()) {
                    return null;
                }
                return LocalDateTime.parse(text, formatter);
            }
        });
        
        logger.info("LocalDateTime Formatter 등록 완료");
    }
    
    /**
     * 인터셉터 등록
     * MenuAccessInterceptor를 모든 요청 경로에 적용합니다.
     * 단, 제외 경로(로그인, 정적 리소스 등)는 인터셉터 내부에서 처리됩니다.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (menuAccessInterceptor != null) {
            logger.info("MenuAccessInterceptor 등록");
            
            registry.addInterceptor(menuAccessInterceptor)
                    .addPathPatterns("/**")  // 모든 경로에 인터셉터 적용
                    .excludePathPatterns(
                            "/login",
                            "/logout",
                            "/signup",
                            "/forgot-password",
                            "/api/auth/**",
                            "/css/**",
                            "/js/**",
                            "/images/**",
                            "/static/**",
                            "/admin/**"
                    );
        } else {
            logger.warn("MenuAccessInterceptor를 찾을 수 없습니다.");
        }
    }
}
