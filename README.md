# Bank Card System

REST API для управления банковскими картами с аутентификацией, ролями и переводами.

## Технологии
- Java 21
- Spring Boot 3.3+
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Liquibase
- Docker
- Swagger (OpenAPI)

## Запуск локально
1. Клонировать репозиторий: `git clone <your-repo>`
2. Установить зависимости: `mvn clean install`
3. Запустить: `mvn spring-boot:run`
4. Swagger: http://localhost:8080/swagger-ui/index.html

## Запуск в Docker
1. `docker-compose up -d`
2. Приложение на http://localhost:8080
3. БД: postgres:5432, user: bank_user, pass: bank_pass, db: bank_db

## Примеры API
- Register: POST /api/auth/register { "username": "user", "password": "pass" }
- Login: POST /api/auth/login { "username": "user", "password": "pass" } → token
- Создать карту (admin): POST /api/cards { "number": "1234-5678-1234-5678", "owner": "user", "expiryDate": "2026-12-31", "balance": 1000.00 }
- Перевод: POST /api/cards/transfer { "fromCardId": 1, "toCardId": 2, "amount": 500.00 }

Используйте token в Authorization: Bearer <token>.