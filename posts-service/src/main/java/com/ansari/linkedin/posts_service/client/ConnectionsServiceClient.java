package com.ansari.linkedin.posts_service.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "connections-service", path="/connections")
public interface ConnectionsServiceClient {
}
