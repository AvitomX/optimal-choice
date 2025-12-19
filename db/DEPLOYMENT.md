# Процесс развертывания базы данных

## Обзор

Процесс развертывания базы данных состоит из нескольких этапов, которые выполняются автоматически при первом запуске Docker контейнера.

## Этапы развертывания

### 1. Запуск Docker Compose

```bash
docker-compose up -d postgres
```

**Что происходит:**
- Docker Compose читает `docker-compose.yml`
- Создает/использует сеть `optimal-choice-network`
- Создает/использует том `postgres_data`
- Запускает контейнер PostgreSQL

**Переменные окружения из `.env`:**
- `POSTGRES_USER=postgres` - суперпользователь PostgreSQL
- `POSTGRES_PASSWORD=qazwsx321` - пароль суперпользователя
- `POSTGRES_DB=postgres` - база данных по умолчанию
- `POSTGRES_PORT=5432` - порт PostgreSQL

### 2. Инициализация PostgreSQL (если БД пустая)

При первом запуске PostgreSQL выполняет entrypoint скрипт, который:
1. Проверяет, инициализирована ли БД
2. Если нет - инициализирует PostgreSQL
3. Выполняет скрипты из `/docker-entrypoint-initdb.d` в алфавитном порядке

### 3. Выполнение скриптов инициализации

#### 3.1. `01-init-db.sql` - Создание схемы

**Что делает:**
- Создает схему `optch` в базе данных `postgres`
- Выдает права суперпользователю `postgres` на схему

**Используемые переменные:**
- `POSTGRES_USER` (из окружения контейнера)
- `POSTGRES_DB` (из окружения контейнера)

**Результат:**
- Схема `optch` создана
- Суперпользователь `postgres` имеет все права

#### 3.2. `02-create-user.sh` - Создание пользователя приложения (опционально)

**Что делает:**
- Создает отдельного пользователя для приложения (если нужно)
- Выдает права на базу данных и схему `optch`

**Используемые переменные:**
- `DB_APP_USER` (по умолчанию: `optimal_choice_user`)
- `DB_APP_PASSWORD` (по умолчанию: `optimal_choice_password`)
- `POSTGRES_DB` (из окружения контейнера)
- `POSTGRES_USER` (из окружения контейнера)

**Результат:**
- Пользователь `optimal_choice_user` создан (если переменные установлены)
- Пользователь имеет права на схему `optch`

**Примечание:** По умолчанию скрипт не выполняется, так как переменные `DB_APP_USER` и `DB_APP_PASSWORD` не установлены в `.env`. 
Для использования отдельного пользователя раскомментируйте эти переменные в `.env`.

### 4. Применение миграций Flyway

При запуске Spring Boot приложения Flyway автоматически применяет миграции из:
```
optimal-choice-app/src/main/resources/db/migration/postgres/
```

**Первая миграция:** `V1_0_0__create_tables.sql`
- Создает все таблицы в схеме `optch`
- Создает индексы и внешние ключи

## Текущая конфигурация

### Используемые учетные данные

**Суперпользователь PostgreSQL:**
- Пользователь: `postgres`
- Пароль: `qazwsx321` (из `.env`)
- База данных: `postgres`

**Пользователь приложения (Spring Boot):**
- Пользователь: `postgres` (тот же, что и суперпользователь)
- Пароль: `qazwsx321` (из `.env`)
- База данных: `postgres`
- Схема: `optch`

### Подключение Spring Boot

В `application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:postgres}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:qazwsx321}
  jpa:
    properties:
      hibernate:
        default_schema: optch
```

Переменные из `.env`:
- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=postgres`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=qazwsx321`

## Использование отдельного пользователя для приложения

Для повышения безопасности рекомендуется использовать отдельного пользователя для приложения.

### Шаг 1: Обновите `.env`

Раскомментируйте и установите:
```env
DB_APP_USER=optimal_choice_user
DB_APP_PASSWORD=your_secure_password_here
```

### Шаг 2: Обновите `application.yaml`

Измените:
```yaml
spring:
  datasource:
    username: ${DB_APP_USER:postgres}
    password: ${DB_APP_PASSWORD:qazwsx321}
```

### Шаг 3: Пересоздайте контейнер

```bash
docker-compose down -v
docker-compose up -d postgres
```

Скрипт `02-create-user.sh` автоматически создаст пользователя с указанными учетными данными.

## Проверка развертывания

### Проверка схемы

```bash
docker exec -it optimal-choice-postgres psql -U postgres -d postgres -c "\dn optch"
```

### Проверка таблиц

```bash
docker exec -it optimal-choice-postgres psql -U postgres -d postgres -c "\dt optch.*"
```

### Проверка пользователей

```bash
docker exec -it optimal-choice-postgres psql -U postgres -d postgres -c "\du"
```

### Проверка прав пользователя

```bash
docker exec -it optimal-choice-postgres psql -U postgres -d postgres -c "\dp optch.*"
```

## Безопасность

### Рекомендации для production:

1. **Измените все пароли по умолчанию**
   - Обновите `POSTGRES_PASSWORD` в `.env`
   - Обновите `DB_PASSWORD` в `.env`
   - Если используете отдельного пользователя, обновите `DB_APP_PASSWORD`

2. **Используйте отдельного пользователя для приложения**
   - Не используйте суперпользователя `postgres` для приложения
   - Создайте пользователя с минимальными необходимыми правами

3. **Используйте переменные окружения на сервере**
   - Не храните `.env` файл в репозитории
   - Используйте секреты Docker или переменные окружения сервера

4. **Включите SSL для подключений**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}?ssl=true&sslmode=require
   ```

## Troubleshooting

### Проблема: Скрипты не выполняются

**Решение:**
- Убедитесь, что БД пустая (удалите том: `docker-compose down -v`)
- Проверьте логи: `docker logs optimal-choice-postgres`
- Убедитесь, что файлы в `db/init/` имеют правильные имена и формат

### Проблема: Ошибка прав доступа

**Решение:**
- Проверьте, что схема `optch` существует
- Проверьте права пользователя: `\dp optch.*`
- Выполните вручную: `GRANT ALL PRIVILEGES ON SCHEMA optch TO postgres;`

### Проблема: Приложение не подключается

**Решение:**
- Проверьте переменные в `.env`
- Проверьте, что контейнер запущен: `docker ps`
- Проверьте логи приложения
- Проверьте подключение: `docker exec -it optimal-choice-postgres psql -U postgres -d postgres`

