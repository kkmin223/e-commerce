package kr.hhplus.be.server.logging.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 요청에 대한 로깅 (http method, url, ip)
        logger.info("[Filter] Request: {} {} from {}", req.getMethod(), req.getRequestURI(), req.getRemoteAddr());

        try {
            chain.doFilter(request, response);
        } finally {
            // 응답에 대한 로깅 (http method, url, statusCode)
            logger.info("[Filter] Response: {} {} -> status {}", req.getMethod(), req.getRequestURI(), res.getStatus());
            MDC.clear(); // 꼭 clear 해주세요!
        }
    }
}
