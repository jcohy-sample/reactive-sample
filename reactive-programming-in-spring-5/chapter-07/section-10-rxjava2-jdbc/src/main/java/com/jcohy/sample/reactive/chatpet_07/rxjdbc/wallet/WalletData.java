package com.jcohy.sample.reactive.chatpet_07.rxjdbc.wallet;

import org.davidmoten.rx.jdbc.annotations.Column;
import org.davidmoten.rx.jdbc.annotations.Query;

/**
 * 描述: .
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/28:12:01
 * @since 2022.0.1
 */
@Query("select id, owner, balance, deposits, withdraws from wallet")
public interface WalletData {
    @Column
    Integer id();
    @Column String owner();
    @Column Integer balance();

    // Some statistics
    @Column Integer deposits();
    @Column Integer withdraws();
}
