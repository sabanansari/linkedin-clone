package com.ansari.linkedin.connections_service.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String raw = request.getHeader("X-User-Id");
        log.info("Incoming X-User-Id header: {}", raw);

        if (raw == null || raw.isBlank()) {
            log.info("Missing X-User-Id header");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid X-User-Id");
            return false;
        }

        String cleaned = raw.trim();
        // strip common wrappers like Optional[...] or surrounding quotes or square brackets
        if (cleaned.startsWith("Optional[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(9, cleaned.length() - 1);
        }
        if (cleaned.startsWith("[") && cleaned.endsWith("]")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }
        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        try {
            long id = Long.parseLong(cleaned);
            AuthContextHolder.setCurrentUserId(id);
            log.info("Set current user id in AuthContextHolder: {}", id);
        } catch (NumberFormatException e) {
            log.info("Invalid X-User-Id header value: '{}' (cleaned='{}')", raw, cleaned);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid X-User-Id");
            return false;
        }

        return true;
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContextHolder.clear();
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
