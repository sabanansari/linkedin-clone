package com.ansari.linkedin.posts_service.auth;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Long userId = AuthContextHolder.getCurrentUserId();
        if(userId !=null) {
            template.header("X-User-Id", String.valueOf(userId));
        }
    }
}
