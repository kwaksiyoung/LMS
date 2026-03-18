package kr.co.lms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Spring MVC 설정
 * LocalDateTime을 자동으로 포맷팅하기 위한 Formatter 등록
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
    
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
}
