package com.jcohy.sample.reactive.chapter_07.mongo_tx.wallet;

/**
 * 描述: 操作结果汇总.
 * <p>
 * Copyright © 2022 <a href="https://www.jcohy.com" target= "_blank">https://www.jcohy.com</a>
 *
 * @author jiac
 * @version 2022.0.1 2022/4/27:12:09
 * @since 2022.0.1
 */
public class OperationStats {

    private final int successful;

    private final int notEnoughFunds;

    private final int conflict;

    public OperationStats(int successful, int notEnoughFunds, int conflict) {
        this.successful = successful;
        this.notEnoughFunds = notEnoughFunds;
        this.conflict = conflict;
    }

    public OperationStats countTxResult(WalletService.TxResult result) {
        switch (result) {
            case SUCCESS:
                return new OperationStats(successful + 1,notEnoughFunds,conflict);
            case NOT_ENOUGH_FUNDS:
                return new OperationStats(successful,notEnoughFunds + 1, conflict);
            case TX_CONFLICT:
                return new OperationStats(successful,notEnoughFunds,conflict + 1);
            default:
                throw new RuntimeException("Unexpected status:" + result);
        }
    }

    public static OperationStats start() {
        return new OperationStats(0,0,0);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OperationStats{");
        sb.append("successful=").append(this.successful);
        sb.append(", notEnoughFunds=").append(this.notEnoughFunds);
        sb.append(", conflict=").append(this.conflict);
        sb.append('}');
        return sb.toString();
    }
}
