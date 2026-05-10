//package com.ansari.linkedin.posts_service.config;
//
//import feign.RequestInterceptor; // ⚠️ THIS is feign, not spring
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import com.ansari.linkedin.posts_service.auth.AuthContextHolder;
//
//@Configuration
//public class FeignConfig {
//
//    @Bean
//    public RequestInterceptor feignRequestInterceptor() {
//        return template -> {
//
//            Long userId = AuthContextHolder.getCurrentUserId();
//
//            if (userId != null) {
//                template.header("X-User-Id", userId.toString());
//            }
//        };
//    }
//}