# 海外株式取引サービス

基本的な海外株式の取引と残高管理をモデル化した Spring Boot のスターターサービスです。REST API を公開しており、アカウントの作成、現金の入金、買い／売り注文の執行を行い、メモリ内ポートフォリオを管理します。専用のフロントエンドスタックがなくても最初の手動リクエストを試せる軽量な静的ページも同梱しています。

## 機能
- 初期入金額を指定して取引アカウントを作成
- アカウントへの米ドル入金／出金
- 海外銘柄の買い／売り注文を実行し、平均取得単価を追跡
- 現金残高と保有銘柄を含むアカウントスナップショットを取得
- ブラウザから REST 呼び出しを行える最小限の HTML クライアントを提供

## 技術スタック
- Java 17
- Spring Boot 3（Web + Validation）
- Maven（ビルドと依存関係管理）
- JUnit 5（ユニットテスト）

## はじめに

### 前提条件
- Java 17 以上
- Maven 3.9 以上

### サービスの起動
```bash
mvn spring-boot:run
```
API は `http://localhost:8080` で利用可能です。ブラウザで `http://localhost:8080/index.html` を開くと、最小限の UI を試せます。

### テストの実行
```bash
mvn test
```

## リモート／クラウド環境でのホスティング

ローカル PC の代わりにクラウド上のコンテナ環境でアプリを起動し、スマホなど外部端末からアクセスしたい場合は以下のワークフローが利用できます。

### GitHub Codespaces での一時ホスト
1. GitHub リポジトリで Codespace を作成し、VS Code またはブラウザ版エディタで開きます。
2. ターミナルで上記と同じ手順で `mvn spring-boot:run` を実行します。
3. Codespaces の Ports タブで 8080 番ポートを **Public** に変更すると、共有可能な https URL が払い出されます。スマホのブラウザからその URL（例: `https://<hash>-8080.app.github.dev/index.html`）へアクセスすると、PC と同じ UI を操作できます。
4. 公開した URL は認証付きですが、必要に応じてアクセス権を制限し、不要になったら Codespace を停止または削除します。

### GitHub Actions + コンテナレジストリ + AWS App Runner
リポジトリにはマルチステージビルドの Dockerfile と、ECR へのプッシュおよび App Runner へのデプロイを自動化する GitHub Actions ワークフローを追加しています。

1. Dockerfile は Maven ベースのビルド段階で `mvn -Pproduction package` を実行して JAR を生成し、実行段階では Temurin JRE で `java -jar /app/app.jar` を起動します。
2. `.github/workflows/deploy-app-runner.yml` を有効化するには、以下のリポジトリ変数／シークレットを設定します。
   - **Repository variables**: `AWS_REGION`（例: `ap-northeast-1`）、`ECR_REPOSITORY`（例: `stock-service`）。
   - **Secrets**: `AWS_DEPLOY_ROLE_ARN`（GitHub OIDC から Assume する IAM ロール）、`APP_RUNNER_SERVICE_ARN`（既存サービスの ARN。新規作成時は空のままでも可）。
3. ワークフローは `main` ブランチへの push または手動実行で起動し、ECR リポジトリの作成を確認した上で `docker build`／`docker push` を行います。最後に App Runner サービスへ最新イメージを指定し、`aws apprunner start-deployment` でローリングデプロイをトリガーします。
4. App Runner が提供する https エンドポイントは自動で TLS 終端されるため、スマホなど外部端末から安全にアクセスできます。追加の認証が必要な場合は AWS WAF + Cognito 認証、IAM 認証付きの Web Application Firewall、あるいは Basic 認証リバースプロキシ（ALB + Lambda@Edge など）を組み合わせてください。

### AWS Elastic Beanstalk や ECS/Fargate での常時稼働
1. Elastic Beanstalk の Java プラットフォームを選び、`mvn package` で生成した `target/*.jar` をアップロードするだけで自動デプロイできます。環境作成時に公開 URL が付与され、スマホから `https://<環境名>.elasticbeanstalk.com/index.html` にアクセスできます。
2. あるいは Dockerfile をベースに Amazon ECS + Fargate のタスク定義を作成し、Application Load Balancer 経由で 8080 ポートをインターネットに公開します。Route53 で独自ドメインを割り当てれば、社内／外部のスマホからも同じ URL で利用できます。
3. パブリッククラウドに公開する際は、VPC のセキュリティグループや WAF で IP 制限や TLS を設定し、認証／認可の実装を追加してから運用してください。

## API の概要

| メソッド | エンドポイント | 説明 |
| --- | --- | --- |
| POST | `/api/accounts` | 新しいアカウントを作成 |
| GET | `/api/accounts/{id}` | アカウントの残高と保有銘柄を取得 |
| POST | `/api/accounts/{id}/deposit` | アカウントに米ドルを入金 |
| POST | `/api/accounts/{id}/withdraw` | アカウントから米ドルを出金 |
| POST | `/api/accounts/{id}/trade` | 買いまたは売りの取引を実行 |

すべての POST エンドポイントは JSON ペイロードを受け取り、JSON を返します。検証エラーやビジネスルール違反の場合は HTTP 400、アカウントが見つからない場合は HTTP 404 を返します。

## 今後の拡張案
- アカウントをデータベースに永続化
- 認証／認可の追加
- リアルタイムの為替・市場データソースとの連携
- フロントエンドを拡張して、よりリッチなポートフォリオダッシュボードを提供
