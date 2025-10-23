package com.planttracker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

     private static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

     @Bean
     @Primary
     public ObjectMapper objectMapper() {
          ObjectMapper objectMapper = new ObjectMapper();
          JavaTimeModule javaTimeModule = new JavaTimeModule();

          // Custom serializer/deserializer for LocalDateTime
          javaTimeModule.addSerializer(new LocalDateTimeSerializer(
                    DateTimeFormatter.ofPattern(dateTimeFormat)));

          objectMapper.registerModule(javaTimeModule);
          return objectMapper;
     }
}