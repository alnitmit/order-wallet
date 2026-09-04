# Микросервисная система E-Commerce и Биллинга

Распределенная событийно-ориентированная платформа электронной коммерции, реализующая безопасное списание средств с виртуального кошелька, обработку заказов и отказоустойчивую асинхронную коммуникацию через Apache Kafka с применением паттернов Transactional Outbox и Inbox.

## Что делает проект

- обрабатывает заказы товаров и рассчитывает итоговые суммы
- обеспечивает асинхронную обработку платежей и финансовых транзакций
- реализует паттерн Transactional Outbox для предотвращения проблемы двойной записи
- реализует паттерн Inbox для обеспечения идемпотентности и защиты от дублирования сообщений
- предоставляет REST API для управления заказами и платежами
- использует PostgreSQL c миграциями через Flyway
- объединяет инфраструктуру с помощью Docker Compose
- использует интеграцию API Gateway для централизованной маршрутизации, rate limiting

## Технологический стек

### Бэкенд

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Apache Kafka
- PostgreSQL
- Hibernate 6
- Flyway
- Maven

### DevOps

- Docker
- Docker Compose
- Git

## Структура проекта

```text
root/
|-- order-service/
|   |-- src/main/java/com/petproject/order_service/
|   |   |-- controller/
|   |   |-- dto/
|   |   |-- entity/
|   |   |-- listener/
|   |   |-- repository/
|   |   |-- scheduler/
|   |   |-- service/
|   |-- src/main/resources/
|   |   |-- db/migration/
|   |   `-- application.yaml
|   `-- Dockerfile
|-- payment-service/
|   |-- src/main/java/com/petproject/payment_service/
|   |   |-- dto/
|   |   |-- entity/
|   |   |-- listener/
|   |   |-- repository/
|   |   |-- scheduler/
|   |   |-- service/
|   |-- src/main/resources/
|   |   |-- db/migration/
|   |   `-- application.yaml
|   `-- Dockerfile
|-- docker-compose.yml
-- README.md
```

## Запуск локально с помощью Docker

Из корневой директории проекта:

```bash
docker compose up -d --build
```


Остановка контейнеров:

```bash
docker compose down
```

Если нужно пересобрать отдельный сервис без использования Docker cache:

```bash
docker compose build --no-cache order-service   
docker compose up -d order-service
```

## Запуск локально без Docker

### Order Service

Сначала запустите PostgreSQL и Kafka, затем выполните:

#### Bash

```bash
cd order-service
./mvnw spring-boot:run
```

### Payment Service

Сначала запустите PostgreSQL и Kafka, затем выполните:

#### Bash

```bash
cd payment-service
./mvnw spring-boot:run
```

## Сервисы Docker

docker-compose.yml запускает инфраструктуру:

- postgres
- kafka
- redis
- order-service
- payment-service