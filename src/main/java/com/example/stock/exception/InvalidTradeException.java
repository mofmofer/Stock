package com.example.stock.exception;

/**
 * 不正な取引要求を表す例外です。
 */
public class InvalidTradeException extends RuntimeException {
    /**
     * 不正な取引に関するメッセージを指定して例外を生成します。
     *
     * @param message エラーメッセージ
     */
    public InvalidTradeException(String message) {
        super(message);
    }
}
