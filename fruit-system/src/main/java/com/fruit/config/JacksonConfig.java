package com.fruit.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    // 日期时间格式化模式
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        // 注册LocalDateTime序列化器
        javaTimeModule.addSerializer(
                java.time.LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
        );
        
        // 配置ObjectMapper
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        
        // 配置BigDecimal序列化，保留两位小数，禁用科学计数法
        objectMapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
        
        // 注册BigDecimal序列化器，确保正确的数字格式
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                // 保留两位小数，四舍五入，null值返回0
                BigDecimal writeValue = value != null ? value : BigDecimal.ZERO;
                gen.writeNumber(writeValue.setScale(2, RoundingMode.HALF_UP));
            }
        });
        objectMapper.registerModule(simpleModule);
        
        return objectMapper;
    }
}
