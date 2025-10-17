package com.example.stock.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 画面アクセスの監査ログを表すエンティティです。
 */
@Entity
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @Column(name = "id", columnDefinition = "TEXT")
    private UUID id;

    @Column(name = "page", nullable = false, length = 100)
    private String page;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @Column(name = "ip_address", nullable = false, length = 100)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "accessed_at", nullable = false)
    private Instant accessedAt;

    protected AccessLog() {
        // JPA 用のデフォルトコンストラクタ
    }

    /**
     * アクセスログを生成します。
     *
     * @param page      アクセスした画面種別
     * @param path      リクエストパス
     * @param ipAddress アクセス元 IP
     * @param userAgent ユーザーエージェント
     */
    public AccessLog(String page, String path, String ipAddress, String userAgent) {
        this.id = UUID.randomUUID();
        this.page = Objects.requireNonNull(page, "page");
        this.path = Objects.requireNonNull(path, "path");
        this.ipAddress = Objects.requireNonNull(ipAddress, "ipAddress");
        this.userAgent = userAgent;
        this.accessedAt = Instant.now();
    }

    @PrePersist
    void initialize() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (accessedAt == null) {
            accessedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getPage() {
        return page;
    }

    public String getPath() {
        return path;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getAccessedAt() {
        return accessedAt;
    }
}

