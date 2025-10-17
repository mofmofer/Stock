package com.example.stock.repository;

import com.example.stock.model.AccessLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * アクセスログを永続化するリポジトリです。
 */
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {

    List<AccessLog> findByPageOrderByAccessedAtDesc(String page, Pageable pageable);

    List<AccessLog> findAllByOrderByAccessedAtDesc(Pageable pageable);

    @Query("select distinct l.page from AccessLog l order by l.page asc")
    List<String> findDistinctPages();
}

