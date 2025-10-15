# Foreign Stock Trading Service

A starter Spring Boot service that models basic foreign equity trading and balance management. It exposes a REST API for creating accounts, depositing cash, and executing buy/sell orders against an in-memory portfolio. A lightweight static page is bundled to make the first manual calls without a dedicated front-end stack.

## Features
- Create trading accounts with an initial cash deposit
- Deposit or withdraw USD cash from an account
- Execute buy/sell trades for international symbols with average cost tracking
- Retrieve an account snapshot showing cash and current holdings
- Minimal HTML client that issues the REST calls from the browser

## Tech Stack
- Java 17
- Spring Boot 3 (Web + Validation)
- Maven for builds and dependency management
- JUnit 5 for unit testing

## Getting Started

### Prerequisites
- Java 17 or newer
- Maven 3.9+

### Running the service
```bash
mvn spring-boot:run
```
The API will be available at `http://localhost:8080`. Open `http://localhost:8080/index.html` in a browser to try the minimal UI.

### Running tests
```bash
mvn test
```

## API Overview

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/accounts` | Create a new account |
| GET | `/api/accounts/{id}` | Retrieve account balances and holdings |
| POST | `/api/accounts/{id}/deposit` | Deposit USD into the account |
| POST | `/api/accounts/{id}/withdraw` | Withdraw USD from the account |
| POST | `/api/accounts/{id}/trade` | Execute a buy or sell trade |

All POST endpoints accept and return JSON payloads. Validation errors and business rules respond with HTTP 400; missing accounts return HTTP 404.

## Next Steps
- Persist accounts to a database
- Add authentication/authorization
- Integrate with real-time FX and market data sources
- Expand the front-end into a richer portfolio dashboard
