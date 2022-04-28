package com.jcohy.sample.chapter_06.r2dbc;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:11:15
 * @since 2022.0.1
 */
public class DatabaseLocation {

    private final String host;
    private final Integer port;
    private final String database;
    private final String user;
    private final String password;

    public DatabaseLocation(String host, Integer port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }
}
