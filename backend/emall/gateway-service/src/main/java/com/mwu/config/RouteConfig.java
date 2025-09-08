package com.mwu.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class RouteConfig {


    @Bean
    public RedisRateLimiter redisRateLimiter() {

        return new RedisRateLimiter(1,1,1);
//        假设你设置 new RedisRateLimiter(2, 5, 10)：
//        第一个参数 2：每秒最多生成 2 个令牌，意味着每秒最多允许 2 次请求通过。
//        第二个参数 5：令牌桶容量为 5，最多可以累积 5 个令牌。如果一段时间没有请求，令牌会累积到最多 5 个。
//        第三个参数 10：突发请求最大令牌数为 10，允许短时间内最多有 10 个请求通过（如果令牌桶里有足够的令牌）。
//        举例：
//        如果某用户 5 秒内没有请求，令牌桶会积攒到 5 个令牌。第 6 秒突然来了 7 个请求，前 5 个会被允许通过，后 2 个会被限流拒绝。
//        如果你把突发令牌数设为 10，则最多可以同时通过 10 个请求，适合应对偶发的高并发场景。


    }

    @Bean
    KeyResolver userKeyResolver() {
        return exchange -> Mono.justOrEmpty(exchange
                        .getRequest()
                        .getHeaders()
                        .getFirst("user"))
                .defaultIfEmpty("anonymous");
    }


    @Bean
    public RouteLocator emallocRouteConfig(RouteLocatorBuilder routeLocatorBuilder) {

        return routeLocatorBuilder.routes()
                .route(p -> p
                        .path("/eshoppingzone/user-service/**")
                        .filters(f -> f.rewritePath("/eshoppingzone/user-service/(?<segment>.*)", "/${segment}")
                                // how much time a particular request is sent and received can be monitored

                                //								举例说明：
//								前端请求路径：
//								/eshoppingzone/user-service/profile/info
//								匹配过程：
//								/eshoppingzone/user-service/ 匹配前缀
//								profile/info 被正则表达式 (?<segment>.*) 捕获为 segment
//								重写后路径：
//								/profile/info
//								也就是只保留后面的 segment 部分，去掉了 /eshoppingzone/user-service/ 前缀。
//								作用：
//								这样后端 USER-SERVICE 微服务只需要处理 /profile/info，而不需要关心网关前缀，简化了服务的路由和接口设计

                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
//                                详细解释如下：
//                                .circuitBreaker(config -> config.setName("userCircuitBreaker")
//                                        设置断路器的名称为 userCircuitBreaker，用于标识和管理该断路器。
//                                .setFallbackUri("forward:/contactSupport")
//                        当后端 USER-SERVICE 服务不可用或响应超时时，断路器会自动触发，网关会将请求转发到 /contactSupport 路径（通常是一个本地处理逻辑或兜底页面），而不是直接返回错误。
//                        举例说明：
//                        用户请求 /eshoppingzone/user-service/profile/info。
//                        网关尝试将请求转发到 USER-SERVICE 微服务。
//                        如果 USER-SERVICE 正常，返回实际结果。
//                        如果 USER-SERVICE 超时或不可用，断路器触发，网关自动将请求转发到 /contactSupport，返回兜底响应（如提示“服务暂不可用，请联系客服”）。
//                        这样可以提升系统的稳定性和用户体验，避免后端服务故障时直接暴露错误。

                                .circuitBreaker(config -> config.setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/contactSupport")))

                        .uri("lb://USER-SERVICE"))
                .route(p -> p
                        .path("/eshoppingzone/product-service/**")
                        .filters(f -> f.rewritePath("/eshoppingzone/product-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())

//                                首先，setRetries(3) 表示当请求失败时，最多会重试 3 次。这适用于网络波动或后端服务短暂不可用的场景，从而增加成功的可能性。
//                                接着，setMethods(HttpMethod.GET) 限定了重试机制仅适用于 HTTP GET 请求。这是因为 GET 请求通常是幂等的，重试不会导致副作用。
//                                最后，setBackoff(Duration.ofMillis(1000), Duration.ofMillis(5000), 2, true) 定义了退避策略。初始延迟为 1000 毫秒，最大延迟为 5000 毫秒，延迟时间以倍数 2 增长，并启用了抖动（true）。抖动可以避免多个请求同时重试导致的拥塞问题。
//                                整体而言，这段代码通过配置重试次数、请求方法和退避策略，确保在服务短暂故障时，客户端请求能够更可靠地完成。
                                .retry(retryconfig -> retryconfig.setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(5000),2,true)))
                        .uri("lb://PRODUCT-SERVICE"))
                .route(p -> p
                        .path("/eshoppingzone/image-service/**")
                        .filters(f -> f.rewritePath("/eshoppingzone/image-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://IMAGE-SERVICE"))
                .route(p -> p
                        .path("/eshoppingzone/cart-service/**")
                        .filters(f -> f.rewritePath("/eshoppingzone/cart-service/(?<segment>.*)", "/${segment}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()).setKeyResolver(userKeyResolver())))
    //                    首先，.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()).setKeyResolver(userKeyResolver())) 配置了基于 Redis 的限流器。setRateLimiter(redisRateLimiter()) 使用了一个 RedisRateLimiter Bean，该限流器通过令牌桶算法控制请求速率。具体的限流策略由 RedisRateLimiter 的参数决定，例如每秒生成的令牌数、令牌桶容量等。
    //                    接着，setKeyResolver(userKeyResolver()) 指定了限流的键解析器。这里的 userKeyResolver 是一个 KeyResolver Bean，它从请求头中提取 user 字段作为限流的唯一标识。如果请求头中没有 user 字段，则默认使用 "anonymous"。这确保了限流规则可以基于用户进行区分，从而避免不同用户之间的请求互相干扰。
    //                    最后，.uri("lb://CART-SERVICE") 指定了目标服务的地址，lb:// 表示通过服务发现机制（如 Eureka）动态解析服务实例。
    //                    整体而言，这段代码通过结合 Redis 限流器和用户键解析器，为 CART-SERVICE 提供了一个高效且灵活的请求限流方案，既能保护服务稳定性，又能确保用户请求的公平性。

                        .uri("lb://CART-SERVICE")).build();
}
}
