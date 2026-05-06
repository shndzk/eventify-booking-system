# Eventify — сервис бронирования билетов

Финальная работа курса «Разработка веб-приложений на Java».

## Стек
- Java 21, Spring Boot 3.4.2
- Spring Web / Security / Data JPA / Validation / Actuator
- PostgreSQL 15 + Liquibase
- JWT (jjwt 0.12)
- Lombok, springdoc-openapi (Swagger UI)
- Docker / docker-compose
- JUnit 5 + Mockito

## Архитектура
- REST-контроллеры **реализуют сгенерированные из OpenAPI интерфейсы** `AuthApi`, `EventsApi`, `BookingsApi`, `AdminApi`, `UserApi`.
- DTO — из пакета `event.app.dto` (сгенерированы OpenAPI Generator).
- Сущности БД — `app.event.entity.*`.
- Мапперы (`EventMapper`, `BookingMapper`, `NotificationMapper`, `PageableMapper`) разделяют слои.
- Безопасность — JWT, кастомный `JwtAuthFilter`, роли USER / ADMIN.
- Миграции БД — Liquibase YAML (`db/changelog/...`).

## Запуск через Docker
1. Создайте `.env` в корне (см. ниже).
2. `docker-compose up -d --build`
3. Адреса:
    - Backend — http://localhost:8080
    - Swagger UI — http://localhost:8080/swagger-ui.html
    - Frontend — http://localhost:3000

## Учётная запись по умолчанию
| Роль  | Email                  | Пароль       |
|-------|------------------------|--------------|
| ADMIN | admin@eventify.local   | Admin12345!  |

## Тесты
```bash
./gradlew test

## 🤖 Telegram-бот
Для проекта был создан и настроен бот: [@eventify_template_bot](https://t.me)  
*Примечание: Бот активен, когда запущен бэкенд приложения.*
TELEGRAM_BOT_TOKEN:8762774260:AAE63oq_EA5I6cpYvk__9ou63t9AXMuB6gQ
TELEGRAM_BOT_NAME:@eventify_template_bot



