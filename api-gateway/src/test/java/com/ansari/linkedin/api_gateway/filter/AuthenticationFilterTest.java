package com.ansari.linkedin.api_gateway.filter;

import com.ansari.linkedin.api_gateway.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationFilterTest {

    @Test
    void authenticationFilterAddsXUserIdHeader() throws Exception {
        // Arrange
        String secret = "01234567890123456789012345678901"; // 32+ chars
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());

        String token = Jwts.builder()
                .setSubject("123")
                .signWith(key)
                .compact();

        JwtService jwtService = new JwtService();
        // set private field jwtSecretKey via reflection
        Field f = JwtService.class.getDeclaredField("jwtSecretKey");
        f.setAccessible(true);
        f.set(jwtService, secret);

        AuthenticationFilter filter = new AuthenticationFilter(jwtService);

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("Authorization", "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CapturingGatewayFilterChain chain = new CapturingGatewayFilterChain();

        // Act
        filter.apply(new AuthenticationFilter.Config()).filter(exchange, chain).block();

        // Assert
        ServerWebExchange captured = chain.getCapturedExchange();
        assertNotNull(captured, "Chain should have been invoked with a mutated exchange");
        String forwarded = captured.getRequest().getHeaders().getFirst("X-User-Id");
        assertEquals("123", forwarded, "X-User-Id header should be forwarded with subject value");
    }

    static class CapturingGatewayFilterChain implements GatewayFilterChain {
        private ServerWebExchange capturedExchange;

        @Override
        public Mono<Void> filter(ServerWebExchange exchange) {
            this.capturedExchange = exchange;
            return Mono.empty();
        }

        public ServerWebExchange getCapturedExchange() {
            return this.capturedExchange;
        }
    }
}
