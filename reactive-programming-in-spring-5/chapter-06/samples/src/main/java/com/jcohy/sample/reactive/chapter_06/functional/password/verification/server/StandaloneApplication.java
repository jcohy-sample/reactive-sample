package com.jcohy.sample.reactive.chapter_06.functional.password.verification.server;

import com.jcohy.sample.reactive.chapter_06.functional.password.verification.client.PasswordDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p> 描述: 新的函数式Web 框架允许我们在不启动整个Spring 基础设施的情况下构建 Web 应用程序.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/31:13:03
 * @since 1.0.0
 */
public class StandaloneApplication {
    static Logger LOGGER = LoggerFactory.getLogger(StandaloneApplication.class);

    public static void main(String... args) {
        long start = System.currentTimeMillis();

        // 我们调用 routes 方法，然后将 RouterFunction 转换为 HttpHandler。
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(routes(
                new BCryptPasswordEncoder(18)
        ));

        // 我们使用名为 ReactorHttpHandlerAdapter 的内置 HttpHandler 适配器。
        ReactorHttpHandlerAdapter httpHandlerAdapter = new ReactorHttpHandlerAdapter(httpHandler);

        // 我们创建了一个 HttpServer 实例，它是 Reactor-Netty API 的一部分。
        DisposableServer server = HttpServer.create()
                .host("localhost")
                .port(8080)
                .handle(httpHandlerAdapter)
                .bindNow();

        LOGGER.debug("Started in " + (System.currentTimeMillis() - start) + " ms");

        //最后，为了使应用程序保持活动状态， 我们阻塞主 Thread 并监听所创建服务器的处理事件。
        server.onDispose()
                .block();
    }

    /**
     * routes 方法的声明
     * 这是路由映射逻辑， 它使用 /password 路径处理任何 POST 方法的请求。在这里，我们首先
     * 在 bodyToMono 方法的支持下映射传人的请求。然后， 一旦请求主体被转换， 我们就使用
     * PasswordEncoder 实例来检查己加密密码的原始密码(在该示例中，我们使用强大的 BCrypt
     * 算法进行 18 轮散列，这可能需要几秒钟来编码/匹配)。最后，如果密码与存储的密码匹配，
     * 则 ServerResponse 将返回 OK 状态(200); 如果密码与蒋储的密码不匹配，则返回的状态将为 EXPECTATION_FAILED(417) 。
     *
     * @param encoder encoder
     * @return /
     */
    public static RouterFunction<ServerResponse> routes(PasswordEncoder encoder) {
        return RouterFunctions
                .route(RequestPredicates.POST("/password"),
                        request -> request
                                .bodyToMono(PasswordDTO.class)
                                .map(p -> encoder.matches(p.getRaw(), p.getSecured()))
                                .flatMap(isMasched -> isMasched
                                        ? ServerResponse.ok().build()
                                        : ServerResponse.status(HttpStatus.EXPECTATION_FAILED).build()));
    }
}
