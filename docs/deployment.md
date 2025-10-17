# デプロイガイド

このドキュメントでは、アプリケーションをローカル環境以外で公開・運用するための手順や考慮点をまとめています。

## GitHub Codespaces を用いた一時的なホスティング
1. GitHub リポジトリから Codespace を作成し、VS Code もしくはブラウザ版エディタで開きます。
2. ターミナルで `mvn spring-boot:run` を実行してアプリを起動します。
3. Codespaces の **Ports** タブで 8080 番ポートを **Public** に変更すると共有可能な https URL が払い出されます。例: `https://<hash>-8080.app.github.dev/index.html`。
4. 公開した URL は認証付きですが、必要に応じてアクセス権を制限し、不要になったら Codespace を停止または削除します。

## GitHub Actions + AWS App Runner への継続的デプロイ
本リポジトリにはマルチステージ Dockerfile と、ECR へのイメージ公開および App Runner へのデプロイを自動化する GitHub Actions ワークフローが含まれています。

### 1. コンテナイメージのビルド
Dockerfile のビルドステージでは `mvn -Pproduction package` を実行して JAR を生成し、ランタイムステージでは Temurin JRE 上で `java -jar /app/app.jar` を起動します。

### 2. GitHub Actions での AWS 認証準備
1. **IAM ロールの作成**: GitHub OIDC プロバイダ（`token.actions.githubusercontent.com`）を信頼するロールを新規作成し、`<owner>/<repo>` からの AssumeRole を許可する条件を追加します。ECR・App Runner へアクセス可能なポリシーをアタッチします。
2. **ロール ARN の登録**: ロールの ARN を GitHub リポジトリの Settings → *Secrets and variables* → *Actions* で `AWS_DEPLOY_ROLE_ARN` シークレットとして追加します。
3. **リポジトリ変数の設定**: 同画面の *Variables* タブで `AWS_REGION`（例: `ap-northeast-1`）と `ECR_REPOSITORY`（例: `stock-service`）を登録します。
4. **App Runner サービス ARN（任意）**: 既存サービスを利用する場合は ARN を `APP_RUNNER_SERVICE_ARN` シークレットに登録します。新規作成する場合は空のままで構いません。

### 3. ワークフローの実行
1. GitHub Actions の `Deploy to AWS App Runner` ワークフローを `main` ブランチへの push または手動実行で起動します。
2. `Configure AWS credentials` ステップが成功しない場合は、前節のシークレット／変数設定を見直します。
3. 後続ステップでは ECR リポジトリの作成（未作成の場合）、`docker build`／`docker push`、App Runner サービスの作成／更新、`aws apprunner start-deployment` によるローリングデプロイを順に実行し、最後に `aws apprunner describe-service` で `RUNNING` へ戻るまで待機します。

### 4. 公開 URL の運用
App Runner が提供する https エンドポイントは TLS 終端済みです。追加認証が必要な場合は AWS WAF + Cognito、IAM 認証付き WAF、Basic 認証リバースプロキシ（ALB + Lambda@Edge など）の導入を検討してください。

### 5. デプロイ完了の確認
- **GitHub Actions の実行履歴**: `Build and Deploy to App Runner` ワークフローが成功し、`Update App Runner service` や `Wait for App Runner deployment to complete` のステップが完了していることを確認します。
- **AWS CLI**: `aws apprunner describe-service --service-arn "$APP_RUNNER_SERVICE_ARN" --query 'Service.{Status:Status,ImageIdentifier:SourceConfiguration.ImageRepository.ImageIdentifier}' --output table` を実行し、`Status` が `RUNNING`、`ImageIdentifier` が最新タグと一致するか確認します。
- **App Runner コンソール**: 管理コンソール上で最新リビジョンのデプロイ完了を確認し、公開 URL にアクセスして API や `/index.html` が動作するかテストします。

## AWS Elastic Beanstalk / ECS (Fargate)
- Elastic Beanstalk の Java プラットフォームに `mvn package` で生成した `target/*.jar` をアップロードするだけでデプロイできます。公開 URL は `https://<環境名>.elasticbeanstalk.com/index.html` などになります。
- Dockerfile を基に Amazon ECS + Fargate のタスク定義を作成し、Application Load Balancer 経由で 8080 番ポートを公開する構成も可能です。Route53 で独自ドメインを割り当てれば社内外の利用者が同一 URL でアクセスできます。
- パブリッククラウドへ公開する場合は VPC セキュリティグループや WAF による IP 制限、TLS 設定、認証・認可の実装を行ってから運用してください。

