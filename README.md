# 海外株式取引サービス

海外株式の取引と残高管理をモデル化した Spring Boot サービスです。REST API を提供し、アカウント作成・入出金・売買といった基本的なオペレーションを SQLite に永続化します。最小限の HTML クライアント（`/admin/index.html`）も同梱しており、API を試しやすい構成です。

## 主な機能
- シングルユーザー向けのログイン認証による利用者保護
- 取引アカウントの作成（初期入金額を指定可能）
- 米ドルの入金／出金、および買い・売り注文の執行
- 現金残高と保有銘柄のスナップショット取得
- 入出金・売買を含むトランザクション履歴の永続化

## 技術スタック
- Java 17
- Spring Boot 3（Web + Validation）
- Maven
- JUnit 5

## クイックスタート
### 前提条件
- Java 17 以上
- Maven 3.9 以上

### アプリの起動
```bash
mvn spring-boot:run
```
API は `http://localhost:8080` で利用でき、`http://localhost:8080/login.html` からサインインして簡易 UI を利用できます。

### デモ用ログイン情報
- メールアドレス: `user@example.com`
- パスワード: `trading-demo`

### テストの実行
```bash
mvn test
```

## 詳細ドキュメント
- [API リファレンス](docs/api-reference.md)
- [デプロイガイド](docs/deployment.md)
- [JUnit 整備ガイドライン](docs/junit-guidelines.md)

## 今後の拡張アイデア
- 取引履歴や約定レポートの詳細化
- 多要素認証やロールベース制御など認可の高度化
- 為替・市場データとのリアルタイム連携
- フロントエンドの拡張によるダッシュボード機能の強化

