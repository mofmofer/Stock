package com.example.stock.repository;

import com.example.stock.model.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * アカウント情報へアクセスするためのリポジトリです。
 */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    /**
     * ホールディング情報を含めてアカウント一覧を取得します。
     *
     * @return アカウント一覧
     */
    @Override
    @EntityGraph(attributePaths = "holdings")
    List<Account> findAll();
}
