package com.example.stock.service;

import com.example.stock.model.AccessLog;
import com.example.stock.repository.AccessLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * アクセスログの記録および参照を提供するサービスです。
 */
@Service
public class AccessLogService {

    private static final int MAX_LIMIT = 500;

    private final AccessLogRepository repository;

    public AccessLogService(AccessLogRepository repository) {
        this.repository = repository;
    }

    /**
     * アクセスログを永続化します。
     *
     * @param log 保存対象のログ
     * @return 保存済みログ
     */
    @Transactional
    public AccessLog save(AccessLog log) {
        return repository.save(log);
    }

    /**
     * 直近のアクセスログを取得します。
     *
     * @param pageName 取得対象のページ。未指定の場合は全件
     * @param limit    最大取得件数
     * @return アクセスログの一覧
     */
    @Transactional(readOnly = true)
    public List<AccessLog> getRecentLogs(String pageName, int limit) {
        int size = Math.max(1, Math.min(limit, MAX_LIMIT));
        Pageable pageable = PageRequest.of(0, size);
        if (pageName != null && !pageName.isBlank()) {
            return repository.findByPageOrderByAccessedAtDesc(pageName, pageable);
        }
        return repository.findAllByOrderByAccessedAtDesc(pageable);
    }

    /**
     * ログに記録されているページ名を重複なしで取得します。
     *
     * @return ページ名の一覧
     */
    @Transactional(readOnly = true)
    public List<String> getRegisteredPages() {
        return repository.findDistinctPages();
    }
}

