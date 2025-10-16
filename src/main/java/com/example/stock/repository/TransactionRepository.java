package com.example.stock.repository;

import com.example.stock.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * トランザクション履歴へアクセスするためのリポジトリです。
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 指定されたアカウントのトランザクションを新しい順に取得します。
     *
     * @param accountId アカウント識別子
     * @return トランザクション一覧
     */
    List<Transaction> findByAccountIdOrderByOccurredAtDesc(UUID accountId);
}
