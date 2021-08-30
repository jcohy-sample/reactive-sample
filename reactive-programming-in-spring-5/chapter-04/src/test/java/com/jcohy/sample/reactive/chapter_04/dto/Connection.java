package com.jcohy.sample.reactive.chapter_04.dto;

import java.util.Arrays;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> 描述: Connection 类管理一些内部资源，并通过实现 AutoClosable 接口来通知它。.
 * Copyright: Copyright (c) 2021.
 * <a href="https://www.jcohy.com" target="_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 1.0.0 2021/8/30:15:25
 * @since 1.0.0
 */
public class Connection  implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(Connection.class);
    private final Random rnd = new Random();

    /**
     * 静态 newConnection 工厂方法始终返回 Connection 类的新实例。
     * @return /
     */
    public static Connection newConnection() {
        log.info("IO Connection created");
        return new Connection();
    }

    /**
     * 模拟 IO 操作。有时可能导致异常或返回带有有用数据的 Iterable 集合。
     * @return /
     */
    public Iterable<String> getData() {
        if (rnd.nextInt(10) < 3) {
            throw new RuntimeException("Communication error");
        }
        return Arrays.asList("Some", "data");
    }

    /**
     * close 方法可以释放内部资源， 并且应该始终被调用，即使在 getData 执行期间发生错误也是如此。
     */
    @Override
    public void close() {
        log.info("IO Connection closed");
    }
}
