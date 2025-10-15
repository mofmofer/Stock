package com.example.stock.web;

import com.example.stock.exception.AccountNotFoundException;
import com.example.stock.exception.InsufficientFundsException;
import com.example.stock.exception.InvalidTradeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API向けの例外ハンドリングを集約するクラスです。
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * アカウント未存在時のエラーを処理します。
     *
     * @param ex 発生した例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFound(AccountNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * ビジネスロジック起因の残高不足や不正取引のエラーを処理します。
     *
     * @param ex 発生した例外
     * @return エラーレスポンス
     */
    @ExceptionHandler({InsufficientFundsException.class, InvalidTradeException.class})
    public ResponseEntity<Map<String, Object>> handleBusiness(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * バリデーションエラーを処理します。
     *
     * @param ex 発生した例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        StringBuilder message = new StringBuilder("Validation failed: ");
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message.append(fieldError.getField()).append(" - ").append(fieldError.getDefaultMessage()).append("; ");
        }
        return buildResponse(HttpStatus.BAD_REQUEST, message.toString());
    }

    /**
     * HTTPステータスとメッセージから共通のレスポンスを生成します。
     *
     * @param status HTTPステータス
     * @param message エラーメッセージ
     * @return レスポンスエンティティ
     */
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
