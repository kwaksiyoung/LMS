package kr.co.lms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * Jackson 설정
 * Java 8 Date/Time (LocalDateTime, LocalDate) 직렬화를 위해 JSR310 모듈 등록
 */
@Configuration
public class JacksonConfig {

  /**
   * ObjectMapper 빈 등록
   * JSR310 모듈 (JavaTimeModule) 자동 등록
   */
  @Bean
  public ObjectMapper objectMapper() {
    return Jackson2ObjectMapperBuilder.json()
        .modules(new JavaTimeModule())
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();
  }
}
