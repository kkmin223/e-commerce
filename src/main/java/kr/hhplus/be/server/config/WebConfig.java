package kr.hhplus.be.server.config;

import kr.hhplus.be.server.logging.interceptor.ExecutionTimeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {


    private final ExecutionTimeInterceptor executionTimeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(executionTimeInterceptor)
            .addPathPatterns("/**"); // 모든 요청에 적용
    }
}
