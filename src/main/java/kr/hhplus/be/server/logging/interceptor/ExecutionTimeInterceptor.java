package kr.hhplus.be.server.logging.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ExecutionTimeInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 시작 시간 기록
        request.setAttribute("startTime", System.currentTimeMillis());
        return true; // 컨트롤러로 진입 허용
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        // 종료시간과 시작시간 차이 비교
        long duration = System.currentTimeMillis() - startTime;
        // url 요청 시간 기록
        log.info("[Interceptor] {} {} executed in {} ms", request.getMethod(), request.getRequestURI(), duration);
    }
}
